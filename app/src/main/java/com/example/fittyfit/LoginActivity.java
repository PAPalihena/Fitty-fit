package com.example.fittyfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends BaseActivity {
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ImageView logoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set up Google Sign In button
        MaterialButton googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(v -> signIn());

        // Set up logo animation
        logoImageView = findViewById(R.id.logoImageView);
        startLogoAnimation();
    }

    private void startLogoAnimation() {
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        logoAnimation.setRepeatCount(Animation.INFINITE);
        logoAnimation.setRepeatMode(Animation.REVERSE);
        logoImageView.startAnimation(logoAnimation);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                } else {
                    Toast.makeText(this, "Google sign in failed: Account is null", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                // Handle specific error codes
                String errorMessage;
                switch (e.getStatusCode()) {
                    case 10:
                        errorMessage = "Please check your internet connection and try again";
                        break;
                    case 7:
                        errorMessage = "Network error. Please check your connection";
                        break;
                    case 8:
                        errorMessage = "Internal error. Please try again";
                        break;
                    default:
                        errorMessage = "Google sign in failed: " + e.getMessage();
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        if (idToken == null || idToken.isEmpty()) {
            Toast.makeText(this, "Authentication failed: Invalid token", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Check if user exists in database
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                            
                            userRef.get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    if (task1.getResult().exists()) {
                                        // User exists, go to HomeActivity
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        finish();
                                    } else {
                                        // User doesn't exist, go to RegisterActivity
                                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                                        finish();
                                    }
                                } else {
                                    // Error checking user existence
                                    Toast.makeText(LoginActivity.this, 
                                        "Error checking user data: " + task1.getException().getMessage(), 
                                        Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            String errorMessage = "Authentication failed";
                            if (task.getException() != null) {
                                errorMessage += ": " + task.getException().getMessage();
                            }
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
} 