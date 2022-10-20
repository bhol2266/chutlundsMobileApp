package com.bhola.chutlundsmobileapp;

public class VideoModel {

    String thumbnail , title , duration, likedPercent, views,previewVideo,href;

    public VideoModel() {
    }

    public VideoModel(String thumbnail, String title, String duration, String likedPercent, String views, String previewVideo, String href) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.duration = duration;
        this.likedPercent = likedPercent;
        this.views = views;
        this.previewVideo = previewVideo;
        this.href = href;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLikedPercent() {
        return likedPercent;
    }

    public void setLikedPercent(String likedPercent) {
        this.likedPercent = likedPercent;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getPreviewVideo() {
        return previewVideo;
    }

    public void setPreviewVideo(String previewVideo) {
        this.previewVideo = previewVideo;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
