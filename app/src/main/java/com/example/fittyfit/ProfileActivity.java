package com.example.fittyfit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView profilePhoto;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize navigation buttons
        ImageView navLogo = findViewById(R.id.navLogo);
        ImageButton navCommunity = findViewById(R.id.navCommunity);
        ImageButton navChallenges = findViewById(R.id.navChallenges);
        ImageButton navProfile = findViewById(R.id.navProfile);
        ImageButton navNotifications = findViewById(R.id.navNotifications);
        ImageButton navMore = findViewById(R.id.navMore);

        // Set click listeners for navigation buttons
        navLogo.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        navCommunity.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CommunityActivity.class);
            startActivity(intent);
            finish();
        });

        navChallenges.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChallengesActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            // Already in ProfileActivity, no need to navigate
            Toast.makeText(this, "You are already on the Profile page", Toast.LENGTH_SHORT).show();
        });

        navNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, NotificationsActivity.class);
            startActivity(intent);
            finish();
        });

        navMore.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MoreActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize views
        initializeViews();

        // Setup profile photo selection
        setupProfilePhotoSelection();

        // Set dummy data
        setDummyData();
    }

    private void initializeViews() {
        profilePhoto = findViewById(R.id.profilePhoto);
        ImageView editPhoto = findViewById(R.id.editPhoto);
        
        // Set click listener for edit photo button
        editPhoto.setOnClickListener(v -> showPhotoSelectionDialog());
    }

    private void setupProfilePhotoSelection() {
        // Setup image picker launcher
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    profilePhoto.setImageURI(selectedImage);
                }
            }
        );

        // Setup camera launcher
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Handle camera result
                    Toast.makeText(this, "Photo captured successfully", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    private void showPhotoSelectionDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Profile Photo");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Take Photo
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    cameraLauncher.launch(takePictureIntent);
                }
            } else if (which == 1) {
                // Choose from Gallery
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(pickPhotoIntent);
            }
        });
        builder.show();
    }

    private void setDummyData() {
        // Personal Info
        ((TextView) findViewById(R.id.firstName)).setText("John");
        ((TextView) findViewById(R.id.lastName)).setText("Doe");
        ((TextView) findViewById(R.id.gender)).setText("Male");
        ((TextView) findViewById(R.id.age)).setText("28");
        ((TextView) findViewById(R.id.dateOfBirth)).setText("1995-05-15");

        // Health Stats
        ((TextView) findViewById(R.id.height)).setText("175 cm");
        ((TextView) findViewById(R.id.weight)).setText("70 kg");
        ((TextView) findViewById(R.id.bmi)).setText("22.9");
        ((TextView) findViewById(R.id.bloodPressure)).setText("120/80 mmHg");
    }
} 