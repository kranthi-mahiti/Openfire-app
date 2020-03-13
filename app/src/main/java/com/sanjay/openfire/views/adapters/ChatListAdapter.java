package com.sanjay.openfire.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanjay.openfire.R;
import com.sanjay.openfire.models.ChatListModel.ChatListModel;
import com.sanjay.openfire.models.MessageChatModel;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListView> {
    private List<ChatListModel> modelList;
    private Context context;

    public ChatListAdapter(List<ChatListModel> modelList, Context context) {
        this.modelList = modelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatListAdapter.ChatListView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chat_list, parent, false);
        return new ChatListView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatListView holder, int position) {

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ChatListView extends RecyclerView.ViewHolder {

        public ChatListView(@NonNull View itemView) {
            super(itemView);
        }
    }
}
