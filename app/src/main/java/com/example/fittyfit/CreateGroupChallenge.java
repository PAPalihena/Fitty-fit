package com.example.fittyfit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_challenge);

        // Setup navigation bar
        setupNavigationBar();

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

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

        selectedParticipants = new ArrayList<>();
        participantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Set up adapter for participants
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

        // TODO: Set up adapters for search results and selected members

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

        inviteButton.setOnClickListener(v -> {
            // TODO: Update selected participants list
            dialog.dismiss();
        });

        dialog.show();
    }

    private void searchUsers(String query) {
        if (query.isEmpty()) return;

        databaseReference.child("users")
            .orderByChild("name")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<User> searchResults = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && !user.getUid().equals(auth.getCurrentUser().getUid())) {
                            searchResults.add(user);
                        }
                    }
                    // TODO: Update search results adapter
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

        databaseReference.child("challenges").child(challengeId).setValue(challenge)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(CreateGroupChallenge.this, "Group challenge created successfully!", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(CreateGroupChallenge.this, "Error creating challenge: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
} 