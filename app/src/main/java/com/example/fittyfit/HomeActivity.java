package com.example.fittyfit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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
        // Set welcome message
        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome Back, Praveen!");

        // Initialize RecyclerView
        progressRecyclerView = findViewById(R.id.progressRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        progressRecyclerView.setLayoutManager(layoutManager);
        progressRecyclerView.setHasFixedSize(true);
        
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

        // Setup ViewPager2
        healthTipsAdapter = new HealthTipsAdapter(this, healthTips);
        healthTipsViewPager.setAdapter(healthTipsAdapter);
        healthTipsViewPager.setOffscreenPageLimit(1); // Optimize memory usage

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

        // Update challenges
        updateChallenges();
        
        // Simulate progress updates
        simulateProgressUpdates();
    }

    private void initializeAutoSwipe() {
        autoSwipeHandler = new Handler(Looper.getMainLooper());
        autoSwipeRunnable = new Runnable() {
            @Override
            public void run() {
                if (healthTipsViewPager != null && healthTips != null && !healthTips.isEmpty()) {
                    int currentItem = healthTipsViewPager.getCurrentItem();
                    int nextItem = (currentItem + 1) % healthTips.size();
                    healthTipsViewPager.setCurrentItem(nextItem, true);
                    autoSwipeHandler.postDelayed(this, SWIPE_INTERVAL);
                }
            }
        };
    }

    private void simulateProgressUpdates() {
        new Handler().postDelayed(() -> {
            updateProgress(0, 65); // Steps
            updateProgress(1, 45); // Calories
            updateProgress(2, 30); // Distance
            updateProgress(3, 75); // Workout Time
        }, 1000);
    }

    private void updateProgress(int position, int progress) {
        if (progressAdapter != null) {
            progressAdapter.updateProgress(position, progress);
        }
    }

    private void updateChallenges() {
        LinearProgressIndicator personalChallengeProgress = findViewById(R.id.personalChallengeProgress);
        LinearProgressIndicator groupChallengeProgress = findViewById(R.id.groupChallengeProgress);
        
        if (personalChallengeProgress != null) {
            personalChallengeProgress.setProgress(50);
        }
        if (groupChallengeProgress != null) {
            groupChallengeProgress.setProgress(75);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAutoSwipe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoSwipe();
    }

    private void startAutoSwipe() {
        if (autoSwipeHandler != null && autoSwipeRunnable != null) {
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
        stopAutoSwipe();
        if (autoSwipeHandler != null) {
            autoSwipeHandler.removeCallbacksAndMessages(null);
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