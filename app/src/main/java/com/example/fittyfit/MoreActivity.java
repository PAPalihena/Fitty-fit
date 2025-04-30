package com.example.fittyfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        // Initialize navigation buttons
        ImageView navLogo = findViewById(R.id.navLogo);
        ImageButton navCommunity = findViewById(R.id.navCommunity);
        ImageButton navChallenges = findViewById(R.id.navChallenges);
        ImageButton navProfile = findViewById(R.id.navProfile);
        ImageButton navNotifications = findViewById(R.id.navNotifications);
        ImageButton navMore = findViewById(R.id.navMore);

        // Set click listeners for navigation buttons
        navLogo.setOnClickListener(v -> {
            Intent intent = new Intent(MoreActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        navCommunity.setOnClickListener(v -> {
            Intent intent = new Intent(MoreActivity.this, CommunityActivity.class);
            startActivity(intent);
            finish();
        });

        navChallenges.setOnClickListener(v -> {
            Intent intent = new Intent(MoreActivity.this, MainActivity.class);
            intent.putExtra("selectedTab", R.id.navigation_challenges);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MoreActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        navNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(MoreActivity.this, NotificationsActivity.class);
            startActivity(intent);
            finish();
        });

        navMore.setOnClickListener(v -> {
            // Already in MoreActivity, no need to navigate
        });
    }
} 