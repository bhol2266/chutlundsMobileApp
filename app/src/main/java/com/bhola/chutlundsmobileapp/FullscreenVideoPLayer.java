package com.bhola.chutlundsmobileapp;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.VideoListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import soup.neumorphism.NeumorphButton;

public class FullscreenVideoPLayer extends AppCompatActivity {
    
    TextView videoTitle;
    LinearLayout linearLayoutStatusbar;
    FrameLayout exoplayerFrameLayout;
    ImageView thumbnailImageView;
    ProgressBar videoProgressBar;
    LinearLayout recyclerViewLinearLayout_relatedVideos;
    ProgressBar activityProgressBar;
    private String TAG = "TAGA";
    ExoPlayer exoplayer;
    StyledPlayerView playerView;
    TextView videoQualityTextview;
    String VideoSrc, vidoetitle, href;
    public static String Title, duration, likedPercent, thumbnail, views, preloaded_video_quality;
    public static List<String> video_qualities_available;
    public static List<String> video_qualities_available_withURL;
    public static List<ScreenShotModel> screenshotsMap;
    public static List<VideoModel> relatedVideos;
    public static List<String> tagsArray;
    boolean fullscreenActive = false;
    boolean DATA_FETCHED = false;
    float videoAspectRatio;
    AlertDialog dialog;
    Spinner spinner;
    boolean backPressed = false;
    final Handler[] handler = new Handler[1];//Ads


    //Download Manager
    public final static String COLUMN_TOTAL_SIZE_BYTES = "total_size";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_video_player);

        vidoetitle = getIntent().getStringExtra("title");
        href = getIntent().getStringExtra("href");
//        href = "https://spankbang.com/74jbj/video/utro+safado+de+campinas+que+veio+trair+sua+mulher+comigo";  removedVideo sample
        thumbnail = getIntent().getStringExtra("thumbnail");
        thumbnailImageView = findViewById(R.id.thumbnailImageView);
        Picasso.get().load(thumbnail).into(thumbnailImageView);


        videoProgressBar = findViewById(R.id.videoProgressBar);
        linearLayoutStatusbar = findViewById(R.id.linearLayoutStatusbar);
        videoTitle = findViewById(R.id.title);
        exoplayerFrameLayout = findViewById(R.id.exoplayerFrameLayout);
        recyclerViewLinearLayout_relatedVideos = findViewById(R.id.recyclerViewLinearLayout_relatedVideos);
        VideoSrc = getIntent().getStringExtra("videoSrc");
        videoTitle.setText(getIntent().getStringExtra("title"));

        getVideoData_API();
        setVideoPlayerHeight_PORTRAIT("portrait");
        actionBar();
        showAds();
    }


    private void prepare_videoPlayer() {

        //This is because the videoDetailBar and relativelayout were not hiding inside recyclerViewLinearLayout_relatedVideos, so hiding them indivisually
        LinearLayout videoDetailBar = findViewById(R.id.videoDetailBar);
        videoDetailBar.setVisibility(View.VISIBLE);
        Button downloadBtn = findViewById(R.id.downloadBtn);
        downloadBtn.setVisibility(View.VISIBLE);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!MainActivity.userLoggedIn) {
//                    final Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);
//                    View customSnackView = getLayoutInflater().inflate(R.layout.custom_snackbar_view, null);
//                    // now change the layout of the snackbar
//                    Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
//
//                    TextView gotologins = customSnackView.findViewById(R.id.gotologins);
//                    gotologins.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            Intent i = new Intent(FullscreenVideoPLayer.this, login.class);
//                            i.putExtra("commingFrom", "videoplayerActivity");
//                            i.putExtra("title", vidoetitle);
//                            i.putExtra("href", href);
//                            i.putExtra("thumbnail", thumbnail);
//                            startActivity(i);
//                        }
//                    });
//
//                    // add the custom snack bar layout to snackbar layout
//                    snackbarLayout.addView(customSnackView, 0);
//                    snackbar.show();
//                    return;
//                }
                Toast.makeText(FullscreenVideoPLayer.this, "Download will start after Ad ", Toast.LENGTH_LONG).show();
                    showRewardedVideo();
            }

            private void downloadVideoAfterWatchingAd() {
                try {
                    String downloadURL = video_qualities_available_withURL.get(spinner.getSelectedItemPosition());
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURL));
                    String title = URLUtil.guessFileName(downloadURL, null, null);
                    request.setTitle((CharSequence) title);
