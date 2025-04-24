package com.example.fittyfit;

import android.os.Bundle;

public class ChallengesActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        // Setup navigation bar
        setupNavigationBar();
    }
} 