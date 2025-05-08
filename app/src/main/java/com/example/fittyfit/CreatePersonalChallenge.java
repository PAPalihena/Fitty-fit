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
import java.util.Arrays;
import java.util.List;

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
        // Set image based on challenge type
        int imageResource;
        switch (selectedChallengeType.toLowerCase()) {
            case "running":
                imageResource = R.drawable.running_challenge;
                break;
            case "cycling":
                imageResource = R.drawable.cycling_challenge;
                break;
            case "swimming":
                imageResource = R.drawable.swimming_challenge;
                break;
            case "weightlifting":
                imageResource = R.drawable.weightlifting_challenge;
                break;
            case "yoga":
                imageResource = R.drawable.yoga_challenge;
                break;
            case "hiit":
                imageResource = R.drawable.hiit_challenge;
                break;
            default:
                imageResource = R.drawable.default_challenge;
        }
        challengeTypeImage.setImageResource(imageResource);
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
        Challenge challenge = new Challenge(
            "", // ID will be set by Firebase
            selectedChallengeType + " Challenge",
            "Complete " + selectedChallengeType + " for " + duration + " days",
            duration + " days",
            0, // Initial progress
            "personal",
            String.valueOf(System.currentTimeMillis()),
            "", // End date will be calculated based on duration
            Arrays.asList(userId),
            userId,
            System.currentTimeMillis()
        );

        firebaseHelper.saveChallenge(challenge, new kotlin.jvm.functions.Function2<Boolean, String, kotlin.Unit>() {
            @Override
            public kotlin.Unit invoke(Boolean success, String challengeId) {
                if (success && challengeId != null) {
                    Toast.makeText(CreatePersonalChallenge.this, 
                        "Personal challenge created successfully!", Toast.LENGTH_SHORT).show();
                    
                    // Navigate to challenge details
                    Intent intent = new Intent(CreatePersonalChallenge.this, ChallengeDetailsActivity.class);
                    intent.putExtra("challenge_id", challengeId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CreatePersonalChallenge.this, 
                        "Error creating challenge", Toast.LENGTH_SHORT).show();
                }
                return kotlin.Unit.INSTANCE;
            }
        });
    }
} 