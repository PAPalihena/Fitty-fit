package com.example.fittyfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupNavigationBar() {
        // Find navigation buttons
        ImageView navLogo = findViewById(R.id.navLogo);
        ImageButton navCommunity = findViewById(R.id.navCommunity);
        ImageButton navChallenges = findViewById(R.id.navChallenges);
        ImageButton navProfile = findViewById(R.id.navProfile);
        ImageButton navNotifications = findViewById(R.id.navNotifications);
        ImageButton navMore = findViewById(R.id.navMore);

        if (navLogo != null) {
            navLogo.setOnClickListener(v -> {
                if (!getClass().equals(HomeActivity.class)) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }

        if (navCommunity != null) {
            navCommunity.setOnClickListener(v -> {
                if (!getClass().equals(CommunityActivity.class)) {
                    Intent intent = new Intent(this, CommunityActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (navChallenges != null) {
            navChallenges.setOnClickListener(v -> {
                if (!getClass().equals(ChallengesActivity.class)) {
                    Intent intent = new Intent(this, ChallengesActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                if (!getClass().equals(ProfileActivity.class)) {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (navNotifications != null) {
            navNotifications.setOnClickListener(v -> {
                if (!getClass().equals(NotificationsActivity.class)) {
                    Intent intent = new Intent(this, NotificationsActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (navMore != null) {
            navMore.setOnClickListener(v -> {
                if (!getClass().equals(MoreActivity.class)) {
                    Intent intent = new Intent(this, MoreActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
} 