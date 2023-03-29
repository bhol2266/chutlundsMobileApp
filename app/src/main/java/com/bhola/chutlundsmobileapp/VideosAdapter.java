package com.bhola.chutlundsmobileapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.NativeAd;
import com.squareup.picasso.Picasso;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.viewholder> {

    Context context;
    List<VideoModel> videoData;
    AlertDialog dialog;


    public VideosAdapter(Context context, List<VideoModel> videoData) {
        this.context = context;
        this.videoData = videoData;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.video_thumnail_fullwidth, parent, false);
        return new viewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        VideoModel item = videoData.get(position);

        Picasso.get().load(item.getThumbnail()).into(holder.thumbnail);
        holder.title.setText(item.getTitle());
        holder.duration.setText(item.getDuration());
        holder.views.setText(item.getViews());
        holder.likes.setText(item.getLikedPercent());

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (exoplayer != null) {
//                    exoplayer.pause();
//                    holder.playerView.setVisibility(View.GONE);
//                    holder.thumbnail.setVisibility(View.VISIBLE);
//                    holder.previewBtn.setVisibility(View.VISIBLE);
//                }
                Intent intent = new Intent(v.getContext(), FullscreenVideoPLayer.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("href", item.getHref());
                intent.putExtra("thumbnail", item.getThumbnail());
                v.getContext().startActivity(intent);

            }
        });


        holder.previewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoDialog(item.getPreviewVideo(), item.getThumbnail());
            }
        });

        loadNativeAds(holder.native_Icon, holder.native_Button, holder.native_Title, holder.native_Description, holder.NativeAdCardview, holder.getAbsoluteAdapterPosition());

    }

    private void loadNativeAds(ImageView native_Icon, Button native_Button, TextView native_Title, TextView native_Description, CardView nativeAdCardview, int absoluteAdapterPosition) {

        if (absoluteAdapterPosition % SplashScreen.Native_Ad_Interval == 0) {
            final StartAppNativeAd nativeAd = new StartAppNativeAd(context);
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(new Runnable() {
                @Override
                public void run() {
                    nativeAd.loadAd(new NativeAdPreferences()
                            .setAdsNumber(1)
                            .setAutoBitmapDownload(true)
                            .setPrimaryImageSize(2), new AdEventListener() {
                        @Override
                        public void onReceiveAd(Ad ad) {

                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    native_Icon.setImageBitmap(nativeAd.getNativeAds().get(0).getImageBitmap());
                                    native_Title.setText(nativeAd.getNativeAds().get(0).getTitle());
                                    native_Description.setText(nativeAd.getNativeAds().get(0).getDescription());
                                    native_Button.setText(nativeAd.getNativeAds().get(0).isApp() ? "Install" : "Open");
                                    native_Button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            nativeAdCardview.performClick();
                                        }
                                    });
                                    nativeAd.getNativeAds().get(0).registerViewForInteraction(nativeAdCardview);

                                }
                            });

                        }

                        @Override
                        public void onFailedToReceiveAd(Ad ad) {
                            if (BuildConfig.DEBUG) {
                                Log.v(SplashScreen.TAG, "onFailedToReceiveAd: " + ad.getErrorMessage());
                            }
                        }
                    });
                }
            });

        } else {
            nativeAdCardview.setVisibility(View.GONE);
        }
    }


    private void videoDialog(String previewVideo, String thumbnail) {

        FrameLayout videoDialogLayout;
        ProgressBar progressBar2;
        ImageView thumnailImage;
        ExoPlayer exoplayer;
        final float[] videoAspectRatio = new float[1];
        StyledPlayerView playerView;
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.CustomDialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View promptView = inflater.inflate(R.layout.videodialog, null);
        builder.setView(promptView);
        builder.setCancelable(true);

        playerView = promptView.findViewById(R.id.exolayerView);
        videoDialogLayout = promptView.findViewById(R.id.videoDialogLayout);
        progressBar2 = promptView.findViewById(R.id.progressbar2);
//        thumnailImage = promptView.findViewById(R.id.thumbnailImage2);
//        Picasso.with(context).load(thumbnail).into(thumnailImage);


        exoplayer = new ExoPlayer.Builder(context).build();
        playerView.setShowPreviousButton(false);
        playerView.setShowNextButton(false);
        playerView.setShowShuffleButton(false);
        playerView.setPlayer(exoplayer);
        MediaItem mediaItem = MediaItem.fromUri(previewVideo);
        exoplayer.setMediaItem(mediaItem);

        exoplayer.prepare();
        exoplayer.play();

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
                    progressBar2.setVisibility(View.VISIBLE);

                } else if (playbackState == Player.STATE_READY) {
                    progressBar2.setVisibility(View.GONE);
                    playerView.setVisibility(View.VISIBLE);

                } else if (playbackState == ExoPlayer.STATE_ENDED) {
                    exoplayer.stop();
                    exoplayer.clearMediaItems();
                    dialog.dismiss();

                }
                Player.Listener.super.onPlaybackStateChanged(playbackState);
            }

            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                Player.Listener.super.onPlayWhenReadyChanged(playWhenReady, reason);

            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                if (isPlaying) {

                }

            }


            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);
                exoplayer.stop();
                exoplayer.clearMediaItems();
                dialog.dismiss();
                Toast.makeText(context, "Error Source", Toast.LENGTH_SHORT).show();
            }
        });

        playerView.setAspectRatioListener(new AspectRatioFrameLayout.AspectRatioListener() {
            @Override
            public void onAspectRatioUpdated(float targetAspectRatio, float naturalAspectRatio, boolean aspectRatioMismatch) {

                videoAspectRatio[0] = targetAspectRatio;
                if (targetAspectRatio < 1) {
                    ViewGroup.LayoutParams layoutParams = playerView.getLayoutParams();
                    layoutParams.width = 700;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    playerView.setLayoutParams(layoutParams);
                } else {
                    ViewGroup.LayoutParams layoutParams = playerView.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    playerView.setLayoutParams(layoutParams);
                }
            }
        });


        dialog = builder.create();
        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                exoplayer.stop();
                exoplayer.clearMediaItems();
                dialog.dismiss();
            }
        });

    }


    @Override
    public void onViewRecycled(@NonNull viewholder holder) {
        super.onViewRecycled(holder);

    }


    @Override
    public int getItemCount() {
        return videoData.size();
    }


    public class viewholder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title;
        TextView duration;
        TextView views;
        TextView likes;
        Button previewBtn;
        FrameLayout framelayout;
        ProgressBar progressbar;

        ImageView native_Icon;
        TextView native_Title;
        TextView native_Description;
        Button native_Button;
        CardView NativeAdCardview;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnailImage);
            title = itemView.findViewById(R.id.video_title);
            duration = itemView.findViewById(R.id.duration);
            views = itemView.findViewById(R.id.views);
            likes = itemView.findViewById(R.id.likes);
            previewBtn = itemView.findViewById(R.id.previewBtn);
            framelayout = itemView.findViewById(R.id.framelayout);
            progressbar = itemView.findViewById(R.id.progressbar);


            native_Icon = itemView.findViewById(R.id.icon);
            native_Title = itemView.findViewById(R.id.title);
            native_Description = itemView.findViewById(R.id.description);
            native_Button = itemView.findViewById(R.id.button);
            NativeAdCardview = itemView.findViewById(R.id.NativeAdCardview);

        }


    }
}

