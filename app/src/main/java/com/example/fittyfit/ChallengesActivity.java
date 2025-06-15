package com.example.fittyfit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fittyfit.utils.FirebaseDatabaseHelper;
import com.example.fittyfit.models.Challenge;
import java.util.List;
import java.util.Map;

public class ChallengesActivity extends BaseActivity {
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private static final String TAG = "ChallengesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        // Initialize Firebase helper
        firebaseDatabaseHelper = new FirebaseDatabaseHelper();

        // Setup navigation bar
        setupNavigationBar();

        // Load challenges
        loadChallenges();
    }

    private void loadChallenges() {
        // Load personal challenges
        firebaseDatabaseHelper.getPersonalChallenges(new FirebaseDatabaseHelper.ChallengesCallback() {
            @Override
            public void onChallengesLoaded(List<Challenge> challenges) {
                runOnUiThread(() -> {
                    LinearLayout container = findViewById(R.id.personalChallengesContainer);
                    if (container != null) {
                        container.removeAllViews();
                        for (Challenge challenge : challenges) {
                            View challengeCard = getLayoutInflater().inflate(R.layout.item_personal_challenge, container, false);
                            
                            TextView descView = challengeCard.findViewById(R.id.personalChallengeDesc);
                            ProgressBar progressBar = challengeCard.findViewById(R.id.personalChallengeProgress);
                            TextView progressText = challengeCard.findViewById(R.id.personalChallengeProgressText);
                            
                            if (descView != null) descView.setText(challenge.getDescription());
                            if (progressBar != null) progressBar.setProgress(challenge.getProgress());
                            if (progressText != null) progressText.setText(challenge.getProgress() + "%");
                            
                            container.addView(challengeCard);
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading personal challenges: " + error);
            }
        });

        // Load group challenges
        firebaseDatabaseHelper.getGroupChallenges(new FirebaseDatabaseHelper.ChallengesCallback() {
            @Override
            public void onChallengesLoaded(List<Challenge> challenges) {
                runOnUiThread(() -> {
                    LinearLayout container = findViewById(R.id.groupChallengesContainer);
                    if (container != null) {
                        container.removeAllViews();
                        for (Challenge challenge : challenges) {
                            View challengeCard = getLayoutInflater().inflate(R.layout.item_group_challenge, container, false);
                            
                            TextView descView = challengeCard.findViewById(R.id.groupChallengeDesc);
                            ProgressBar progressBar = challengeCard.findViewById(R.id.groupChallengeProgress);
                            TextView progressText = challengeCard.findViewById(R.id.groupChallengeProgressText);
                            TextView leaderView = challengeCard.findViewById(R.id.groupChallengeLeader);
                            TextView prizeView = challengeCard.findViewById(R.id.groupChallengePrize);
                            
                            if (descView != null) descView.setText(challenge.getDescription());
                            if (progressBar != null) progressBar.setProgress(challenge.getProgress());
                            if (progressText != null) progressText.setText(challenge.getProgress() + "%");
                            
                            // Get the leading participant
                            String leadingParticipant = challenge.getLeadingParticipant();
                            if (leaderView != null) {
                                leaderView.setText(leadingParticipant.isEmpty() ? "No leader yet" : "Leading: " + leadingParticipant);
                            }
                            
                            if (prizeView != null) {
                                prizeView.setText(challenge.getPrize());
                            }
                            
                            // Set click listener for the challenge card
                            challengeCard.setOnClickListener(v -> {
                                Intent intent = new Intent(ChallengesActivity.this, GroupChallengeDetailsActivity.class);
                                intent.putExtra("challenge_id", challenge.getId());
                                startActivity(intent);
                            });
                            
                            container.addView(challengeCard);
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading group challenges: " + error);
            }
        });
    }
} 