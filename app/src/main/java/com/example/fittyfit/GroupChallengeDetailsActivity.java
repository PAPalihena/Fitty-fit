package com.example.fittyfit;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.fittyfit.models.Challenge;
import com.example.fittyfit.utils.FirebaseDatabaseHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroupChallengeDetailsActivity extends AppCompatActivity {
    private FirebaseDatabaseHelper firebaseHelper;
    private DatabaseReference database;
    private String challengeId;
    private String currentUserId;
    private TextView titleView;
    private TextView descriptionView;
    private ProgressBar progressBar;
    private TextView progressText;
    private TextView prizeInfo;
    private TextView durationInfo;
    private RecyclerView participantsRecyclerView;
    private MaterialButton joinButton;
    private ParticipantsAdapter participantsAdapter;
    private Challenge currentChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_challenge_details);

        // Initialize Firebase
        firebaseHelper = new FirebaseDatabaseHelper();
        database = FirebaseDatabase.getInstance().getReference();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get challenge ID from intent
        challengeId = getIntent().getStringExtra("challenge_id");
        if (challengeId == null || challengeId.isEmpty()) {
            Toast.makeText(this, "Invalid challenge", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadChallengeDetails();
    }

    private void initializeViews() {
        titleView = findViewById(R.id.challengeTitle);
        descriptionView = findViewById(R.id.challengeDescription);
        progressBar = findViewById(R.id.challengeProgress);
        progressText = findViewById(R.id.progressText);
        prizeInfo = findViewById(R.id.prizeInfo);
        durationInfo = findViewById(R.id.durationInfo);
        participantsRecyclerView = findViewById(R.id.participantsRecyclerView);
        joinButton = findViewById(R.id.joinChallengeButton);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setupRecyclerView() {
        participantsAdapter = new ParticipantsAdapter(new ArrayList<>());
        participantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        participantsRecyclerView.setAdapter(participantsAdapter);
    }

    private void loadChallengeDetails() {
        firebaseHelper.getChallengeDetails(challengeId, challenge -> {
            if (challenge != null) {
                currentChallenge = challenge;
                runOnUiThread(() -> {
                    // Set challenge details
                    titleView.setText(challenge.getTitle());
                    descriptionView.setText(challenge.getDescription());
                    
                    // Update circular progress
                    progressBar.setProgress(challenge.getProgress());
                    progressText.setText(challenge.getProgress() + "%");
                    
                    // Set prize with gold color
                    prizeInfo.setText(challenge.getPrize());

                    // Calculate and set duration
                    String duration = calculateDuration(challenge.getStartDate(), challenge.getEndDate());
                    durationInfo.setText(duration);

                    // Update participants list
                    List<Participant> participants = new ArrayList<>();
                    for (Map.Entry<String, Map<String, Object>> entry : challenge.getParticipants().entrySet()) {
                        Map<String, Object> data = entry.getValue();
                        String name = (String) data.get("name");
                        Boolean isLeader = (Boolean) data.get("isLeader");
                        Integer progress = ((Number) data.get("progress")).intValue();
                        String profileImage = (String) data.get("profileImage");

                        participants.add(new Participant(name, isLeader, progress, profileImage));
                    }
                    participantsAdapter.updateParticipants(participants);

                    // Update join button state
                    boolean isParticipant = challenge.getParticipants().containsKey(currentUserId);
                    joinButton.setText(isParticipant ? "Leave Challenge" : "Join Challenge");
                    joinButton.setOnClickListener(v -> {
                        if (isParticipant) {
                            leaveChallenge();
                        } else {
                            joinChallenge();
                        }
                    });
                });
            } else {
                Toast.makeText(this, "Error loading challenge details", Toast.LENGTH_SHORT).show();
                finish();
            }
            return null;
        });
    }

    private String calculateDuration(String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            
            if (start != null && end != null) {
                long diff = end.getTime() - start.getTime();
                long days = diff / (24 * 60 * 60 * 1000);
                return days + " days";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Duration not available";
    }

    private void joinChallenge() {
        if (currentChallenge == null) return;

        // Get current user's display name
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (userName == null) userName = "Anonymous User";

        // Create participant data
        Map<String, Object> participantData = new HashMap<>();
        participantData.put("name", userName);
        participantData.put("isLeader", false);
        participantData.put("progress", 0);
        participantData.put("profileImage", ""); // TODO: Add user's profile image URL

        // Update challenge in Firebase
        database.child("user_challenges")
                .child("group_challenges")
                .child(challengeId)
                .child("participants")
                .child(currentUserId)
                .setValue(participantData)
                .addOnSuccessListener(aVoid -> {
                    // Update total participants count
                    int newTotal = currentChallenge.getTotalParticipants() + 1;
                    database.child("user_challenges")
                            .child("group_challenges")
                            .child(challengeId)
                            .child("totalParticipants")
                            .setValue(newTotal)
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(this, "Successfully joined the challenge!", Toast.LENGTH_SHORT).show();
                                loadChallengeDetails(); // Reload the challenge details
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error joining challenge: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void leaveChallenge() {
        if (currentChallenge == null) return;

        // Remove participant from Firebase
        database.child("user_challenges")
                .child("group_challenges")
                .child(challengeId)
                .child("participants")
                .child(currentUserId)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Update total participants count
                    int newTotal = currentChallenge.getTotalParticipants() - 1;
                    database.child("user_challenges")
                            .child("group_challenges")
                            .child(challengeId)
                            .child("totalParticipants")
                            .setValue(newTotal)
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(this, "Successfully left the challenge", Toast.LENGTH_SHORT).show();
                                loadChallengeDetails(); // Reload the challenge details
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error leaving challenge: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Participant data class
    private static class Participant {
        String name;
        boolean isLeader;
        int progress;
        String profileImage;

        Participant(String name, boolean isLeader, int progress, String profileImage) {
            this.name = name;
            this.isLeader = isLeader;
            this.progress = progress;
            this.profileImage = profileImage;
        }
    }

    // Participants adapter
    private class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {
        private List<Participant> participants;

        ParticipantsAdapter(List<Participant> participants) {
            this.participants = participants;
        }

        void updateParticipants(List<Participant> newParticipants) {
            this.participants = newParticipants;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = getLayoutInflater().inflate(R.layout.item_participant, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Participant participant = participants.get(position);
            holder.nameView.setText(participant.name);
            holder.progressView.setText(participant.progress + "%");
            holder.crownIcon.setVisibility(participant.isLeader ? android.view.View.VISIBLE : android.view.View.GONE);
            
            // Load profile image using Glide
            if (participant.profileImage != null && !participant.profileImage.isEmpty()) {
                Glide.with(GroupChallengeDetailsActivity.this)
                    .load(participant.profileImage)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .circleCrop()
                    .into(holder.avatarView);
            } else {
                holder.avatarView.setImageResource(R.drawable.ic_profile_placeholder);
            }
        }

        @Override
        public int getItemCount() {
            return participants.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameView;
            TextView progressView;
            android.widget.ImageView crownIcon;
            android.widget.ImageView avatarView;

            ViewHolder(android.view.View itemView) {
                super(itemView);
                nameView = itemView.findViewById(R.id.participantName);
                progressView = itemView.findViewById(R.id.participantProgress);
                crownIcon = itemView.findViewById(R.id.crownIcon);
                avatarView = itemView.findViewById(R.id.participantAvatar);
            }
        }
    }
} 