package com.example.vedioplaytest.VideoSetting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.vedioplaytest.MainActionBar;
import com.example.vedioplaytest.MainActivity;
import com.example.vedioplaytest.R;

public class Select_InternetView extends AppCompatActivity {

    protected static Select_InternetView activity = null;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_internetview);
        activity = this;

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        setActionBar();
    }

    private void setActionBar() {
        MainActionBar mActionBar = new MainActionBar(this, getSupportActionBar());
        mActionBar.setActionBar(R.layout.internet_action_bar);
    }
}