package com.example.fittyfit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fittyfit.utils.FirebaseDatabaseHelper;
import com.example.fittyfit.models.Challenge;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class ChallengesActivity extends BaseActivity {
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private static final String TAG = "ChallengesActivity";
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        // Initialize Firebase helper and get current user
        firebaseDatabaseHelper = new FirebaseDatabaseHelper();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Setup navigation bar
        setupNavigationBar();

        // Setup create buttons
        setupCreateButtons();

        // Load challenges
        loadChallenges();
    }

    private void setupCreateButtons() {
        // Initialize buttons
        MaterialButton createPersonalButton = findViewById(R.id.btnCreatePersonalChallenge);
        MaterialButton createGroupButton = findViewById(R.id.btnCreateGroupChallenge);
        MaterialButton loadMorePersonalButton = findViewById(R.id.btnLoadMorePersonal);
        MaterialButton loadMoreGroupButton = findViewById(R.id.btnLoadMoreGroup);

        if (createPersonalButton != null) {
            createPersonalButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, ChallengeTypeSelection.class);
                startActivity(intent);
            });
        }

        if (createGroupButton != null) {
            createGroupButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateGroupChallenge.class);
                startActivity(intent);
            });
        }
    }

    private void loadChallenges() {
        // Create dummy personal challenges
        List<Challenge> dummyPersonalChallenges = new ArrayList<>();
        
        // First dummy challenge - 30 Day Running Challenge
        Map<String, Map<String, Object>> runningParticipants = new HashMap<>();
        Map<String, Object> runningParticipantData = new HashMap<>();
        runningParticipantData.put("name", "You");
        runningParticipantData.put("isLeader", true);
        runningParticipantData.put("progress", 45);
        runningParticipants.put(currentUserId, runningParticipantData);
        
        Challenge runningChallenge = new Challenge(
            "running_challenge_1",
            "30 Day Running Challenge",
            "Run 5km every day for 30 days to improve your endurance and stamina",
            "30 days",
            45,
            "running",
            String.valueOf(System.currentTimeMillis()),
            String.valueOf(System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000L)),
            runningParticipants,
            currentUserId,
            System.currentTimeMillis(),
            "",
            "active",
            1,
            ""
        );
        dummyPersonalChallenges.add(runningChallenge);
        
        // Second dummy challenge - 21 Day Yoga Challenge
        Map<String, Map<String, Object>> yogaParticipants = new HashMap<>();
        Map<String, Object> yogaParticipantData = new HashMap<>();
        yogaParticipantData.put("name", "You");
        yogaParticipantData.put("isLeader", true);
        yogaParticipantData.put("progress", 75);
        yogaParticipants.put(currentUserId, yogaParticipantData);
        
        Challenge yogaChallenge = new Challenge(
            "yoga_challenge_1",
            "21 Day Yoga Challenge",
            "Complete 30 minutes of yoga daily to improve flexibility and mindfulness",
            "21 days",
            75,
            "yoga",
            String.valueOf(System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)),
            String.valueOf(System.currentTimeMillis() + (14 * 24 * 60 * 60 * 1000L)),
            yogaParticipants,
            currentUserId,
            System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L),
            "",
            "active",
            1,
            ""
        );
        dummyPersonalChallenges.add(yogaChallenge);

        // Display dummy personal challenges
        LinearLayout container = findViewById(R.id.personalChallengesContainer);
        if (container != null) {
            container.removeAllViews();
            for (Challenge challenge : dummyPersonalChallenges) {
                View challengeCard = getLayoutInflater().inflate(R.layout.item_personal_challenge, container, false);
                
                // Set challenge type icon and text
                ImageView typeIcon = challengeCard.findViewById(R.id.challengeTypeIcon);
                TextView typeText = challengeCard.findViewById(R.id.challengeType);
                if (typeIcon != null) {
                    int iconRes = getDefaultImageForChallengeType(challenge.getType());
                    typeIcon.setImageResource(iconRes);
                }
                if (typeText != null) {
                    typeText.setText(challenge.getType().substring(0, 1).toUpperCase() + 
                                   challenge.getType().substring(1));
                }
                
                // Set challenge title and description
                TextView titleView = challengeCard.findViewById(R.id.challengeTitle);
                TextView descView = challengeCard.findViewById(R.id.challengeDescription);
                if (titleView != null) titleView.setText(challenge.getTitle());
                if (descView != null) descView.setText(challenge.getDescription());
                
                // Set progress
                ProgressBar progressBar = challengeCard.findViewById(R.id.personalChallengeProgress);
                TextView progressText = challengeCard.findViewById(R.id.personalChallengeProgressText);
                if (progressBar != null) progressBar.setProgress(challenge.getProgress());
                if (progressText != null) progressText.setText(challenge.getProgress() + "%");
                
                // Set target (duration)
                TextView durationView = challengeCard.findViewById(R.id.challengeDuration);
                if (durationView != null) durationView.setText(challenge.getTarget());
                
                // Set up view details button
                MaterialButton viewDetailsButton = challengeCard.findViewById(R.id.viewDetailsButton);
                if (viewDetailsButton != null) {
                    viewDetailsButton.setOnClickListener(v -> {
                        Intent intent = new Intent(ChallengesActivity.this, ChallengeDetailsActivity.class);
                        intent.putExtra("challenge_id", challenge.getId());
                        startActivity(intent);
                    });
                }
                
                container.addView(challengeCard);
            }
        }

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
                            
                            // Set challenge image
                            ImageView challengeImage = challengeCard.findViewById(R.id.challengeImage);
                            if (challengeImage != null) {
                                if (challenge.getImageUrl() != null && !challenge.getImageUrl().isEmpty()) {
                                    // Load custom image if available
                                    Glide.with(ChallengesActivity.this)
                                        .load(challenge.getImageUrl())
                                        .centerCrop()
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .into(challengeImage);
                                } else {
                                    // Use default group challenge image
                                    Glide.with(ChallengesActivity.this)
                                        .load(R.drawable.group_challenge_animation)
                                        .centerCrop()
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .into(challengeImage);
                                }
                            }
                            
                            TextView descView = challengeCard.findViewById(R.id.groupChallengeDesc);
                            ProgressBar progressBar = challengeCard.findViewById(R.id.groupChallengeProgress);
                            TextView progressText = challengeCard.findViewById(R.id.groupChallengeProgressText);
                            TextView leaderView = challengeCard.findViewById(R.id.groupChallengeLeader);
                            TextView prizeView = challengeCard.findViewById(R.id.groupChallengePrize);
                            MaterialButton joinButton = challengeCard.findViewById(R.id.joinButton);
                            
                            if (descView != null) descView.setText(challenge.getDescription());
                            if (progressBar != null) progressBar.setProgress(challenge.getProgress());
                            if (progressText != null) progressText.setText(challenge.getProgress() + "%");
                            
                            // Get the leading participant
                            String leadingParticipant = challenge.getLeadingParticipant();
                            if (leaderView != null) {
                                leaderView.setText(leadingParticipant.isEmpty() ? "No leader yet" : leadingParticipant);
                            }
                            
                            if (prizeView != null) {
                                prizeView.setText(challenge.getPrize());
                            }

                            // Setup join button
                            if (joinButton != null) {
                                boolean isParticipant = challenge.getParticipants().containsKey(currentUserId);
                                joinButton.setText(isParticipant ? "Leave" : "Join");
                                joinButton.setOnClickListener(v -> {
                                    if (isParticipant) {
                                        leaveChallenge(challenge.getId());
                                    } else {
                                        joinChallenge(challenge.getId());
                                    }
                                });
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

    private int getDefaultImageForChallengeType(String challengeType) {
        // Map challenge types to their default images
        switch (challengeType.toLowerCase()) {
            case "running":
                return R.drawable.ic_running;
            case "cycling":
                return R.drawable.ic_cycling;
            case "swimming":
                return R.drawable.ic_swimming;
            case "yoga":
                return R.drawable.ic_yoga;
            case "weightlifting":
                return R.drawable.ic_weightlifting;
            default:
                return R.drawable.default_challenge;
        }
    }

    private void joinChallenge(String challengeId) {
        firebaseDatabaseHelper.joinGroupChallenge(challengeId, currentUserId, new FirebaseDatabaseHelper.Callback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(ChallengesActivity.this, "Joined challenge successfully!", Toast.LENGTH_SHORT).show();
                    loadChallenges(); // Reload challenges to update UI
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ChallengesActivity.this, "Failed to join challenge: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void leaveChallenge(String challengeId) {
        firebaseDatabaseHelper.leaveGroupChallenge(challengeId, currentUserId, new FirebaseDatabaseHelper.Callback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(ChallengesActivity.this, "Left challenge successfully!", Toast.LENGTH_SHORT).show();
                    loadChallenges(); // Reload challenges to update UI
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ChallengesActivity.this, "Failed to leave challenge: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
} 