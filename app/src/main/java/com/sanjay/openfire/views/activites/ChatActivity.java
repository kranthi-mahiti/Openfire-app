package com.sanjay.openfire.views.activites;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanjay.openfire.R;
import com.sanjay.openfire.views.adapters.ChatListAdapter;

public class ChatActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private ChatListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
    }

    public void initView() {
        recyclerView = findViewById(R.id.rvMessage);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);

        adapter = new ChatListAdapter();
        recyclerView.setAdapter(adapter);

    }
}
