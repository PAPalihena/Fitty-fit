package com.example.fittyfit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;
import com.example.fittyfit.models.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroupChallenge extends BaseActivity {
    private TextInputEditText challengeTitleInput, startDateInput, endDateInput, prizeInput;
    private MaterialButton addParticipantsButton, createChallengeButton;
    private RecyclerView participantsRecyclerView;
    private List<String> selectedParticipants;
    private List<User> selectedUsers;
    private ImageView challengeImage;
    private Uri selectedImageUri;
    private String imageUrl = "";
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ParticipantsAdapter participantsAdapter;
    private SearchResultsAdapter searchResultsAdapter;
    private SelectedMembersAdapter selectedMembersAdapter;

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
        new ActivityResultContracts.GetContent(),
        uri -> {
            if (uri != null) {
                selectedImageUri = uri;
                Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .into(challengeImage);
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize Firebase first
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Set the base layout and content
        setContentLayout(R.layout.activity_create_group_challenge);

        // Initialize views
        initializeViews();
        setupDatePickers();
        setupClickListeners();
    }

    private void initializeViews() {
        challengeTitleInput = findViewById(R.id.challengeTitleInput);
        startDateInput = findViewById(R.id.startDateInput);
        endDateInput = findViewById(R.id.endDateInput);
        prizeInput = findViewById(R.id.prizeInput);
        addParticipantsButton = findViewById(R.id.addParticipantsButton);
        createChallengeButton = findViewById(R.id.createChallengeButton);
        participantsRecyclerView = findViewById(R.id.participantsRecyclerView);
        challengeImage = findViewById(R.id.challengeImage);

        selectedParticipants = new ArrayList<>();
        selectedUsers = new ArrayList<>();
        participantsAdapter = new ParticipantsAdapter(selectedUsers);
        participantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        participantsRecyclerView.setAdapter(participantsAdapter);

        // Set up image click listener
        challengeImage.setOnClickListener(v -> pickImage.launch("image/*"));
    }

    private void setupDatePickers() {
        Calendar calendar = Calendar.getInstance();
        
        startDateInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    startDateInput.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        endDateInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    endDateInput.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void setupClickListeners() {
        addParticipantsButton.setOnClickListener(v -> showAddMembersDialog());
        createChallengeButton.setOnClickListener(v -> createChallenge());
    }

    private void showAddMembersDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_members);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextInputEditText searchInput = dialog.findViewById(R.id.searchMemberInput);
        RecyclerView searchResultsRecyclerView = dialog.findViewById(R.id.searchResultsRecyclerView);
        RecyclerView selectedMembersRecyclerView = dialog.findViewById(R.id.selectedMembersRecyclerView);
        MaterialButton inviteButton = dialog.findViewById(R.id.inviteButton);

        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectedMembersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchResultsAdapter = new SearchResultsAdapter(new ArrayList<>(), user -> {
            if (!selectedUsers.contains(user)) {
                selectedUsers.add(user);
                selectedParticipants.add(user.getUid());
                selectedMembersAdapter.notifyDataSetChanged();
                participantsAdapter.notifyDataSetChanged();
            }
        });

        selectedMembersAdapter = new SelectedMembersAdapter(selectedUsers, user -> {
            selectedUsers.remove(user);
            selectedParticipants.remove(user.getUid());
            selectedMembersAdapter.notifyDataSetChanged();
            participantsAdapter.notifyDataSetChanged();
        });

        searchResultsRecyclerView.setAdapter(searchResultsAdapter);
        selectedMembersRecyclerView.setAdapter(selectedMembersAdapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        inviteButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void searchUsers(String query) {
        if (query.isEmpty()) {
            searchResultsAdapter.updateUsers(new ArrayList<>());
            return;
        }

        databaseReference.child("users")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<User> searchResults = new ArrayList<>();
                    String currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
                    
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && !user.getUid().equals(currentUserId)) {
                            String fullName = (user.getFirstName() + " " + user.getLastName()).toLowerCase();
                            if (fullName.contains(query.toLowerCase())) {
                                searchResults.add(user);
                            }
                        }
                    }
                    searchResultsAdapter.updateUsers(searchResults);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(CreateGroupChallenge.this, "Error searching users", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void createChallenge() {
        String title = challengeTitleInput.getText().toString().trim();
        String startDate = startDateInput.getText().toString().trim();
        String endDate = endDateInput.getText().toString().trim();
        String prize = prizeInput.getText().toString().trim();

        if (title.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || prize.isEmpty() || selectedParticipants.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and add participants", Toast.LENGTH_SHORT).show();
            return;
        }

        // First upload the image if one was selected
        if (selectedImageUri != null) {
            uploadImageAndCreateChallenge(title, startDate, endDate, prize);
        } else {
            createChallengeInDatabase(title, startDate, endDate, prize, "");
        }
    }

    private void uploadImageAndCreateChallenge(String title, String startDate, String endDate, String prize) {
        String imageId = databaseReference.push().getKey();
        StorageReference imageRef = storageReference.child("challenge_images/" + imageId);

        imageRef.putFile(selectedImageUri)
            .addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        createChallengeInDatabase(title, startDate, endDate, prize, uri.toString());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CreateGroupChallenge.this, 
                            "Error getting image URL: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    });
            })
            .addOnFailureListener(e -> {
                Toast.makeText(CreateGroupChallenge.this, 
                    "Error uploading image: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }

    private void createChallengeInDatabase(String title, String startDate, String endDate, String prize, String imageUrl) {
        String challengeId = databaseReference.child("challenges").push().getKey();
        Map<String, Object> challenge = new HashMap<>();
        challenge.put("title", title);
        challenge.put("startDate", startDate);
        challenge.put("endDate", endDate);
        challenge.put("prize", prize);
        challenge.put("type", "group");
        challenge.put("createdBy", auth.getCurrentUser().getUid());
        challenge.put("participants", selectedParticipants);
        challenge.put("status", "active");
        challenge.put("imageUrl", imageUrl);
        challenge.put("createdAt", System.currentTimeMillis());

        databaseReference.child("challenges").child(challengeId).setValue(challenge)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(CreateGroupChallenge.this, "Challenge created successfully!", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(CreateGroupChallenge.this, "Error creating challenge: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    // Adapter classes
    private static class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {
        private final List<User> users;

        ParticipantsAdapter(List<User> users) {
            this.users = users;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_participant, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            User user = users.get(position);
            holder.nameText.setText(user.getFirstName() + " " + user.getLastName());
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText;

            ViewHolder(View view) {
                super(view);
                nameText = view.findViewById(R.id.participantName);
            }
        }
    }

    private static class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
        private List<User> users;
        private final OnUserSelectedListener listener;

        interface OnUserSelectedListener {
            void onUserSelected(User user);
        }

        SearchResultsAdapter(List<User> users, OnUserSelectedListener listener) {
            this.users = users;
            this.listener = listener;
        }

        void updateUsers(List<User> newUsers) {
            this.users = newUsers;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            User user = users.get(position);
            holder.nameText.setText(user.getFirstName() + " " + user.getLastName());
            holder.itemView.setOnClickListener(v -> listener.onUserSelected(user));
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText;

            ViewHolder(View view) {
                super(view);
                nameText = view.findViewById(R.id.userName);
            }
        }
    }

    private static class SelectedMembersAdapter extends RecyclerView.Adapter<SelectedMembersAdapter.ViewHolder> {
        private final List<User> users;
        private final OnUserRemovedListener listener;

        interface OnUserRemovedListener {
            void onUserRemoved(User user);
        }

        SelectedMembersAdapter(List<User> users, OnUserRemovedListener listener) {
            this.users = users;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_member, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            User user = users.get(position);
            holder.nameText.setText(user.getFirstName() + " " + user.getLastName());
            holder.removeButton.setOnClickListener(v -> listener.onUserRemoved(user));
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText;
            MaterialButton removeButton;

            ViewHolder(View view) {
                super(view);
                nameText = view.findViewById(R.id.selectedUserName);
                removeButton = view.findViewById(R.id.removeButton);
            }
        }
    }
} 