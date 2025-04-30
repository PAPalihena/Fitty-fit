package com.example.fittyfit;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ChallengesActivity extends BaseActivity {
    private ProgressBar[] personalChallengeProgress;
    private ProgressBar[] groupChallengeProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        // Setup navigation bar
        setupNavigationBar();

        // Initialize views
        initializeViews();
    }

    private void initializeViews() {
        // Initialize progress bars arrays
        personalChallengeProgress = new ProgressBar[] {
            findViewById(R.id.personalChallengeProgress1),
            findViewById(R.id.personalChallengeProgress2),
            findViewById(R.id.personalChallengeProgress3)
        };

        groupChallengeProgress = new ProgressBar[] {
            findViewById(R.id.groupChallengeProgress1),
            findViewById(R.id.groupChallengeProgress2),
            findViewById(R.id.groupChallengeProgress3)
        };

        // Set dummy progress values for personal challenges
        personalChallengeProgress[0].setProgress(65);
        personalChallengeProgress[1].setProgress(45);
        personalChallengeProgress[2].setProgress(80);

        // Set dummy progress values for group challenges
        groupChallengeProgress[0].setProgress(45);
        groupChallengeProgress[1].setProgress(60);
        groupChallengeProgress[2].setProgress(75);

        // Set level descriptions
        ((TextView) findViewById(R.id.personalLevelDesc)).setText("Fitness Warrior");
        ((TextView) findViewById(R.id.groupLevelDesc)).setText("Team Player");
    }
} 