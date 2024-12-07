package com.example.securemessengerapp2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    private EditText searchFriendInput, chatInput;
    private Button sendMessageButton, addFriendButton, startChatButton;
    private RecyclerView chatListRecyclerView;
    private ChatAdapter chatAdapter;
    private String foundUsername; // Holds the username that was found
    private DatabaseReference friendsReference, usersReference, chatReference;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Firebase Realtime Database references
        usersReference = FirebaseDatabase.getInstance().getReference("Usernames");
        chatReference = FirebaseDatabase.getInstance().getReference("Chats");
        friendsReference = FirebaseDatabase.getInstance().getReference("Friends");

        // Link UI elements
        searchFriendInput = findViewById(R.id.searchFriendInput);
        chatInput = findViewById(R.id.chatInput);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        addFriendButton = findViewById(R.id.addFriendButton);
        startChatButton = findViewById(R.id.sendMessageButton);
        chatListRecyclerView = findViewById(R.id.chatListRecyclerView);

        // Set up RecyclerView
        chatListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter();  // Assume this adapter is created
        chatListRecyclerView.setAdapter(chatAdapter);

        // Set up the Drawer Layout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Set up the ActionBarDrawerToggle to open/close the drawer
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.openDrawer, R.string.closeDrawer
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set the ActionBar to show the hamburger icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Show the hamburger icon in the action bar

        // Handle drawer item clicks (Logout, etc.)
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                clearLoginInfo(); // Clear saved login info
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
                finish();  // Close the ChatActivity
            }
            drawerLayout.closeDrawers();  // Close the drawer after clicking an item
            return true;
        });

        // Set up search for friends
        searchFriendInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                searchForFriends(charSequence.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });

        // Add friend button functionality
        addFriendButton.setOnClickListener(v -> {
            if (foundUsername != null) {
                String currentUser = "osama"; // Replace with the actual current user's username

                // Add the friendship relationship in Firebase
                friendsReference.child(currentUser).child(foundUsername).setValue(true)
                        .addOnSuccessListener(aVoid -> {
                            // Also add the reverse relationship
                            friendsReference.child(foundUsername).child(currentUser).setValue(true)
                                    .addOnSuccessListener(aVoid2 -> {
                                        Toast.makeText(ChatActivity.this, foundUsername + " added to your friend list!", Toast.LENGTH_SHORT).show();
                                        addFriendButton.setVisibility(View.GONE);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ChatActivity.this, "Failed to add reverse relationship: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ChatActivity.this, "Failed to add friend: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(ChatActivity.this, "No user selected", Toast.LENGTH_SHORT).show();
            }
        });

        // Start chat button functionality
        startChatButton.setOnClickListener(v -> {
            if (foundUsername != null) {
                // Log to check the recipientUsername
                Log.d("ChatActivity", "Recipient Username: " + foundUsername);

                // Navigate to the Chat Window (ChatWindowActivity)
                Intent intent = new Intent(ChatActivity.this, ChatWindowActivity.class);
                intent.putExtra("recipientUsername", foundUsername);  // Pass username for chat
                startActivity(intent);
            } else {
                Toast.makeText(ChatActivity.this, "No user selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Search for friends
    private void searchForFriends(String username) {
        Query query = usersReference.orderByKey().equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username found, show user profile
                    foundUsername = username;
                    Intent intent = new Intent(ChatActivity.this, UserProfileActivity.class);
                    intent.putExtra("username", foundUsername);  // Pass the found username
                    startActivity(intent);
                } else {
                    // No user found
                    foundUsername = null;
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

