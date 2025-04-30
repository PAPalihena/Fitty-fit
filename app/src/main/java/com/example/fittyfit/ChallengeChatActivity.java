package com.example.fittyfit;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class ChallengeChatActivity extends BaseActivity {
    private String challengeId;
    private String currentUserId;
    private DatabaseReference chatRef;
    private EditText messageInput;
    private ImageButton sendButton;
    private RecyclerView chatRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_chat);

        // Setup navigation bar
        setupNavigationBar();

        // Get challenge ID from intent
        challengeId = getIntent().getStringExtra("challenge_id");
        if (challengeId == null || challengeId.isEmpty()) {
            finish();
            return;
        }

        // Initialize Firebase
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatRef = FirebaseDatabase.getInstance().getReference("challenge_chats").child(challengeId);

        // Initialize views
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Setup toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        // Setup chat recycler view
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Setup chat adapter

        // Setup send button
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            String messageId = chatRef.push().getKey();
            if (messageId != null) {
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("message", message);
                messageMap.put("sender", currentUserId);
                messageMap.put("timestamp", System.currentTimeMillis());

                chatRef.child(messageId).setValue(messageMap)
                    .addOnSuccessListener(aVoid -> messageInput.setText(""))
                    .addOnFailureListener(e -> {
                        // Handle error
                    });
            }
        }
    }
} 