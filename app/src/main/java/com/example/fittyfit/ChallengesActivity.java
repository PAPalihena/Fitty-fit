package com.example.fittyfit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.example.fittyfit.models.Challenge;
import com.example.fittyfit.utils.FirebaseDatabaseHelper;
import java.util.List;

public class ChallengesActivity extends BaseActivity {
    private LinearProgressIndicator[] personalChallengeProgress;
    private LinearProgressIndicator[] groupChallengeProgress;
    private FirebaseDatabaseHelper firebaseHelper;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        // Setup navigation bar
        setupNavigationBar();

        // Initialize Firebase
        firebaseHelper = new FirebaseDatabaseHelper();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            finish();
            return;
        }
        currentUserId = auth.getCurrentUser().getUid();

        // Initialize views
        initializeViews();

        // Load challenges
        loadChallenges();

        // Setup button click listeners
        setupButtonListeners();
    }

    private void initializeViews() {
        // Initialize progress indicators arrays
        personalChallengeProgress = new LinearProgressIndicator[] {
            findViewById(R.id.personalChallengeProgress1),
            findViewById(R.id.personalChallengeProgress2),
            findViewById(R.id.personalChallengeProgress3)
        };

        groupChallengeProgress = new LinearProgressIndicator[] {
            findViewById(R.id.groupChallengeProgress1),
            findViewById(R.id.groupChallengeProgress2),
            findViewById(R.id.groupChallengeProgress3)
        };

        // Set level descriptions
        ((TextView) findViewById(R.id.personalLevelDesc)).setText("Fitness Warrior");
        ((TextView) findViewById(R.id.groupLevelDesc)).setText("Team Player");
    }

    private void loadChallenges() {
        // Load personal challenges
        firebaseHelper.getUserChallenges(currentUserId, challenges -> {
            int personalIndex = 0;
            int groupIndex = 0;

            for (Challenge challenge : challenges) {
                if (challenge.getType().equals("personal") && personalIndex < personalChallengeProgress.length) {
                    updateChallengeCard(
                        personalIndex,
                        challenge,
                        personalChallengeProgress,
                        new int[] {R.id.personalChallengeDesc1, R.id.personalChallengeDesc2, R.id.personalChallengeDesc3}
                    );
                    personalIndex++;
                } else if (challenge.getType().equals("group") && groupIndex < groupChallengeProgress.length) {
                    updateChallengeCard(
                        groupIndex,
                        challenge,
                        groupChallengeProgress,
                        new int[] {R.id.groupChallengeDesc1, R.id.groupChallengeDesc2, R.id.groupChallengeDesc3}
                    );
                    groupIndex++;
                }
            }
            return kotlin.Unit.INSTANCE;
        });
    }

    private void updateChallengeCard(int index, Challenge challenge, LinearProgressIndicator[] progressBars, int[] descIds) {
        if (index < progressBars.length && index < descIds.length) {
            TextView descView = findViewById(descIds[index]);
            if (descView != null) {
                descView.setText(challenge.getTitle());
            }
            if (progressBars[index] != null) {
                progressBars[index].setProgress(challenge.getProgress());
            }
        }
    }

    private void setupButtonListeners() {
        MaterialButton createChallengeButton = findViewById(R.id.createChallengeButton);
        if (createChallengeButton != null) {
            createChallengeButton.setOnClickListener(v -> {
                // TODO: Implement create challenge functionality
                // Intent intent = new Intent(this, CreateChallengeActivity.class);
                // startActivity(intent);
            });
        }
    }
} 