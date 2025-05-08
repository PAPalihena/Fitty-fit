package com.example.fittyfit.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fittyfit.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.util.List;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder> {
    private List<ProgressItem> progressItems;
    private int lastPosition = -1;

    public ProgressAdapter(List<ProgressItem> progressItems) {
        this.progressItems = progressItems;
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_progress, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        ProgressItem item = progressItems.get(position);
        holder.bind(item);
        
        // Apply animation
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_animation);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return progressItems.size();
    }

    public void updateProgress(int position, int progress) {
        if (position >= 0 && position < progressItems.size()) {
            progressItems.get(position).setProgress(progress);
            notifyItemChanged(position);
        }
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private final ImageView progressIcon;
        private final TextView progressTitle;
        private final CircularProgressIndicator progressBar;
        private final TextView progressValue;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            progressIcon = itemView.findViewById(R.id.progressIcon);
            progressTitle = itemView.findViewById(R.id.progressTitle);
            progressBar = itemView.findViewById(R.id.progressBar);
            progressValue = itemView.findViewById(R.id.progressValue);
        }

        public void bind(ProgressItem item) {
            progressIcon.setImageResource(item.getIconResId());
            progressTitle.setText(item.getTitle());
            progressBar.setProgress(item.getProgress());
            progressValue.setText(item.getProgressText());
        }
    }

    public static class ProgressItem {
        private final int iconResId;
        private final String title;
        private int progress;
        private final String progressText;

        public ProgressItem(int iconResId, String title, int progress, String progressText) {
            this.iconResId = iconResId;
            this.title = title;
            this.progress = progress;
            this.progressText = progressText;
        }

        public int getIconResId() { return iconResId; }
        public String getTitle() { return title; }
        public int getProgress() { return progress; }
        public String getProgressText() { return progressText; }
        public void setProgress(int progress) { this.progress = progress; }
    }
} 