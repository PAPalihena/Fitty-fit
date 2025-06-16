package com.example.fittyfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class ChallengeTypeSelection extends AppCompatActivity {

    private MaterialCardView walkingChallengeCard;
    private MaterialCardView caloriesChallengeCard;
    private MaterialCardView distanceChallengeCard;
    private MaterialCardView yogaChallengeCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_type_selection);

        // Initialize views
        walkingChallengeCard = findViewById(R.id.walkingChallengeCard);
        caloriesChallengeCard = findViewById(R.id.caloriesChallengeCard);
        distanceChallengeCard = findViewById(R.id.distanceChallengeCard);
        yogaChallengeCard = findViewById(R.id.yogaChallengeCard);

        // Set click listeners with correct challenge type mapping
        walkingChallengeCard.setOnClickListener(v -> startCreateChallenge("steps"));
        caloriesChallengeCard.setOnClickListener(v -> startCreateChallenge("calories"));
        distanceChallengeCard.setOnClickListener(v -> startCreateChallenge("distance"));
        yogaChallengeCard.setOnClickListener(v -> startCreateChallenge("yoga"));
    }

    private void startCreateChallenge(String challengeType) {
        Intent intent = new Intent(this, CreatePersonalChallenge.class);
        intent.putExtra("challenge_type", challengeType);
        startActivity(intent);
    }
} 