package com.example.securemessengerapp2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ChatWindowActivity extends AppCompatActivity {

    private TextView recipientName;  // Declare once at the class level
    private String recipientUsername; // Declare once

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        recipientName = findViewById(R.id.recipientName);

        // Get the recipient's username from the intent
        recipientUsername = getIntent().getStringExtra("recipientUsername");

        if (recipientUsername != null) {
            recipientName.setText("Chatting with: " + recipientUsername);
        } else {
            Log.e("ChatWindowActivity", "Recipient Username is null");
            // Handle the case when recipientUsername is null (e.g., show an error message or navigate back)
            // You can call finish() to go back to the previous activity if needed
        }




        // Link UI elements
        recipientName = findViewById(R.id.recipientName);
        recipientName.setText("Chatting with: " + recipientUsername);

        // TODO: Implement chat functionality
    }
}
