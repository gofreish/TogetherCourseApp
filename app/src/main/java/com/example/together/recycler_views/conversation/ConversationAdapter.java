package com.example.together.recycler_views.conversation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.together.R;
import com.example.together.model.User;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationRecyclerViewHolder> {

    Context context;
    List<User> users;

    public ConversationAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ConversationRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationRecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.conversation_item_view, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationRecyclerViewHolder holder, int position) {
        User user = users.get(position);
        holder.emailTextView.setText(user.getEmail());
        holder.usernameTextView.setText(user.getUsername());
        Glide.with(context).load(user.getProfile_img_url()).into(holder.userImageView);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
