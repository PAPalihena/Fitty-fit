package com.example.fittyfit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

import com.example.fittyfit.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends BaseActivity {
    private static final String TAG = "ProfileActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView profilePhoto;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Setup navigation bar
        setupNavigationBar();

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Log.e(TAG, "No user is currently signed in");
            Toast.makeText(this, "Please sign in to view your profile", Toast.LENGTH_LONG).show();
            // Redirect to login activity or handle accordingly
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "Current user ID: " + userId);
        
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        Log.d(TAG, "Database reference path: " + userRef.toString());

        // Test database connection
        testDatabaseConnection();

        // Initialize views
        initializeViews();

        // Setup profile photo selection
        setupProfilePhotoSelection();

        // Load user data from Firebase
        loadUserData();

        // Setup button click listeners
        setupButtonListeners();
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
                    // TODO: Upload image to Firebase Storage and update profileImageUrl
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
                    // TODO: Upload image to Firebase Storage and update profileImageUrl
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

    private void loadUserData() {
        Log.d(TAG, "Starting to load user data");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Data snapshot exists: " + dataSnapshot.exists());
                if (!dataSnapshot.exists()) {
                    Log.e(TAG, "No data found for user");
                    // Create default user data
                    createDefaultUserData();
                    return;
                }

                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "User data retrieved: " + (user != null));
                
                if (user != null) {
                    try {
                        // Update Personal Info
                        TextView firstNameView = findViewById(R.id.firstName);
                        TextView lastNameView = findViewById(R.id.lastName);
                        TextView genderView = findViewById(R.id.gender);
                        TextView ageView = findViewById(R.id.age);
                        TextView dobView = findViewById(R.id.dateOfBirth);

                        if (firstNameView != null) firstNameView.setText(user.getFirstName());
                        if (lastNameView != null) lastNameView.setText(user.getLastName());
                        if (genderView != null) genderView.setText(user.getGender());
                        if (ageView != null) ageView.setText(String.valueOf(user.getAge()));
                        if (dobView != null) dobView.setText(user.getDateOfBirth());

                        // Update Health Stats
                        TextView heightView = findViewById(R.id.height);
                        TextView weightView = findViewById(R.id.weight);
                        TextView bmiView = findViewById(R.id.bmi);
                        TextView bloodPressureView = findViewById(R.id.bloodPressure);

                        if (heightView != null) heightView.setText(user.getHeight() + " cm");
                        if (weightView != null) weightView.setText(user.getWeight() + " kg");
                        if (bmiView != null) bmiView.setText(String.valueOf(user.getBmi()));
                        if (bloodPressureView != null) bloodPressureView.setText(user.getBloodPressure());

                        // Load profile image if available
                        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                            Picasso.get()
                                .load(user.getProfileImageUrl())
                                .placeholder(R.drawable.default_profile)
                                .error(R.drawable.default_profile)
                                .into(profilePhoto);
                        }
                        
                        Log.d(TAG, "Successfully updated UI with user data");
                    } catch (Exception e) {
                        Log.e(TAG, "Error updating UI: " + e.getMessage());
                        Toast.makeText(ProfileActivity.this, 
                            "Error displaying profile data", 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Failed to parse user data");
                    Toast.makeText(ProfileActivity.this, 
                        "Error loading profile data", 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
                Toast.makeText(ProfileActivity.this, 
                    "Failed to load user data: " + databaseError.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createDefaultUserData() {
        User defaultUser = new User(
            "John",           // firstName
            "Doe",           // lastName
            "Male",          // gender
            28,              // age
            "1995-05-15",    // dateOfBirth
            175f,            // height
            70f,             // weight
            22.9f,           // bmi
            "120/80",        // bloodPressure
            ""               // profileImageUrl
        );

        userRef.setValue(defaultUser)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Default user data created successfully");
                Toast.makeText(ProfileActivity.this, 
                    "Profile created successfully", 
                    Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error creating default user data: " + e.getMessage());
                Toast.makeText(ProfileActivity.this, 
                    "Error creating profile: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }

    private void testDatabaseConnection() {
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                Log.d(TAG, "Raw data from Firebase: " + snapshot.getValue());
                
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Log.d(TAG, "Child: " + child.getKey() + " = " + child.getValue());
                    }
                } else {
                    Log.e(TAG, "No data exists at this location");
                }
            } else {
                Log.e(TAG, "Error getting data", task.getException());
            }
        });
    }

    private void setupButtonListeners() {
        MaterialButton editProfileButton = findViewById(R.id.editProfileButton);
        MaterialButton accountSettingsButton = findViewById(R.id.accountSettingsButton);

        editProfileButton.setOnClickListener(v -> {
            // TODO: Implement edit profile functionality
            Toast.makeText(this, "Edit Profile functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        accountSettingsButton.setOnClickListener(v -> {
            // TODO: Implement account settings functionality
            Toast.makeText(this, "Account Settings functionality coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
} 