package com.example.securemessengerapp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class UserProfileActivity extends AppCompatActivity {

    private TextView userNameText;
    private Button chatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        userNameText = findViewById(R.id.userNameText);
        chatButton = findViewById(R.id.chatButton);

        // Get the username from the search and display it
        String userName = getIntent().getStringExtra("username");
        userNameText.setText(userName);

        // Set up the chat button
        chatButton.setOnClickListener(v -> {
            // Open chat window with the selected user
            Intent intent = new Intent(UserProfileActivity.this, ChatWindowActivity.class);
            intent.putExtra("recipientUsername", userName);  // Pass username for chat
            startActivity(intent);
        });
    }
}
