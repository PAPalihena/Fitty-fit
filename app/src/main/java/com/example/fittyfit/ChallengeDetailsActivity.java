package com.example.fittyfit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.example.fittyfit.models.Challenge;
import com.example.fittyfit.utils.FirebaseDatabaseHelper;

public class ChallengeDetailsActivity extends BaseActivity {
    private FirebaseDatabaseHelper firebaseHelper;
    private String currentUserId;
    private String challengeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_details);

        // Setup navigation bar
        setupNavigationBar();

        // Initialize Firebase
        firebaseHelper = new FirebaseDatabaseHelper();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please sign in to view challenges", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        currentUserId = auth.getCurrentUser().getUid();
        
        // Get challenge ID from intent
        challengeId = getIntent().getStringExtra("challenge_id");
        if (challengeId == null || challengeId.isEmpty()) {
            Toast.makeText(this, "Invalid challenge", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load challenge details
        loadChallengeDetails();
    }

    private void loadChallengeDetails() {
        firebaseHelper.getChallengeDetails(challengeId, new kotlin.jvm.functions.Function1<Challenge, kotlin.Unit>() {
            @Override
            public kotlin.Unit invoke(Challenge challenge) {
                if (challenge == null) {
                    Toast.makeText(ChallengeDetailsActivity.this, "Challenge not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return kotlin.Unit.INSTANCE;
                }

                try {
                    // Initialize views
                    TextView titleTextView = findViewById(R.id.challengeTitle);
                    TextView targetTextView = findViewById(R.id.challengeTarget);
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    TextView progressText = findViewById(R.id.progressText);
                    FloatingActionButton chatButton = findViewById(R.id.chatButton);
                    Toolbar toolbar = findViewById(R.id.toolbar);

                    // Set challenge data
                    if (titleTextView != null) {
                        titleTextView.setText(challenge.getTitle());
                    }
                    if (targetTextView != null) {
                        targetTextView.setText(challenge.getTarget());
                    }
                    if (progressBar != null) {
                        progressBar.setProgress(challenge.getProgress());
                    }
                    if (progressText != null) {
                        progressText.setText(challenge.getProgress() + "% completed");
                    }

                    // Setup toolbar
                    if (toolbar != null) {
                        setSupportActionBar(toolbar);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        toolbar.setNavigationOnClickListener(v -> onBackPressed());
                    }

                    // Setup chat button
                    if (chatButton != null) {
                        chatButton.setOnClickListener(v -> {
                            Intent intent = new Intent(ChallengeDetailsActivity.this, ChallengeChatActivity.class);
                            intent.putExtra("challenge_id", challengeId);
                            startActivity(intent);
                        });
                    }
                } catch (Exception e) {
                    Toast.makeText(ChallengeDetailsActivity.this, "Error loading challenge details", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                return kotlin.Unit.INSTANCE;
            }
        });
    }
} 