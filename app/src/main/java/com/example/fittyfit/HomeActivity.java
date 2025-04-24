package com.example.fittyfit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {
    private ViewPager2 healthTipsViewPager;
    private TabLayout healthTipsTabLayout;
    private HealthTipsAdapter healthTipsAdapter;
    private List<HealthTip> healthTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Setup navigation bar
        setupNavigationBar();

        // Set welcome message
        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome Back, John!");

        // Initialize progress indicators
        CircularProgressIndicator waterProgress = findViewById(R.id.waterProgress);
        CircularProgressIndicator caloriesProgress = findViewById(R.id.caloriesProgress);
        CircularProgressIndicator stepsProgress = findViewById(R.id.stepsProgress);

        // Set progress values
        waterProgress.setProgress(75);
        caloriesProgress.setProgress(60);
        stepsProgress.setProgress(85);

        // Initialize health tips
        initializeHealthTips();

        // Setup ViewPager2 and TabLayout
        healthTipsViewPager = findViewById(R.id.healthTipsViewPager);
        healthTipsTabLayout = findViewById(R.id.healthTipsTabLayout);

        healthTipsAdapter = new HealthTipsAdapter(this, healthTips);
        healthTipsViewPager.setAdapter(healthTipsAdapter);

        new TabLayoutMediator(healthTipsTabLayout, healthTipsViewPager,
                (tab, position) -> {
                    // Customize tab appearance if needed
                }).attach();

        // Update challenges
        updateChallenges();
    }

    private void updateChallenges() {
        // Update personal challenge progress
        LinearProgressIndicator personalChallengeProgress = findViewById(R.id.personalChallengeProgress);
        personalChallengeProgress.setProgress(50);

        // Update group challenge progress
        LinearProgressIndicator groupChallengeProgress = findViewById(R.id.groupChallengeProgress);
        groupChallengeProgress.setProgress(75);
    }

    private void initializeHealthTips() {
        healthTips = new ArrayList<>();
        healthTips.add(new HealthTip("Hydration", "Stay hydrated throughout the day"));
        healthTips.add(new HealthTip("Exercise", "Exercise for at least 30 minutes daily"));
        healthTips.add(new HealthTip("Sleep", "Get 7-8 hours of sleep each night"));
        healthTips.add(new HealthTip("Diet", "Eat a balanced diet with plenty of fruits and vegetables"));
        healthTips.add(new HealthTip("Movement", "Take regular breaks from sitting"));
    }
}

class HealthTipsAdapter extends RecyclerView.Adapter<HealthTipsAdapter.HealthTipViewHolder> {
    private List<HealthTip> healthTips;
    private HomeActivity activity;

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
        holder.tipText.setText(healthTip.getTip());
    }

    @Override
    public int getItemCount() {
        return healthTips.size();
    }

    static class HealthTipViewHolder extends RecyclerView.ViewHolder {
        TextView tipText;

        public HealthTipViewHolder(@NonNull View itemView) {
            super(itemView);
            tipText = itemView.findViewById(R.id.tipText);
        }
    }
} 