//                request.setDescription("Downloading video please wait...");
                    request.setAllowedOverMetered(true);
                    request.setAllowedOverRoaming(true);
                    request.setVisibleInDownloadsUi(true);
                    String cookie = CookieManager.getInstance().getCookie(downloadURL);
                    request.addRequestHeader("cookie", cookie);
                    request.allowScanningByMediaScanner();

                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);

                    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    long downloadId = downloadManager.enqueue(request);

                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadId);
                    Cursor cursor = downloadManager.query(q);
                    cursor.moveToFirst();
//                int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
//                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
//                final long dl_progress = (bytes_downloaded*100L)/bytes_total;
                    Toast.makeText(FullscreenVideoPLayer.this, "Downloading started...", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(FullscreenVideoPLayer.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            private void showRewardedVideo() {
                final StartAppAd rewardedVideo = new StartAppAd(FullscreenVideoPLayer.this);

                rewardedVideo.setVideoListener(new VideoListener() {
                    @Override
                    public void onVideoCompleted() {
                        downloadVideoAfterWatchingAd();
                    }
                });

                rewardedVideo.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                    @Override
                    public void onReceiveAd(Ad ad) {
                        rewardedVideo.showAd();
                    }

                    @Override
                    public void onFailedToReceiveAd(Ad ad) {
                        Toast.makeText(getApplicationContext(), "Can't show rewarded video", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        relatedVideosRecyclerView();
        TextView durationText = findViewById(R.id.duration);
        durationText.setText(duration);
        TextView viewsText = findViewById(R.id.views);
        viewsText.setText(views);
        TextView likesText = findViewById(R.id.likes);
        likesText.setText(likedPercent);

        exoplayer = new ExoPlayer.Builder(FullscreenVideoPLayer.this).setSeekBackIncrementMs(10000)
                .setSeekForwardIncrementMs(10000).build();
        playerView = findViewById(R.id.exolayerView);
        playerView.setShowPreviousButton(false);
        playerView.setShowNextButton(false);
        playerView.setShowShuffleButton(false);
        playerView.getControllerHideOnTouch();
        playerView.setControllerShowTimeoutMs(3000);
        playerView.setControllerAutoShow(true);
        playerView.setShowVrButton(true);

        playerView.setKeepScreenOn(true);

        spinner = findViewById(R.id.spinner1);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_videoplayer, video_qualities_available);
        adapter.setDropDownViewResource(R.layout.spinneritem);
        spinner.setAdapter(adapter);
        int spinnerPosition = video_qualities_available.indexOf(preloaded_video_quality);
        spinner.setSelection(spinnerPosition);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int current_position = (int) exoplayer.getCurrentPosition();
                if (current_position != 0 || video_qualities_available.indexOf(preloaded_video_quality) != position) {
                    exoplayer.stop();
                    playerView.hideController();
                    MediaItem mediaItem = MediaItem.fromUri(video_qualities_available_withURL.get(position));
                    exoplayer.setMediaItem(mediaItem);
                    exoplayer.prepare();
                    exoplayer.play();
                    exoplayer.seekTo(current_position);
                    playerView.setVisibility(View.GONE);
                    videoProgressBar.setVisibility(View.VISIBLE);
                    thumbnailImageView.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        playerView.setFullscreenButtonClickListener(new StyledPlayerView.FullscreenButtonClickListener() {
            @Override
            public void onFullscreenButtonClick(boolean isFullScreen) {
                Log.d(TAG, "isFullScreen: " + isFullScreen);
                fullscreenActive = !fullscreenActive;
                if (!fullscreenActive) {
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

                videoAspectRatio = targetAspectRatio;
            }
        });

        playerView.setPlayer(exoplayer);
//        int videoQualityForPlayingIndex = getDeviceScreenResolution();
        MediaItem mediaItem = MediaItem.fromUri(VideoSrc);
        exoplayer.setMediaItem(mediaItem);
        exoplayer.prepare();
        exoplayer.play();
        exoplayerListeners();

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
                    if (backPressed) {
                        exoplayer.stop();
                        exoplayer.release();
                    }
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


    private void getVideoData_API() {
        RequestQueue requestQueue = Volley.newRequestQueue(FullscreenVideoPLayer.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.chutlunds.com/api/spangbang/videoPlayer", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //let's parse json data
                relatedVideos = new ArrayList<>();
                video_qualities_available = new ArrayList<>();
                video_qualities_available_withURL = new ArrayList<>();
                screenshotsMap = new ArrayList<>();
                tagsArray = new ArrayList<>();
                DATA_FETCHED = true;

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean noVideos = Boolean.parseBoolean(jsonObject.getString("noVideos"));
                    if (noVideos) {
                        JSONArray jsonArray = jsonObject.getJSONArray("relatedVideos");
                        JSONArray jsonArray2 = jsonObject.getJSONArray("finalDataArray2");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            VideoModel videoModel = new VideoModel(obj.getString("thumbnailArray"), obj.getString("TitleArray"), obj.getString("durationArray"), obj.getString("likedPercentArray"), obj.getString("viewsArray"), obj.getString("previewVideoArray"), obj.getString("hrefArray"));
                            relatedVideos.add(videoModel);
                        }
                        relatedVideosRecyclerView();
                        exoplayerFrameLayout.setVisibility(View.GONE);
                        Toast.makeText(FullscreenVideoPLayer.this, "This video has been removed", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject videolink_qualities_screenshotsOBJECT = jsonObject.getJSONObject("videolink_qualities_screenshots");
                    VideoSrc = videolink_qualities_screenshotsOBJECT.getString("default_video_src");
                    JSONArray video_qualities_Array_JSON = videolink_qualities_screenshotsOBJECT.getJSONArray("video_qualities_available");
                    JSONArray video_qualitiesURL_Array_JSON = videolink_qualities_screenshotsOBJECT.getJSONArray("video_qualities_available_withURL");
                    JSONArray screenshotsArray_JSON = videolink_qualities_screenshotsOBJECT.getJSONArray("screenshotsArray");
                    JSONArray tagsArray_JSON = videolink_qualities_screenshotsOBJECT.getJSONArray("tagsArray");


                    for (int i = 0; i < video_qualities_Array_JSON.length(); i++) {
                        video_qualities_available.add(video_qualities_Array_JSON.getString(i));
                    }
                    for (int i = 0; i < video_qualitiesURL_Array_JSON.length(); i++) {
                        video_qualities_available_withURL.add(video_qualitiesURL_Array_JSON.getString(i));
                    }
                    for (int i = 0; i < tagsArray_JSON.length(); i++) {
                        tagsArray.add(tagsArray_JSON.getString(i));
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
                    setTagsInLayout();
                    setScreenShotsInLayout();
                    setVideoQualtiyInLayout();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Exception: " + e.getMessage());

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DATA_FETCHED = true;
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

    private void setTagsInLayout() {

        LinearLayout tagsLayout = findViewById(R.id.tagsLayout);
        for (int i = 0; i < tagsArray.size(); i++) {

            String tagKey = tagsArray.get(i).trim();
            View view = getLayoutInflater().inflate(R.layout.tag, null);
            TextView tag = view.findViewById(R.id.tag);
            tag.setText(tagKey);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 5, 15, 5);
            tag.setLayoutParams(params);

            tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), VideosList.class);
                    intent.putExtra("Title", tagKey);
                    intent.putExtra("url", "https://spankbang.com/s/" + tagKey + "/");
                    startActivity(intent);
                }
            });

            tagsLayout.addView(view);
        }
    }

    private void setVideoQualtiyInLayout() {

        LinearLayout videoQualityLayout = findViewById(R.id.videoQualtiyLayout);
        for (int i = 0; i < video_qualities_available.size(); i++) {
            int positionn = i;
            View view = getLayoutInflater().inflate(R.layout.videoquality, null);
            TextView quality = view.findViewById(R.id.videoQualtyTextview);
            quality.setText(video_qualities_available.get(i));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 10, 0);
            quality.setLayoutParams(params);


            quality.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int current_position = (int) exoplayer.getCurrentPosition();
                    spinner.setSelection(positionn);
                    playerView.performClick();
                }
            });
            videoQualityLayout.addView(view);
        }
    }

    private void setScreenShotsInLayout() {

        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        RecyclerView recyclerViewScreenshot = findViewById(R.id.recyclerViewScreenshot);

        ScreenShotAdapter screenShotAdapter = new ScreenShotAdapter((Context) FullscreenVideoPLayer.this, screenshotsMap, exoplayer);
        recyclerViewScreenshot.setLayoutManager(layoutManager);
        recyclerViewScreenshot.setAdapter(screenShotAdapter);

        TextView openScreenshotALayout = findViewById(R.id.openScreenshotALayout);
        openScreenshotALayout.setVisibility(View.VISIBLE);
        openScreenshotALayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewScreenshot.getVisibility() == View.GONE) {
                    recyclerViewScreenshot.setVisibility(View.VISIBLE);
                } else {
                    recyclerViewScreenshot.setVisibility(View.GONE);

                }
            }
        });

    }


    private void setVideoPlayerHeight_PORTRAIT(String orientation) {
        linearLayoutStatusbar.setVisibility(View.VISIBLE);
        recyclerViewLinearLayout_relatedVideos.setVisibility(View.VISIBLE);


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
        recyclerViewLinearLayout_relatedVideos.setVisibility(View.GONE);
        ViewGroup.LayoutParams params = exoplayerFrameLayout.getLayoutParams();
        params.width = MATCH_PARENT;
        params.height = MATCH_PARENT;
        exoplayerFrameLayout.setLayoutParams(params);
    }


    private void videoQuality_dialog() {


        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(FullscreenVideoPLayer.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View promptView = inflater.inflate(R.layout.video_qualityselector_dialog, null);
        builder.setView(promptView);
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();

    }

    private int getDeviceScreenResolution() {
        int videoQualityForPlayingIndex = 0;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        for (int i = 0; i < video_qualities_available.size(); i++) {
            if (video_qualities_available.get(i).equals(String.valueOf(width) + "p")) {
                videoQualityForPlayingIndex = i;
            }
        }
        if (videoQualityForPlayingIndex == 0) {
            return video_qualities_available_withURL.size() - 1;
        } else {
            return videoQualityForPlayingIndex;

        }
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
        TextView relativelayoutTextview = findViewById(R.id.relatedVideos);
        relativelayoutTextview.setVisibility(View.VISIBLE);
        recyclerViewLinearLayout_relatedVideos.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 1);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        VideosAdapter adapter = new VideosAdapter(FullscreenVideoPLayer.this, relatedVideos);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (exoplayer != null) {
            exoplayer.pause();
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (handler[0] != null) handler[0].removeCallbacksAndMessages(null);
        super.onWindowFocusChanged(hasFocus);
    }

    private void showAds() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                StartAppAd.showAd(FullscreenVideoPLayer.this);
            }
        },50000);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            MainActivity.authProviderName = user.getProviderData().get(user.getProviderData().size() - 1).getProviderId();
            Log.d(TAG, "AuthProvider: " + MainActivity.authProviderName);
            MainActivity.userLoggedIn = true;
            MainActivity.menu_login.setTitle("Log Out");
            MainActivity.loggedInLayout.setVisibility(View.VISIBLE);
            String personEmail = user.getEmail();
            MainActivity.email.setText(personEmail);
        }

    }


    @Override
    public void onBackPressed() {

        if (fullscreenActive && DATA_FETCHED) {
            fullscreenActive = false;
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
            windowInsetsCompat.show(WindowInsetsCompat.Type.statusBars());
            setVideoPlayerHeight_PORTRAIT("landscape");
        } else {
            if (handler[0] != null) handler[0].removeCallbacksAndMessages(null);
            if (exoplayer != null) {
                exoplayer.pause();
                exoplayer.stop();
                exoplayer.clearMediaItems();
            }
            backPressed=true;
            StartAppAd.showAd(FullscreenVideoPLayer.this);
            super.onBackPressed();
            this.finish();
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
