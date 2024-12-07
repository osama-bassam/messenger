package com.example.securemessengerapp2;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button loginButton, signUpButton;
    private DatabaseReference databaseReference;
    private String savedUsername;  // Global variable for saved username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link UI elements
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Usernames");

        // Get saved username (if any)
        savedUsername = getSavedUsername();

        // Check if there's a saved username for auto-login
        if (savedUsername != null) {
            // Automatically log the user in and navigate to the next screen
            navigateToChatActivity(savedUsername);
        } else {
            // Show the login screen if no username is found
            showLoginScreen();
        }

        // Sign Up Button functionality
        signUpButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            } else {
                // Check if the username exists in Firebase
                databaseReference.child(username).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            // If username exists, show an error
                            Toast.makeText(MainActivity.this, "Username already exists!", Toast.LENGTH_SHORT).show();
                        } else {
                            // If username doesn't exist, create a new entry in the database
                            databaseReference.child(username).child("password").setValue(password)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(MainActivity.this, "Sign-Up successful! Please log in.", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(MainActivity.this, "Sign-Up failed!", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Handle the error if something goes wrong
                        Toast.makeText(MainActivity.this, "Error checking username", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Login Button functionality
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            } else {
                // Check if the username exists and validate password
                databaseReference.child(username).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        String savedPassword = task.getResult().child("password").getValue(String.class);
                        if (savedPassword != null && savedPassword.equals(password)) {
                            Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                            // Save the username to SharedPreferences for auto-login next time
                            saveLoginInfo(username);

                            // Navigate to ChatActivity
                            navigateToChatActivity(username);
                        } else {
                            Toast.makeText(MainActivity.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Username does not exist!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void saveLoginInfo(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);  // Save the username
        editor.apply();  // Apply the changes
    }

    private String getSavedUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("username", null);  // Return null if not found
    }

    private void clearLoginInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.apply();  // Clear stored data
    }

    private void navigateToChatActivity(String username) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();  // Close the login activity
    }

    private void showLoginScreen() {
        // You can implement logic here to show the login UI
        // The default screen is already the login screen in your layout
    }
}