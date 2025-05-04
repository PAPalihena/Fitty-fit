package com.example.fittyfit.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fittyfit.R;
import com.example.fittyfit.models.User;
import java.util.ArrayList;
import java.util.List;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.UserViewHolder> {
    private List<User> users;
    private List<User> selectedUsers;
    private OnUserSelectedListener listener;

    public interface OnUserSelectedListener {
        void onUserSelected(User user, boolean isSelected);
    }

    public SearchUserAdapter(OnUserSelectedListener listener) {
        this.users = new ArrayList<>();
        this.selectedUsers = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_search_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        String fullName = user.getFirstName() + " " + user.getLastName();
        holder.userName.setText(fullName);
        holder.itemView.setOnClickListener(v -> {
            boolean isSelected = !selectedUsers.contains(user);
            if (isSelected) {
                selectedUsers.add(user);
            } else {
                selectedUsers.remove(user);
            }
            listener.onUserSelected(user, isSelected);
            notifyItemChanged(position);
        });
        holder.itemView.setSelected(selectedUsers.contains(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateUsers(List<User> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName;

        UserViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
        }
    }
} 