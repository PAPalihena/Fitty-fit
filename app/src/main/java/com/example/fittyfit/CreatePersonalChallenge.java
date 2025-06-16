package com.example.fittyfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.example.fittyfit.models.Challenge;
import com.example.fittyfit.utils.FirebaseDatabaseHelper;
import java.util.HashMap;
import java.util.Map;
import kotlin.Unit;

public class CreatePersonalChallenge extends BaseActivity {
    private TextInputEditText durationInput;
    private MaterialButton createChallengeButton;
    private ImageView challengeTypeImage;
    private String selectedChallengeType;
    private FirebaseDatabaseHelper firebaseHelper;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_personal_challenge);

        // Setup navigation bar
        setupNavigationBar();

        // Get challenge type from intent
        selectedChallengeType = getIntent().getStringExtra("challenge_type");
        if (selectedChallengeType == null || selectedChallengeType.isEmpty()) {
            Toast.makeText(this, "Invalid challenge type", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase
        firebaseHelper = new FirebaseDatabaseHelper();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        initializeViews();
        setupChallengeTypeImage();
        setupClickListeners();
    }

    private void initializeViews() {
        durationInput = findViewById(R.id.durationInput);
        createChallengeButton = findViewById(R.id.createChallengeButton);
        challengeTypeImage = findViewById(R.id.challengeTypeImage);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void setupChallengeTypeImage() {
        // Set appropriate image based on challenge type
        int imageResource = getResources().getIdentifier(
            "ic_" + selectedChallengeType.toLowerCase(),
            "drawable",
            getPackageName()
        );
        if (imageResource != 0) {
            challengeTypeImage.setImageResource(imageResource);
        }
    }

    private void setupClickListeners() {
        createChallengeButton.setOnClickListener(v -> createChallenge());
    }

    private void createChallenge() {
        String duration = durationInput.getText().toString().trim();
        
        if (duration.isEmpty()) {
            Toast.makeText(this, "Please enter challenge duration", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        
        // Create participant data for the creator
        Map<String, Object> participantData = new HashMap<>();
        participantData.put("name", auth.getCurrentUser().getDisplayName());
        participantData.put("isLeader", true);
        participantData.put("progress", 0);
        
        // Create participants map with the creator as the only participant
        Map<String, Map<String, Object>> participants = new HashMap<>();
        participants.put(userId, participantData);

        Challenge challenge = new Challenge(
            "", // ID will be set by Firebase
            selectedChallengeType + " Challenge",
            "Complete " + selectedChallengeType + " for " + duration + " days",
            duration + " days",
            0, // Initial progress
            "personal",
            String.valueOf(System.currentTimeMillis()),
            "", // End date will be calculated based on duration
            participants,
            userId,
            System.currentTimeMillis(),
            "", // No prize for personal challenges
            "active", // status
            1, // totalParticipants (just the creator)
            "" // No image for personal challenges
        );

        firebaseHelper.saveChallenge(challenge, (success, challengeId) -> {
            if (success) {
                Toast.makeText(CreatePersonalChallenge.this, 
                    "Challenge created successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(CreatePersonalChallenge.this, 
                    "Error creating challenge", Toast.LENGTH_SHORT).show();
            }
            return Unit.INSTANCE;
        });
    }
} 