package com.bhola.chutlundsmobileapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VideosList extends AppCompatActivity {

    VideosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videolist);
        actionBar();
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 1);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        Log.d("onCreate", "onCreate: " + getIntent().getStringExtra("Title"));

        if (getIntent().getStringExtra("Title").equals("Trending Videos")) {
            adapter = new VideosAdapter(VideosList.this, SplashScreen.Trending_collectonData);
        }
            if (getIntent().getStringExtra("Title").equals("Upcoming Videos")) {
            adapter = new VideosAdapter(VideosList.this, SplashScreen.Upcoming_collectonData);
        }
            if (getIntent().getStringExtra("Title").equals("Popular Videos")) {
            adapter = new VideosAdapter(VideosList.this, SplashScreen.Popular_collectonData);
        }
            if (getIntent().getStringExtra("Title").equals("New Videos")) {
            adapter = new VideosAdapter(VideosList.this, SplashScreen.New_collectonData);
        }
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    private void actionBar() {
        TextView title_collection = findViewById(R.id.title_collection);
        title_collection.setText(getIntent().getStringExtra("Title"));
        ImageView back_arrow=findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


}
