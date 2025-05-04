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

        try {
            // Setup navigation bar
            setupNavigationBar();

            // Initialize Firebase
            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() == null) {
                Log.e(TAG, "No user is currently signed in");
                Toast.makeText(this, "Please sign in to view your profile", Toast.LENGTH_LONG).show();
                // Redirect to login activity
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            String userId = mAuth.getCurrentUser().getUid();
            Log.d(TAG, "Current user ID: " + userId);
            
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            Log.d(TAG, "Database reference path: " + userRef.toString());

            // Initialize views
            initializeViews();

            // Setup profile photo selection
            setupProfilePhotoSelection();

            // Load user data from Firebase
            loadUserData();

            // Setup button click listeners
            setupButtonListeners();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        try {
            profilePhoto = findViewById(R.id.profilePhotoImageView);
            ImageView editPhoto = findViewById(R.id.editPhoto);
            
            if (profilePhoto == null || editPhoto == null) {
                throw new IllegalStateException("Required views not found in layout");
            }
            
            // Set click listener for edit photo button
            editPhoto.setOnClickListener(v -> showPhotoSelectionDialog());
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            Toast.makeText(this, "Error initializing profile views", Toast.LENGTH_SHORT).show();
        }
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
        try {
            // Get current user
            if (mAuth.getCurrentUser() == null) {
                Log.e(TAG, "No user is currently signed in");
                return;
            }

            String userId = mAuth.getCurrentUser().getUid();
            Log.d(TAG, "Current user ID: " + userId);

            // Get reference to user data
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            Log.d(TAG, "Database reference path: " + userRef.toString());

            // Add value event listener
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        Log.d(TAG, "Data snapshot exists: " + dataSnapshot.exists());
                        Log.d(TAG, "Data snapshot value: " + dataSnapshot.getValue());
                        
                        if (!dataSnapshot.exists()) {
                            Log.e(TAG, "No data found for user");
                            createDefaultUserData();
                            return;
                        }

                        // Try to get user data
                        User user = dataSnapshot.getValue(User.class);
                        Log.d(TAG, "User data retrieved: " + (user != null));
                        
                        if (user != null) {
                            // Log individual fields
                            Log.d(TAG, "First Name: " + user.getFirstName());
                            Log.d(TAG, "Last Name: " + user.getLastName());
                            Log.d(TAG, "Date of Birth: " + user.getDateOfBirth());
                            Log.d(TAG, "Age: " + user.getAge());
                            Log.d(TAG, "Gender: " + user.getGender());
                            
                            updateUIWithUserData(user);
                        } else {
                            Log.e(TAG, "Failed to parse user data");
                            Toast.makeText(ProfileActivity.this, 
                                "Error loading profile data", 
                                Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in onDataChange: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, 
                            "Error updating profile data: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Database error: " + databaseError.getMessage());
                    Log.e(TAG, "Database error details: " + databaseError.getDetails());
                    Toast.makeText(ProfileActivity.this, 
                        "Failed to load user data: " + databaseError.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up data listener: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error setting up profile data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUIWithUserData(User user) {
        try {
            // Update Personal Info
            TextView firstNameView = findViewById(R.id.firstNameTextView);
            TextView lastNameView = findViewById(R.id.lastNameTextView);
            TextView dobView = findViewById(R.id.dobTextView);
            TextView ageView = findViewById(R.id.ageTextView);
            TextView genderView = findViewById(R.id.genderTextView);

            // Update Health Stats
            TextView heightView = findViewById(R.id.heightTextView);
            TextView weightView = findViewById(R.id.weightTextView);
            TextView bloodPressureView = findViewById(R.id.bloodPressureTextView);
            TextView bmiView = findViewById(R.id.bmiTextView);

            // Set Personal Info
            if (firstNameView != null) {
                firstNameView.setText(user.getFirstName() != null ? user.getFirstName() : "");
                Log.d(TAG, "First Name: " + user.getFirstName());
            }
            
            if (lastNameView != null) {
                lastNameView.setText(user.getLastName() != null ? user.getLastName() : "");
                Log.d(TAG, "Last Name: " + user.getLastName());
            }
            
            if (dobView != null) {
                dobView.setText(user.getDateOfBirth() != null ? user.getDateOfBirth() : "");
                Log.d(TAG, "Date of Birth: " + user.getDateOfBirth());
            }
            
            if (ageView != null) {
                ageView.setText(String.valueOf(user.getAge()));
                Log.d(TAG, "Age: " + user.getAge());
            }
            
            if (genderView != null) {
                genderView.setText(user.getGender() != null ? user.getGender() : "");
                Log.d(TAG, "Gender: " + user.getGender());
            }

            // Set Health Stats
            if (heightView != null) {
                heightView.setText(user.getHeight() + " cm");
                Log.d(TAG, "Height: " + user.getHeight());
            }
            
            if (weightView != null) {
                weightView.setText(user.getWeight() + " kg");
                Log.d(TAG, "Weight: " + user.getWeight());
            }
            
            if (bloodPressureView != null) {
                bloodPressureView.setText(user.getBloodPressure() != null ? user.getBloodPressure() : "");
                Log.d(TAG, "Blood Pressure: " + user.getBloodPressure());
            }
            
            if (bmiView != null) {
                bmiView.setText(calculateBMI(user.getHeight(), user.getWeight()));
                Log.d(TAG, "BMI: " + calculateBMI(user.getHeight(), user.getWeight()));
            }

            // Load profile image if available
            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty() && profilePhoto != null) {
                Picasso.get()
                    .load(user.getProfileImageUrl())
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(profilePhoto);
            } else if (profilePhoto != null) {
                // Set default profile image if no URL is available
                profilePhoto.setImageResource(R.drawable.default_profile);
            }
            
            Log.d(TAG, "Successfully updated UI with user data");
        } catch (Exception e) {
            Log.e(TAG, "Error updating UI: " + e.getMessage());
            Toast.makeText(this, "Error displaying profile data", Toast.LENGTH_SHORT).show();
        }
    }

    private String calculateBMI(int height, int weight) {
        try {
            if (height > 0 && weight > 0) {
                double heightInMeters = height / 100.0;
                double bmi = weight / (heightInMeters * heightInMeters);
                return String.format("%.1f", bmi);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculating BMI: " + e.getMessage());
        }
        return "N/A";
    }

    private void createDefaultUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        
        User defaultUser = new User(
            userId,          // uid
            "New",          // firstName
            "User"          // lastName
        );
        
        // Set additional default values
        defaultUser.setDateOfBirth("");
        defaultUser.setAge(0);
        defaultUser.setGender("");
        defaultUser.setHeight(0);
        defaultUser.setWeight(0);
        defaultUser.setBloodPressure("");
        defaultUser.setProfileImageUrl("");

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

    private void setupButtonListeners() {
        try {
            MaterialButton editProfileButton = findViewById(R.id.editProfileButton);
            MaterialButton accountSettingsButton = findViewById(R.id.accountSettingsButton);

            if (editProfileButton == null || accountSettingsButton == null) {
                throw new IllegalStateException("Required buttons not found in layout");
            }

            editProfileButton.setOnClickListener(v -> {
                // TODO: Implement edit profile functionality
                Toast.makeText(this, "Edit Profile functionality coming soon!", Toast.LENGTH_SHORT).show();
            });

            accountSettingsButton.setOnClickListener(v -> {
                // TODO: Implement account settings functionality
                Toast.makeText(this, "Account Settings functionality coming soon!", Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up button listeners: " + e.getMessage());
            Toast.makeText(this, "Error setting up profile buttons", Toast.LENGTH_SHORT).show();
        }
    }
} 