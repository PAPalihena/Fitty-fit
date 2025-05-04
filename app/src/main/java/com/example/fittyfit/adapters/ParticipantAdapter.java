package com.example.fittyfit.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fittyfit.R;
import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder> {
    private List<String> participants;

    public ParticipantAdapter(List<String> participants) {
        this.participants = participants;
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_participant, parent, false);
        return new ParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        String participant = participants.get(position);
        holder.participantName.setText(participant);
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public void updateParticipants(List<String> newParticipants) {
        this.participants = newParticipants;
        notifyDataSetChanged();
    }

    static class ParticipantViewHolder extends RecyclerView.ViewHolder {
        TextView participantName;

        ParticipantViewHolder(View itemView) {
            super(itemView);
            participantName = itemView.findViewById(R.id.participantName);
        }
    }
} 