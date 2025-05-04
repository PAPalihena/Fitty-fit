package com.example.fittyfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private View challengeOptionsMenu;
    private boolean isMenuVisible = false;

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

        // Setup FAB and menu
        setupFloatingActionButton();
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

        // Initialize menu
        challengeOptionsMenu = findViewById(R.id.challengeOptionsMenu);
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fabCreateChallenge);
        TextView btnPersonal = findViewById(R.id.btnCreatePersonalChallenge);
        TextView btnGroup = findViewById(R.id.btnCreateGroupChallenge);

        fab.setOnClickListener(v -> toggleMenu());

        btnPersonal.setOnClickListener(v -> {
            // TODO: Launch create personal challenge activity
            hideMenu();
        });

        btnGroup.setOnClickListener(v -> {
            Intent intent = new Intent(ChallengesActivity.this, CreateGroupChallenge.class);
            startActivity(intent);
            hideMenu();
        });
    }

    private void toggleMenu() {
        if (isMenuVisible) {
            hideMenu();
        } else {
            showMenu();
        }
    }

    private void showMenu() {
        challengeOptionsMenu.setVisibility(View.VISIBLE);
        Animation slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        challengeOptionsMenu.startAnimation(slideUp);
        isMenuVisible = true;
    }

    private void hideMenu() {
        Animation slideDown = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                challengeOptionsMenu.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        challengeOptionsMenu.startAnimation(slideDown);
        isMenuVisible = false;
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
} 