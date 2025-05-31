package com.example.fittyfit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fittyfit.R;
import com.example.fittyfit.model.CommunityPost;

import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.PostViewHolder> {
    private Context context;
    private List<CommunityPost> postList;

    public CommunityAdapter(Context context, List<CommunityPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_community_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        CommunityPost post = postList.get(position);
        holder.userName.setText(post.getUserName());
        holder.postContent.setText(post.getContent());
        holder.postTime.setText(post.getTimeAgo());
        holder.likeCount.setText(String.valueOf(post.getLikes()));
        holder.commentCount.setText(String.valueOf(post.getComments()));
        holder.shareCount.setText(String.valueOf(post.getShares()));

        // Handle post image
        if (post.getImageResId() != 0) {
            holder.postImage.setImageResource(post.getImageResId());
            holder.postImage.setVisibility(View.VISIBLE);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        // Set click listeners
        holder.likeButton.setOnClickListener(v -> {
            // TODO: Implement like functionality
        });

        holder.commentButton.setOnClickListener(v -> {
            // TODO: Implement comment functionality
        });

        holder.shareButton.setOnClickListener(v -> {
            // TODO: Implement share functionality
        });

        holder.menuButton.setOnClickListener(v -> {
            // TODO: Implement menu functionality
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userName, postContent, postTime, likeCount, commentCount, shareCount;
        View likeButton, commentButton, shareButton;
        ImageView userProfileImage, postImage;
        ImageView menuButton;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            postContent = itemView.findViewById(R.id.postContent);
            postTime = itemView.findViewById(R.id.postTime);
            likeCount = itemView.findViewById(R.id.likeCount);
            commentCount = itemView.findViewById(R.id.commentCount);
            shareCount = itemView.findViewById(R.id.shareCount);
            likeButton = itemView.findViewById(R.id.likeButton);
            commentButton = itemView.findViewById(R.id.commentButton);
            shareButton = itemView.findViewById(R.id.shareButton);
            menuButton = itemView.findViewById(R.id.menuButton);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            postImage = itemView.findViewById(R.id.postImage);
        }
    }
} 