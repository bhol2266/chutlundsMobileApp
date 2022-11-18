package com.bhola.chutlundsmobileapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.squareup.picasso.Picasso;

import java.util.List;

class ScreenShotAdapter extends RecyclerView.Adapter<ScreenShotAdapter.viewholder> {

    Context context;
    List<ScreenShotModel> screenShotCollection;
    ExoPlayer exoPlayer;


    public ScreenShotAdapter(Context context, List<ScreenShotModel> screenShotCollection, ExoPlayer exoplayer) {
        this.context = context;
        this.screenShotCollection = screenShotCollection;
        this.exoPlayer = exoplayer;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.screenshot_layout, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onViewRecycled(viewholder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        ScreenShotModel screenShotModel = screenShotCollection.get(position);

        Picasso.get().load(screenShotModel.getUrl()).into(holder.screenShotthumbnail);
        holder.seekTime.setText(screenShotModel.getSeekTime());


        holder.ssFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = screenShotModel.getSeekTime();
                int extractMinute = Integer.parseInt(time.substring(0, time.indexOf(":")));
                int extractSeconds = Integer.parseInt(time.substring(time.indexOf(":") + 1, time.length()));

                Log.d("TAGA", "onClick: " + extractMinute + " " + extractSeconds);

                exoPlayer.seekTo(((extractMinute * 60) + extractSeconds) * 1000);
                if (!exoPlayer.isPlaying()) {
                    exoPlayer.play();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return screenShotCollection.size();
    }


    public class viewholder extends RecyclerView.ViewHolder {
        FrameLayout ssFrameLayout;
        ImageView screenShotthumbnail;
        TextView seekTime;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            screenShotthumbnail = itemView.findViewById(R.id.thumnailScreenshot);
            ssFrameLayout = itemView.findViewById(R.id.ssFrameLayout);
            seekTime = itemView.findViewById(R.id.seekTime);


        }


    }
}
