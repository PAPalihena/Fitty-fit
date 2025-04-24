package com.example.fittyfit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fittyfit.R;
import com.example.fittyfit.model.CommunityPost;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.PostViewHolder> {

    private List<CommunityPost> postList;
    private Context context;

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

        // Load profile image (placeholder for now)
        // TODO: Use a library like Glide or Picasso to load actual user images
        holder.userProfileImage.setImageResource(R.drawable.ic_profile); 

        // Load post image if available
        if (post.getImageResId() != 0) {
            holder.postImage.setImageResource(post.getImageResId());
            holder.postImage.setVisibility(View.VISIBLE);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        holder.btnReact.setOnClickListener(v -> showReactOptions(v, holder.btnReact));
        holder.btnComment.setOnClickListener(v -> {
            // TODO: Implement comment functionality
            Toast.makeText(context, "Comment clicked for post by " + post.getUserName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    private void showReactOptions(View anchor, Button reactButtonView) {
        // Cast to MaterialButton
        MaterialButton reactButton = (MaterialButton) reactButtonView;

        PopupMenu popup = new PopupMenu(context, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_react_options, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.react_like) {
                reactButton.setText("Like");
                reactButton.setIconResource(R.drawable.ic_like);
                // TODO: Update reaction state
            } else if (itemId == R.id.react_love) {
                reactButton.setText("Love");
                reactButton.setIconResource(R.drawable.ic_love);
                // TODO: Update reaction state
            } else if (itemId == R.id.react_smile) {
                reactButton.setText("Smile");
                reactButton.setIconResource(R.drawable.ic_smile);
                // TODO: Update reaction state
            } else if (itemId == R.id.react_amazed) {
                reactButton.setText("Amazed");
                reactButton.setIconResource(R.drawable.ic_amazed);
                // TODO: Update reaction state
            } else {
                return false;
            }
            Toast.makeText(context, "Reacted: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        });
        popup.show();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage, postImage;
        TextView userName, postContent;
        MaterialButton btnReact;
        Button btnComment;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            userName = itemView.findViewById(R.id.userName);
            postContent = itemView.findViewById(R.id.postContent);
            postImage = itemView.findViewById(R.id.postImage);
            btnReact = itemView.findViewById(R.id.btnReact);
            btnComment = itemView.findViewById(R.id.btnComment);
        }
    }
} 