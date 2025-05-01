package com.example.fittyfit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fittyfit.adapter.CommunityAdapter;
import com.example.fittyfit.model.CommunityPost;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CommunityActivity extends BaseActivity {

    private RecyclerView communityRecyclerView;
    private CommunityAdapter communityAdapter;
    private List<CommunityPost> postList;
    private FloatingActionButton fabAddPost;

    private AlertDialog addPostDialog;
    private ImageView imageViewAttachedImage;
    private Uri selectedImageUri = null;

    // ActivityResultLauncher for picking an image
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (addPostDialog != null && imageViewAttachedImage != null) {
                        imageViewAttachedImage.setImageURI(selectedImageUri);
                        imageViewAttachedImage.setVisibility(View.VISIBLE);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        // Setup navigation bar
        setupNavigationBar();

        // --- Temporarily Commented Out for Debugging ---
        //* // Uncomment Toolbar section
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) { 
            actionBar.setTitle("Fit Community");
            actionBar.setDisplayHomeAsUpEnabled(true); // Add back button
        }
        //*/

        /* // Keep the rest commented for now */ // Reverted comment type
        // Uncomment RecyclerView, FAB, and list creation
        communityRecyclerView = findViewById(R.id.communityRecyclerView);
        fabAddPost = findViewById(R.id.fabAddPost);

        postList = new ArrayList<>();
        // Add dummy posts with images
        postList.add(new CommunityPost("Alice", "Just finished a 5k run! Feeling great! #fitness", R.drawable.dummy_post_image_1));
        postList.add(new CommunityPost("Bob", "Tried a new healthy recipe today. Delicious and nutritious!", 0)); // 0 means no image
        postList.add(new CommunityPost("Charlie", "Yoga session done. So relaxing.", R.drawable.dummy_post_image_2));
        postList.add(new CommunityPost("David", "Hit my step goal for the 5th day in a row! #consistency", 0)); // 0 means no image

        /* // Keep adapter setup commented */ // Reverted previous comment
        // Uncomment Adapter setup and FAB listener
        communityAdapter = new CommunityAdapter(this, postList);
        communityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        communityRecyclerView.setAdapter(communityAdapter);

        fabAddPost.setOnClickListener(v -> showAddPostDialog());
        // */ // End of uncommented section
        // --- End of Temporarily Commented Out ---

        // Initialize views
        communityRecyclerView = findViewById(R.id.communityRecyclerView);
        fabAddPost = findViewById(R.id.fabAddPost);

        // Setup RecyclerView
        communityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        communityAdapter = new CommunityAdapter(this, new ArrayList<>());
        communityRecyclerView.setAdapter(communityAdapter);

        // Add dummy posts
        addDummyPosts();

        // Setup FAB click listener
        fabAddPost.setOnClickListener(v -> {
            Toast.makeText(this, "Create new post coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    /* // Temporarily comment out helper methods as well */ // Reverted previous comment
    // Uncomment helper methods
    private void showAddPostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_post, null);
        builder.setView(dialogView);

        EditText editTextPostContent = dialogView.findViewById(R.id.editTextPostContent);
        Button btnAttachImage = dialogView.findViewById(R.id.btnAttachImage);
        imageViewAttachedImage = dialogView.findViewById(R.id.imageViewAttachedImage);
        Button btnDiscard = dialogView.findViewById(R.id.btnDiscard);
        Button btnPost = dialogView.findViewById(R.id.btnPost);

        // Reset state for new dialog
        imageViewAttachedImage.setVisibility(View.GONE);
        imageViewAttachedImage.setImageURI(null);
        selectedImageUri = null;

        btnAttachImage.setOnClickListener(v -> openImagePicker());

        btnDiscard.setOnClickListener(v -> addPostDialog.dismiss());

        btnPost.setOnClickListener(v -> {
            String postContent = editTextPostContent.getText().toString().trim();
            if (postContent.isEmpty()) {
                Toast.makeText(this, "Post content cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Replace "CurrentUser" with actual logged-in user name
            // Pass 0 for imageResId as new posts from dialog don't have a predefined drawable
            CommunityPost newPost = new CommunityPost("CurrentUser", postContent, 0);
            // TODO: Handle image upload if selectedImageUri is not null

            postList.add(0, newPost); // Add new post to the top
            communityAdapter.notifyItemInserted(0);
            communityRecyclerView.scrollToPosition(0); // Scroll to show the new post
            addPostDialog.dismiss();
            Toast.makeText(this, "Post added!", Toast.LENGTH_SHORT).show();
        });

        addPostDialog = builder.create();
        addPostDialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }
    // */ // End of uncommented section

    private void addDummyPosts() {
        List<CommunityPost> dummyPosts = new ArrayList<>();
        
        // Post 1 - With image
        dummyPosts.add(new CommunityPost(
            "Alice W.",
            "Just finished a 5k run! Feeling great! #fitness",
            R.drawable.dummy_post_image_1
        ));

        // Post 2 - Without image
        dummyPosts.add(new CommunityPost(
            "Bob M.",
            "Tried a new healthy recipe today. Delicious and nutritious!",
            "1 hour ago",
            15,
            3,
            1
        ));

        // Post 3 - With image
        dummyPosts.add(new CommunityPost(
            "Charlie P.",
            "Yoga session done. So relaxing.",
            R.drawable.dummy_post_image_2
        ));

        // Post 4 - Without image
        dummyPosts.add(new CommunityPost(
            "David W.",
            "Hit my step goal for the 5th day in a row! #consistency",
            "2 hours ago",
            28,
            7,
            2
        ));

        // Post 5 - With image
        dummyPosts.add(new CommunityPost(
            "Emma W.",
            "New personal best in deadlift! 💪",
            R.drawable.dummy_post_image_3
        ));

        // Post 6 - Without image
        dummyPosts.add(new CommunityPost(
            "Frank H.",
            "Morning meditation session. Starting the day right!",
            "3 hours ago",
            19,
            4,
            1
        ));

        // Post 7 - With image
        dummyPosts.add(new CommunityPost(
            "Grace D.",
            "Beautiful sunset run today!",
            R.drawable.dummy_post_image_4
        ));

        // Post 8 - Without image
        dummyPosts.add(new CommunityPost(
            "Henry P.",
            "Just joined a new cycling group. The community is amazing!",
            "4 hours ago",
            32,
            8,
            3
        ));

        // Post 9 - With image
        dummyPosts.add(new CommunityPost(
            "Ivy R.",
            "Meal prep Sunday! Healthy eating is key.",
            R.drawable.dummy_post_image_5
        ));

        // Post 10 - Without image
        dummyPosts.add(new CommunityPost(
            "Jack D.",
            "Recovery day with some light stretching. Taking care of your body is crucial!",
            "5 hours ago",
            24,
            6,
            2
        ));

        communityAdapter = new CommunityAdapter(this, dummyPosts);
        communityRecyclerView.setAdapter(communityAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle the back button press in the toolbar
        finish();
        return true;
    }
} 