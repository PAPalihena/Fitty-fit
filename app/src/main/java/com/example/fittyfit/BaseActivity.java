package com.example.fittyfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fittyfit.views.NotificationPanelView;

public class BaseActivity extends AppCompatActivity {
    protected NotificationPanelView notificationPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_layout);
        
        try {
            // Initialize notification panel
            notificationPanel = findViewById(R.id.notificationPanel);
            if (notificationPanel != null) {
                // Set initial state based on global state
                if (notificationPanel.isPanelShowing()) {
                    notificationPanel.setVisibility(View.VISIBLE);
                } else {
                    notificationPanel.setVisibility(View.GONE);
                }
            }
            
            // Set up navigation bar
            setupNavigationBar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setContentLayout(int layoutResId) {
        try {
            View contentContainer = findViewById(R.id.contentContainer);
            if (contentContainer != null) {
                ViewGroup.LayoutParams params = contentContainer.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                contentContainer.setLayoutParams(params);
            }
            getLayoutInflater().inflate(layoutResId, findViewById(R.id.contentContainer), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setupNavigationBar() {
        try {
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
                        finish();
                    }
                });
            }

            if (navCommunity != null) {
                navCommunity.setOnClickListener(v -> {
                    if (!getClass().equals(CommunityActivity.class)) {
                        Intent intent = new Intent(this, CommunityActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            if (navChallenges != null) {
                navChallenges.setOnClickListener(v -> {
                    if (!getClass().equals(ChallengesActivity.class)) {
                        Intent intent = new Intent(this, ChallengesActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            if (navProfile != null) {
                navProfile.setOnClickListener(v -> {
                    if (!getClass().equals(ProfileActivity.class)) {
                        Intent intent = new Intent(this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            if (navNotifications != null) {
                navNotifications.setOnClickListener(v -> {
                    try {
                        if (notificationPanel != null) {
                            if (notificationPanel.isPanelShowing()) {
                                notificationPanel.hide();
                            } else {
                                notificationPanel.show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            if (navMore != null) {
                navMore.setOnClickListener(v -> showMoreMenu(v));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure notification panel state is maintained
        if (notificationPanel != null) {
            if (notificationPanel.isPanelShowing()) {
                notificationPanel.setVisibility(View.VISIBLE);
            } else {
                notificationPanel.setVisibility(View.GONE);
            }
        }
    }

    private void showMoreMenu(View anchor) {
        try {
            PopupMenu popup = new PopupMenu(this, anchor);
            popup.getMenuInflater().inflate(R.menu.more_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_about) {
                    // Handle About Us click
                    showAboutDialog();
                    return true;
                } else if (itemId == R.id.menu_settings) {
                    // Handle Settings click
                    showSettingsDialog();
                    return true;
                }
                return false;
            });

            popup.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAboutDialog() {
        try {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("About Us")
                .setMessage("FittyFit is your personal fitness companion. We help you achieve your fitness goals through challenges, community support, and personalized tracking.")
                .setPositiveButton("OK", null)
                .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSettingsDialog() {
        try {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Settings")
                .setItems(new String[]{"Notifications", "Privacy", "Account", "Help & Support"}, (dialog, which) -> {
                    // Handle settings options
                    switch (which) {
                        case 0:
                            // Handle Notifications settings
                            break;
                        case 1:
                            // Handle Privacy settings
                            break;
                        case 2:
                            // Handle Account settings
                            break;
                        case 3:
                            // Handle Help & Support
                            break;
                    }
                })
                .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 