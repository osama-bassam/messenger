package com.example.securemessengerapp2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatWindowActivity extends AppCompatActivity {

    private TextView recipientName;
    private EditText messageInput;
    private Button sendButton;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Map<String, Object>> chatMessages = new ArrayList<>();
    private String recipientUsername;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        // Link UI elements
        recipientName = findViewById(R.id.recipientName);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        // Get recipient's username from the intent
        recipientUsername = getIntent().getStringExtra("recipientUsername");
        recipientName.setText("Chatting with: " + recipientUsername);

        // Set up RecyclerView
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the current username from SharedPreferences
        currentUsername = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getString("username", null);

        if (currentUsername == null) {
            Toast.makeText(ChatWindowActivity.this, "Please login again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ChatWindowActivity.this, MainActivity.class));
            finish();
            return;
        }

        // Initialize ChatAdapter with chatMessages list and currentUsername
        chatAdapter = new ChatAdapter(chatMessages, currentUsername);
        chatRecyclerView.setAdapter(chatAdapter);

        // Load previous messages
        loadMessages();

        // Send button logic
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();

        if (!messageText.isEmpty()) {
            long timestamp = System.currentTimeMillis();

            if (currentUsername == null) {
                Toast.makeText(ChatWindowActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> messageData = new HashMap<>();
            messageData.put("message", messageText);
            messageData.put("timestamp", timestamp);

            String senderChatNode = currentUsername + "_chats";
            String receiverChatNode = recipientUsername + "_chats";

            DatabaseReference senderChatRef = FirebaseDatabase.getInstance()
                    .getReference("Usernames")
                    .child(currentUsername)
                    .child(senderChatNode)
                    .child(recipientUsername)
                    .child("chat_messages");

            DatabaseReference receiverChatRef = FirebaseDatabase.getInstance()
                    .getReference("Usernames")
                    .child(recipientUsername)
                    .child(receiverChatNode)
                    .child(currentUsername)
                    .child("chat_messages");

            senderChatRef.push().setValue(messageData).addOnSuccessListener(aVoid -> {
                receiverChatRef.push().setValue(messageData).addOnSuccessListener(aVoid1 -> {
                    messageInput.setText(""); // Clear input field after sending
                    Log.d("ChatWindowActivity", "Message sent successfully!");
                }).addOnFailureListener(e -> {
                    Toast.makeText(ChatWindowActivity.this, "Failed to send message to receiver: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ChatWindowActivity", "Receiver failure", e);
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(ChatWindowActivity.this, "Failed to send message to sender: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ChatWindowActivity", "Sender failure", e);
            });
        } else {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMessages() {
        if (currentUsername == null) {
            Toast.makeText(ChatWindowActivity.this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        String senderChatNode = currentUsername + "_chats";

        DatabaseReference chatReference = FirebaseDatabase.getInstance()
                .getReference("Usernames")
                .child(currentUsername)
                .child(senderChatNode)
                .child(recipientUsername)
                .child("chat_messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatMessages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> messageData = (Map<String, Object>) snapshot.getValue();
                    if (messageData != null) {
                        chatMessages.add(messageData);
                    }
                }
                chatAdapter.notifyDataSetChanged();  // Notify the adapter to refresh the UI
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatWindowActivity.this, "Failed to load messages: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
