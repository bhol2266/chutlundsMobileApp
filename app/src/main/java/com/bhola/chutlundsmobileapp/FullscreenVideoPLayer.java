package com.bhola.chutlundsmobileapp;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullscreenVideoPLayer extends AppCompatActivity {
    TextView videoTitle;
    LinearLayout linearLayoutStatusbar;
    FrameLayout exoplayerFrameLayout;
    ImageView thumbnailImageView;
    ProgressBar videoProgressBar;
    LinearLayout recyclerViewLinearLayout;
    ProgressBar activityProgressBar;
    private String TAG = "jsonObject";
    ExoPlayer exoplayer;
    StyledPlayerView playerView;
    String VideoSrc, vidoetitle, href;
    public static String Title, duration, likedPercent, thumbnail, views, preloaded_video_quality;
    public static List<String> video_qualities_available;
    public static List<String> video_qualities_available_withURL;
    public static List<ScreenShotModel> screenshotsMap;
    public static List<VideoModel> relatedVideos;
    boolean fullscreenActive;
    float videoAspectRatio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_video_player);

        vidoetitle = getIntent().getStringExtra("title");
        href = getIntent().getStringExtra("href");
        thumbnail = getIntent().getStringExtra("thumbnail");
        thumbnailImageView = findViewById(R.id.thumbnailImageView);
        Picasso.with(FullscreenVideoPLayer.this).load(thumbnail).into(thumbnailImageView);


        videoProgressBar = findViewById(R.id.videoProgressBar);
        linearLayoutStatusbar = findViewById(R.id.linearLayoutStatusbar);
        videoTitle = findViewById(R.id.title);
        exoplayerFrameLayout = findViewById(R.id.exoplayerFrameLayout);
        recyclerViewLinearLayout = findViewById(R.id.recyclerViewLinearLayout);
        VideoSrc = getIntent().getStringExtra("videoSrc");
        videoTitle.setText(getIntent().getStringExtra("title"));


        getVideoData_API();
        setVideoPlayerHeight_PORTRAIT("portrait");
        actionBar();
    }


    private void prepare_videoPlayer() {

        //This is because the videoDetailBar and relativelayout were not hiding inside recyclerViewLinearLayout, so hiding them indivisually
        LinearLayout videoDetailBar = findViewById(R.id.videoDetailBar);
        videoDetailBar.setVisibility(View.VISIBLE);
        TextView relativelayout = findViewById(R.id.relatedVideos);
        relativelayout.setVisibility(View.VISIBLE);

        recyclerViewLinearLayout.setVisibility(View.VISIBLE);
        relatedVideosRecyclerView();
        TextView durationText = findViewById(R.id.duration);
        durationText.setText(duration);
        TextView viewsText = findViewById(R.id.views);
        viewsText.setText(views);
        TextView likesText = findViewById(R.id.likes);
        likesText.setText(likedPercent);


        exoplayer = new ExoPlayer.Builder(FullscreenVideoPLayer.this).build();
        playerView = findViewById(R.id.exolayerView);
        playerView.setShowPreviousButton(false);
        playerView.setShowNextButton(false);
        playerView.setShowShuffleButton(false);
        playerView.getControllerHideOnTouch();
        playerView.setControllerAutoShow(true);
        playerView.setKeepScreenOn(true);


        playerView.setFullscreenButtonClickListener(new StyledPlayerView.FullscreenButtonClickListener() {
            @Override
            public void onFullscreenButtonClick(boolean isFullScreen) {
                Log.d(TAG, "isFullScreen: " + isFullScreen);
                if (!isFullScreen) {
                    fullscreenActive = false;
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
                    windowInsetsCompat.show(WindowInsetsCompat.Type.statusBars());
                    setVideoPlayerHeight_PORTRAIT("landscape");
                } else {

                    if (videoAspectRatio < 1) {

                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                    fullscreenActive = true;
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
                    windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
                    setVideoPlayerHeight_LANDSCAPE();

                }


            }
        });
        playerView.setAspectRatioListener(new AspectRatioFrameLayout.AspectRatioListener() {
            @Override
            public void onAspectRatioUpdated(float targetAspectRatio, float naturalAspectRatio, boolean aspectRatioMismatch) {
//                Log.d(TAG, "targetAspectRatio: " + targetAspectRatio);
//                Log.d(TAG, "naturalAspectRatio: " + naturalAspectRatio);
//                Log.d(TAG, "aspectRatioMismatch: " + aspectRatioMismatch);
                videoAspectRatio = targetAspectRatio;
            }
        });

        playerView.setPlayer(exoplayer);


        MediaItem mediaItem = MediaItem.fromUri(VideoSrc);
        exoplayer.setMediaItem(mediaItem);
        exoplayer.prepare();
        exoplayer.play();
        exoplayerListeners();

    }


    private void getVideoData_API() {
        RequestQueue requestQueue = Volley.newRequestQueue(FullscreenVideoPLayer.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.chutlunds.live/api/spangbang/videoPlayer", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //let's parse json data
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject videolink_qualities_screenshotsOBJECT = jsonObject.getJSONObject("videolink_qualities_screenshots");
                    VideoSrc = videolink_qualities_screenshotsOBJECT.getString("default_video_src");
                    JSONArray video_qualities_Array_JSON = videolink_qualities_screenshotsOBJECT.getJSONArray("video_qualities_available");
                    JSONArray video_qualitiesURL_Array_JSON = videolink_qualities_screenshotsOBJECT.getJSONArray("video_qualities_available_withURL");
                    JSONArray screenshotsArray_JSON = videolink_qualities_screenshotsOBJECT.getJSONArray("screenshotsArray");
                    video_qualities_available = new ArrayList<>();
                    video_qualities_available_withURL = new ArrayList<>();
                    screenshotsMap = new ArrayList<>();
                    relatedVideos = new ArrayList<>();

                    for (int i = 0; i < video_qualities_Array_JSON.length(); i++) {
                        video_qualities_available.add(video_qualities_Array_JSON.getString(i));
                    }
                    for (int i = 0; i < video_qualitiesURL_Array_JSON.length(); i++) {
                        video_qualities_available_withURL.add(video_qualitiesURL_Array_JSON.getString(i));
                    }

                    for (int i = 0; i < screenshotsArray_JSON.length(); i++) {
                        JSONObject jsonObject1 = screenshotsArray_JSON.getJSONObject(i);
                        String url = jsonObject1.getString("url");
                        String seekTime = jsonObject1.getString("seekTime");
                        ScreenShotModel screenShotModel = new ScreenShotModel(url, seekTime);
                        screenshotsMap.add(screenShotModel);
                    }

                    //video_details
                    preloaded_video_quality = jsonObject.getString("preloaded_video_quality");
                    JSONObject video_details_json = jsonObject.getJSONObject("video_details");

                    Title = video_details_json.getString("Title");
                    duration = video_details_json.getString("duration");
                    likedPercent = video_details_json.getString("likedPercent");
                    thumbnail = video_details_json.getString("thumbnail");
                    views = video_details_json.getString("views");

                    JSONArray relatedVideos_JSON = jsonObject.getJSONArray("relatedVideos");

                    for (int i = 0; i < relatedVideos_JSON.length(); i++) {
                        JSONObject obj = relatedVideos_JSON.getJSONObject(i);
                        VideoModel videoModel = new VideoModel(obj.getString("thumbnailArray"), obj.getString("TitleArray"), obj.getString("durationArray"), obj.getString("likedPercentArray"), obj.getString("viewsArray"), obj.getString("previewVideoArray"), obj.getString("hrefArray"));
                        relatedVideos.add(videoModel);
                    }


                    prepare_videoPlayer();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Exception: " + e.getMessage());

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("href", href);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


    private void setVideoPlayerHeight_PORTRAIT(String orientation) {
        linearLayoutStatusbar.setVisibility(View.VISIBLE);
        recyclerViewLinearLayout.setVisibility(View.VISIBLE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        ViewGroup.LayoutParams params = exoplayerFrameLayout.getLayoutParams();
        if (orientation == "landscape" && videoAspectRatio > 1) {
            params.width = MATCH_PARENT;
            params.height = (displayHeight / 16) * 9;

        } else {
            params.width = MATCH_PARENT;
            params.height = (displayWidth / 16) * 9;

        }
        exoplayerFrameLayout.setLayoutParams(params);
    }

    private void setVideoPlayerHeight_LANDSCAPE() {
        linearLayoutStatusbar.setVisibility(View.GONE);
        recyclerViewLinearLayout.setVisibility(View.GONE);
        ViewGroup.LayoutParams params = exoplayerFrameLayout.getLayoutParams();
        params.width = MATCH_PARENT;
        params.height = MATCH_PARENT;
        exoplayerFrameLayout.setLayoutParams(params);
    }

    private void exoplayerListeners() {
        exoplayer.addListener(new Player.Listener() {
            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {
                Player.Listener.super.onTimelineChanged(timeline, reason);
            }

            @Override
            public void onTracksChanged(Tracks tracks) {
                Player.Listener.super.onTracksChanged(tracks);
            }

            @Override
            public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
                Player.Listener.super.onMediaMetadataChanged(mediaMetadata);
                Log.d(TAG, "onMediaMetadataChanged: " + mediaMetadata);
            }

            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                Player.Listener.super.onIsLoadingChanged(isLoading);

            }

            @Override
            public void onTrackSelectionParametersChanged(TrackSelectionParameters parameters) {
                Player.Listener.super.onTrackSelectionParametersChanged(parameters);
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
//                    Log.d(TAG, "STATE_BUFFERING");
                } else if (playbackState == Player.STATE_READY) {
//                    Log.d(TAG, "STATE_READY");

                }
                Player.Listener.super.onPlaybackStateChanged(playbackState);
//                Log.d(TAG, "playbackState: " + playbackState);
            }

            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                Player.Listener.super.onPlayWhenReadyChanged(playWhenReady, reason);

            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                if (isPlaying && playerView.getVisibility() == View.GONE) {
                    playerView.setVisibility(View.VISIBLE);
                    videoProgressBar.setVisibility(View.GONE);
                    thumbnailImageView.setVisibility(View.GONE);
                }
            }


            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);
            }
        });

    }


    private void fullscreenMode() {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }

    private void actionBar() {
        TextView title_collection = findViewById(R.id.title_collection);
//        title_collection.setText(getIntent().getStringExtra("Title"));
//        title_collection.setText(vidoetitle);
        ImageView back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void relatedVideosRecyclerView() {
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 1);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        VideosAdapter adapter = new VideosAdapter(FullscreenVideoPLayer.this, relatedVideos);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (exoplayer != null && !hasFocus) {
            exoplayer.pause();
        } else if (exoplayer != null && hasFocus) {
            exoplayer.play();
        }
    }

    @Override
    public void onBackPressed() {
        if (fullscreenActive) {
            fullscreenActive = false;
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
            windowInsetsCompat.show(WindowInsetsCompat.Type.statusBars());
            setVideoPlayerHeight_PORTRAIT("landscape");
        } else {
            super.onBackPressed();

        }
    }
}

class ScreenShotModel {

    String url, seekTime;

    public ScreenShotModel() {
    }

    public ScreenShotModel(String url, String seekTime) {
        this.url = url;
        this.seekTime = seekTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSeekTime() {
        return seekTime;
    }

    public void setSeekTime(String seekTime) {
        this.seekTime = seekTime;
    }
}
