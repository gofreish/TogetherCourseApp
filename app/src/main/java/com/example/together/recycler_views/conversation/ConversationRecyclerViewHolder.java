package com.example.together.recycler_views.conversation;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.together.R;

public class ConversationRecyclerViewHolder extends RecyclerView.ViewHolder {

    ImageView userImageView;
    TextView usernameTextView;
    TextView emailTextView;
    public ConversationRecyclerViewHolder(@NonNull View viewItem){
        super(viewItem);
        userImageView = viewItem.findViewById(R.id.userImageView);
        usernameTextView = viewItem.findViewById(R.id.usernameTextView);
        emailTextView = viewItem.findViewById(R.id.emailTextView);
    }
}
