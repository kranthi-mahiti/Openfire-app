package com.sanjay.openfire.views.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListView> {
    @NonNull
    @Override
    public ChatListAdapter.ChatListView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatListView holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ChatListView extends RecyclerView.ViewHolder {
        public ChatListView(@NonNull View itemView) {
            super(itemView);
        }
    }
}
