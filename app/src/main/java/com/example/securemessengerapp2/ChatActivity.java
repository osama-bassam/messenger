package com.example.securemessengerapp2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;


public class ChatActivity extends AppCompatActivity {

    private EditText searchFriendInput;
    private Button searchButton;
    private ChatAdapter chatAdapter;
    private String currentUsername;  // This should be dynamically fetched
    private DatabaseReference usersReference;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Firebase Realtime Database references
        usersReference = FirebaseDatabase.getInstance().getReference("Usernames");

        // Link UI elements
        searchFriendInput = findViewById(R.id.searchFriendInput);
        searchButton = findViewById(R.id.searchButton);

        // Set up Drawer Layout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

//This is a utility class that manages the opening
// and closing of a navigation drawer with the help of the action bar (hamburger icon)
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.openDrawer, R.string.closeDrawer);
// It ensures that the ActionBarDrawerToggle is notified whenever the drawer is opened or closed,
// and updates the action bar accordingly (e.g., switching between hamburger and back icon).
        drawerLayout.addDrawerListener(toggle);
// . Synchronize the state of the toggle: For example, if the drawer is open, the toggle will show the
// back arrow, and if the drawer is closed, it will show the hamburger icon.
        toggle.syncState();

 //The button will appear as a back button when the drawer is open, allowing the user to close the drawer.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Handle drawer item clicks (logout, etc.)
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                clearLoginInfo(); // Clear saved login info
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
                finish();  // Close the ChatActivity
            }
            drawerLayout.closeDrawers();  // Close the drawer after clicking an item
            return true;//Returning true indicates that the item click event
                       // has been successfully handled and that no further processing is needed
        });

        // Set up search for friends when search button is pressed
        searchButton.setOnClickListener(v -> {
            String username = searchFriendInput.getText().toString().trim();
            if (!username.isEmpty()) {
                searchForFriends(username); // Trigger search
            } else {
                Toast.makeText(ChatActivity.this, "Please enter a username to search", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the Enter key functionality on search input
        searchFriendInput.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String username = searchFriendInput.getText().toString().trim();
                if (!username.isEmpty()) {
                    searchForFriends(username); // Trigger search
                } else {
                    Toast.makeText(ChatActivity.this, "Please enter a username to search", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
    }
//This method is responsible for managing item selections, especially the ones related to the drawer toggle
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to search for friends
    private void searchForFriends(String username) {
        Query query = usersReference.orderByKey().equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username found, show user profile
                    Intent intent = new Intent(ChatActivity.this, UserProfileActivity.class);
                    intent.putExtra("username", username);  // Pass the found username
                    startActivity(intent);
                } else {
                    Toast.makeText(ChatActivity.this, "No user found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Error searching users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to clear login info (for logout)
    private void clearLoginInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.apply();  // Clear stored data
    }
}
