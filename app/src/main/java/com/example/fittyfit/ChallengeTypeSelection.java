package com.example.fittyfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.Arrays;
import java.util.List;

public class ChallengeTypeSelection extends BaseActivity {
    private RecyclerView challengeTypesRecyclerView;
    private List<String> challengeTypes = Arrays.asList(
        "Running",
        "Cycling",
        "Swimming",
        "Weightlifting",
        "Yoga",
        "HIIT"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_type_selection);

        // Setup navigation bar
        setupNavigationBar();

        // Initialize views
        initializeViews();
        setupRecyclerView();
    }

    private void initializeViews() {
        challengeTypesRecyclerView = findViewById(R.id.challengeTypesRecyclerView);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void setupRecyclerView() {
        challengeTypesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        challengeTypesRecyclerView.setAdapter(new ChallengeTypeAdapter(challengeTypes, this::onChallengeTypeSelected));
    }

    private void onChallengeTypeSelected(String challengeType) {
        Intent intent = new Intent(this, CreatePersonalChallenge.class);
        intent.putExtra("challenge_type", challengeType);
        startActivity(intent);
    }

    private class ChallengeTypeAdapter extends RecyclerView.Adapter<ChallengeTypeAdapter.ViewHolder> {
        private List<String> types;
        private OnChallengeTypeClickListener listener;

        public ChallengeTypeAdapter(List<String> types, OnChallengeTypeClickListener listener) {
            this.types = types;
            this.listener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_challenge_type, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String type = types.get(position);
            holder.typeName.setText(type);
            
            // Set icon based on challenge type
            int iconResource;
            switch (type.toLowerCase()) {
                case "running":
                    iconResource = R.drawable.running_challenge;
                    break;
                case "cycling":
                    iconResource = R.drawable.cycling_challenge;
                    break;
                case "swimming":
                    iconResource = R.drawable.swimming_challenge;
                    break;
                case "weightlifting":
                    iconResource = R.drawable.weightlifting_challenge;
                    break;
                case "yoga":
                    iconResource = R.drawable.yoga_challenge;
                    break;
                case "hiit":
                    iconResource = R.drawable.hiit_challenge;
                    break;
                default:
                    iconResource = R.drawable.default_challenge;
            }
            holder.typeIcon.setImageResource(iconResource);
            
            holder.card.setOnClickListener(v -> listener.onChallengeTypeClick(type));
        }

        @Override
        public int getItemCount() {
            return types.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            MaterialCardView card;
            TextView typeName;
            ImageView typeIcon;

            ViewHolder(View itemView) {
                super(itemView);
                card = itemView.findViewById(R.id.challengeTypeCard);
                typeName = itemView.findViewById(R.id.challengeTypeName);
                typeIcon = itemView.findViewById(R.id.challengeTypeIcon);
            }
        }
    }

    interface OnChallengeTypeClickListener {
        void onChallengeTypeClick(String type);
    }
} 