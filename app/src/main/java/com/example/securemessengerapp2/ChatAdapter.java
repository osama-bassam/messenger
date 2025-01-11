package com.example.securemessengerapp2;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<Map<String, Object>> chatMessages;
    private String currentUsername;

    public ChatAdapter(List<Map<String, Object>> chatMessages, String currentUsername) {
        this.chatMessages = chatMessages;
        this.currentUsername = currentUsername;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new LinearLayout to hold the message and timestamp
        LinearLayout layout = new LinearLayout(parent.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(10, 5, 10, 5); // Add some padding to the layout

        // Create a TextView to display the message
        TextView messageText = new TextView(parent.getContext());
        messageText.setPadding(20, 20, 20, 20);  // Padding for the message bubble
        messageText.setTextSize(16f);  // Text size
        messageText.setMaxWidth(parent.getWidth() - 100); // Limit width of the bubble

        // Set up layout parameters for the messageText
        LinearLayout.LayoutParams messageLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        messageLayoutParams.setMargins(10, 5, 10, 5);  // Set margins for the message bubble
        messageText.setLayoutParams(messageLayoutParams);

        // Create a TextView for the timestamp
        TextView timestampText = new TextView(parent.getContext());
        timestampText.setPadding(10, 5, 10, 5);  // Padding for the timestamp
        timestampText.setTextSize(12f);  // Smaller text size for the timestamp
        timestampText.setTextColor(Color.GRAY);  // Set timestamp text color

        // Set up layout parameters for the timestampText
        LinearLayout.LayoutParams timestampLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        timestampLayoutParams.setMargins(10, 2, 10, 5);  // Set margins for the timestamp
        timestampText.setLayoutParams(timestampLayoutParams);

        // Add the message and timestamp TextViews to the layout
        layout.addView(messageText);
        layout.addView(timestampText);  // Add timestamp below the message

        return new ChatViewHolder(layout, messageText, timestampText);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Map<String, Object> messageData = chatMessages.get(position);
        String message = (String) messageData.get("message");
        long timestamp = (long) messageData.get("timestamp");  // Assuming the timestamp is stored as a long value

        // Set the message text
        holder.messageText.setText(message != null ? message : "Unknown message");

        // Set the timestamp text
        String formattedTimestamp = formatTimestamp(timestamp);
        holder.timestampText.setText(formattedTimestamp);

        // Apply the same background color for all messages
        holder.messageText.setBackgroundResource(R.drawable.message_bubble_default);

        // Align all messages to the left (or right, if you prefer)
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.messageText.getLayoutParams();
        params.gravity = Gravity.START;  // Align left for all messages
        holder.messageText.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    private String formatTimestamp(long timestamp) {
        // Format the timestamp into a readable string (e.g., "12:30 PM")
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timestampText;

        public ChatViewHolder(View itemView, TextView messageText, TextView timestampText) {
            super(itemView);
            this.messageText = messageText;
            this.timestampText = timestampText;  // Initialize the timestamp view
        }
    }
}
