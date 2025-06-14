package com.example.fittyfit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.button.MaterialButton;

import com.example.fittyfit.adapters.ProgressAdapter;
import com.example.fittyfit.adapters.ProgressAdapter.ProgressItem;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {
    private ViewPager2 healthTipsViewPager;
    private TabLayout healthTipsTabLayout;
    private HealthTipsAdapter healthTipsAdapter;
    private List<HealthTip> healthTips;
    private Handler autoSwipeHandler;
    private Runnable autoSwipeRunnable;
    private static final long SWIPE_INTERVAL = 5000; // 5 seconds
    private RecyclerView progressRecyclerView;
    private ProgressAdapter progressAdapter;
    private List<ProgressItem> progressItems;
    private boolean isActivityActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupNavigationBar();
        initializeViews();
        setupHealthTips();
        setupProgressSection();
    }

    private void initializeViews() {
        // Set welcome message with different styles
        TextView welcomeText = findViewById(R.id.welcomeText);
        String fullText = "Welcome Back, Praveen!";
        SpannableString spannableString = new SpannableString(fullText);
        
        // Set opacity for "Welcome Back, " using color with alpha
        int originalColor = Color.parseColor("#0c0d0d"); // Get the original color
        int transparentColor = Color.argb(
            (int)(255 * 0.5f), // 50% opacity
            Color.red(originalColor),
            Color.green(originalColor),
            Color.blue(originalColor)
        );
        spannableString.setSpan(
            new ForegroundColorSpan(transparentColor),
            0,                    // start index
            13,                   // end index (length of "Welcome Back, ")
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        
        // Set brown color for "Praveen"
        spannableString.setSpan(
            new ForegroundColorSpan(Color.parseColor("#8B4513")), // Brown color
            14,                    // start index
            21,                    // end index (length of "Praveen")
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        
        welcomeText.setText(spannableString);

        // Initialize RecyclerView with fixed size and layout
        progressRecyclerView = findViewById(R.id.progressRecyclerView);
        progressRecyclerView.setHasFixedSize(true);
        progressRecyclerView.setItemViewCacheSize(4); // Cache 4 items
        progressRecyclerView.setDrawingCacheEnabled(true);
        progressRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        progressRecyclerView.setLayoutManager(layoutManager);
        
        // Add item decoration for spacing
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        progressRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view, 
                                     @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = spacing;
                outRect.right = spacing;
            }
        });
    }

    private void setupHealthTips() {
        healthTipsViewPager = findViewById(R.id.healthTipsViewPager);
        healthTipsTabLayout = findViewById(R.id.healthTipsTabLayout);

        // Initialize health tips
        healthTips = new ArrayList<>();
        healthTips.add(new HealthTip("Hydration", "Stay hydrated throughout the day", R.drawable.health_tip_1));
        healthTips.add(new HealthTip("Exercise", "Exercise for at least 30 minutes daily", R.drawable.health_tip_2));
        healthTips.add(new HealthTip("Sleep", "Get 7-8 hours of sleep each night", R.drawable.health_tip_3));
        healthTips.add(new HealthTip("Diet", "Eat a balanced diet with plenty of fruits and vegetables", R.drawable.health_tip_4));
        healthTips.add(new HealthTip("Movement", "Take regular breaks from sitting", R.drawable.health_tip_5));

        // Setup ViewPager2 with optimizations
        healthTipsAdapter = new HealthTipsAdapter(this, healthTips);
        healthTipsViewPager.setAdapter(healthTipsAdapter);
        healthTipsViewPager.setOffscreenPageLimit(1);
        healthTipsViewPager.setPageTransformer((page, position) -> {
            // Optional: Add page transformations for better UX
            page.setAlpha(1 - Math.abs(position));
        });

        // Setup TabLayout
        new TabLayoutMediator(healthTipsTabLayout, healthTipsViewPager,
                (tab, position) -> {}).attach();

        // Initialize auto-swipe
        initializeAutoSwipe();
    }

    private void setupProgressSection() {
        progressItems = new ArrayList<>();
        progressItems.add(new ProgressItem(R.drawable.ic_steps, "Steps", 0, "0/10,000"));
        progressItems.add(new ProgressItem(R.drawable.ic_calories, "Calories", 0, "0/2,000"));
        progressItems.add(new ProgressItem(R.drawable.ic_distance, "Distance", 0, "0/5 km"));
        progressItems.add(new ProgressItem(R.drawable.ic_workout, "Workout Time", 0, "0/60 min"));

        progressAdapter = new ProgressAdapter(progressItems);
        progressRecyclerView.setAdapter(progressAdapter);

        // Setup challenges
        setupChallenges();
        
        // Simulate progress updates
        simulateProgressUpdates();
    }

    private void setupChallenges() {
        // Setup Personal Challenge
        View personalChallengeView = findViewById(R.id.personalChallengeItem);
        if (personalChallengeView != null) {
            TextView personalTitle = personalChallengeView.findViewById(R.id.challengeTitle);
            TextView personalDescription = personalChallengeView.findViewById(R.id.challengeDescription);
            TextView personalProgress = personalChallengeView.findViewById(R.id.challengeProgress);
            LinearProgressIndicator personalProgressBar = personalChallengeView.findViewById(R.id.challengeProgressBar);

            if (personalTitle != null) personalTitle.setText("30-Day Push-up Challenge");
            if (personalDescription != null) personalDescription.setText("Complete 100 push-ups daily for 30 days to build upper body strength and endurance");
            if (personalProgress != null) personalProgress.setText("15/30 days");
            if (personalProgressBar != null) personalProgressBar.setProgress(50);
        }

        // Setup Group Challenge
        View groupChallengeView = findViewById(R.id.groupChallengeItem);
        if (groupChallengeView != null) {
            TextView groupTitle = groupChallengeView.findViewById(R.id.challengeTitle);
            TextView groupDescription = groupChallengeView.findViewById(R.id.challengeDescription);
            TextView groupProgress = groupChallengeView.findViewById(R.id.challengeProgress);
            TextView leaderInfo = groupChallengeView.findViewById(R.id.leaderInfo);
            TextView prizeInfo = groupChallengeView.findViewById(R.id.prizeInfo);
            LinearProgressIndicator groupProgressBar = groupChallengeView.findViewById(R.id.challengeProgressBar);

            if (groupTitle != null) groupTitle.setText("Team Running Challenge");
            if (groupDescription != null) groupDescription.setText("Run 100km as a team in 7 days. Work together to achieve the goal!");
            if (groupProgress != null) groupProgress.setText("25/100 km");
            if (leaderInfo != null) leaderInfo.setText("🏆 John D. (45km)");
            if (prizeInfo != null) prizeInfo.setText("🎁 $500 Gift Card");
            if (groupProgressBar != null) groupProgressBar.setProgress(25);
        }

        // Setup buttons with null checks
        MaterialButton btnLoadMorePersonal = findViewById(R.id.btnLoadMorePersonal);
        MaterialButton btnLoadMoreGroup = findViewById(R.id.btnLoadMoreGroup);
        MaterialButton btnCreatePersonalChallenge = findViewById(R.id.btnCreatePersonalChallenge);
        MaterialButton btnCreateGroupChallenge = findViewById(R.id.btnCreateGroupChallenge);

        if (btnLoadMorePersonal != null) {
            btnLoadMorePersonal.setOnClickListener(v -> {
                Intent intent = new Intent(this, ChallengesActivity.class);
                intent.putExtra("challengeType", "personal");
                startActivity(intent);
            });
        }

        if (btnLoadMoreGroup != null) {
            btnLoadMoreGroup.setOnClickListener(v -> {
                Intent intent = new Intent(this, ChallengesActivity.class);
                intent.putExtra("challengeType", "group");
                startActivity(intent);
            });
        }

        if (btnCreatePersonalChallenge != null) {
            btnCreatePersonalChallenge.setOnClickListener(v -> {
                Intent intent = new Intent(this, ChallengeTypeSelection.class);
                startActivity(intent);
            });
        }

        if (btnCreateGroupChallenge != null) {
            btnCreateGroupChallenge.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateGroupChallenge.class);
                startActivity(intent);
            });
        }
    }

    private void initializeAutoSwipe() {
        if (autoSwipeHandler == null) {
            autoSwipeHandler = new Handler(Looper.getMainLooper());
        }
        
        if (autoSwipeRunnable == null) {
            autoSwipeRunnable = new Runnable() {
                @Override
                public void run() {
                    if (isActivityActive && healthTipsViewPager != null && healthTips != null && !healthTips.isEmpty()) {
                        int currentItem = healthTipsViewPager.getCurrentItem();
                        int nextItem = (currentItem + 1) % healthTips.size();
                        healthTipsViewPager.setCurrentItem(nextItem, true);
                        autoSwipeHandler.postDelayed(this, SWIPE_INTERVAL);
                    }
                }
            };
        }
    }

    private void simulateProgressUpdates() {
        if (autoSwipeHandler != null) {
            autoSwipeHandler.postDelayed(() -> {
                if (isActivityActive) {
                    updateProgress(0, 65); // Steps
                    updateProgress(1, 45); // Calories
                    updateProgress(2, 30); // Distance
                    updateProgress(3, 75); // Workout Time
                }
            }, 1000);
        }
    }

    private void updateProgress(int position, int progress) {
        if (progressAdapter != null && position >= 0 && position < progressItems.size()) {
            progressAdapter.updateProgress(position, progress);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityActive = true;
        startAutoSwipe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityActive = false;
        stopAutoSwipe();
    }

    private void startAutoSwipe() {
        if (autoSwipeHandler != null && autoSwipeRunnable != null && isActivityActive) {
            autoSwipeHandler.removeCallbacks(autoSwipeRunnable);
            autoSwipeHandler.postDelayed(autoSwipeRunnable, SWIPE_INTERVAL);
        }
    }

    private void stopAutoSwipe() {
        if (autoSwipeHandler != null && autoSwipeRunnable != null) {
            autoSwipeHandler.removeCallbacks(autoSwipeRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityActive = false;
        stopAutoSwipe();
        if (autoSwipeHandler != null) {
            autoSwipeHandler.removeCallbacksAndMessages(null);
            autoSwipeHandler = null;
        }
        autoSwipeRunnable = null;
        
        // Clear adapters and lists
        if (healthTipsAdapter != null) {
            healthTipsAdapter = null;
        }
        if (progressAdapter != null) {
            progressAdapter = null;
        }
        if (healthTips != null) {
            healthTips.clear();
            healthTips = null;
        }
        if (progressItems != null) {
            progressItems.clear();
            progressItems = null;
        }
    }
}

class HealthTipsAdapter extends RecyclerView.Adapter<HealthTipsAdapter.HealthTipViewHolder> {
    private final List<HealthTip> healthTips;
    private final HomeActivity activity;

    public HealthTipsAdapter(HomeActivity activity, List<HealthTip> healthTips) {
        this.activity = activity;
        this.healthTips = healthTips;
    }

    @NonNull
    @Override
    public HealthTipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_health_tip, parent, false);
        return new HealthTipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HealthTipViewHolder holder, int position) {
        HealthTip healthTip = healthTips.get(position);
        holder.tipTitle.setText(healthTip.getTitle());
        holder.tipDescription.setText(healthTip.getTip());
        holder.tipImage.setImageResource(healthTip.getImageResourceId());
    }

    @Override
    public int getItemCount() {
        return healthTips.size();
    }

    static class HealthTipViewHolder extends RecyclerView.ViewHolder {
        final TextView tipTitle;
        final TextView tipDescription;
        final ImageView tipImage;

        public HealthTipViewHolder(@NonNull View itemView) {
            super(itemView);
            tipTitle = itemView.findViewById(R.id.tipTitle);
            tipDescription = itemView.findViewById(R.id.tipDescription);
            tipImage = itemView.findViewById(R.id.tipImage);
        }
    }
} 