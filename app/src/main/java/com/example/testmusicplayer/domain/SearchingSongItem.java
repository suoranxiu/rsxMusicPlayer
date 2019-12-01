package com.example.testmusicplayer.domain;

public class SearchingSongItem {

    private MediaItem mediaItem;

    private int listIndex;

    public SearchingSongItem(MediaItem mediaItem, int listIndex) {
        this.mediaItem = mediaItem;
        this.listIndex = listIndex;
    }

    public MediaItem getMediaItem() {
        return mediaItem;
    }

    public void setMediaItem(MediaItem mediaItem) {
        this.mediaItem = mediaItem;
    }

    public int getListIndex() {
        return listIndex;
    }

    public void setListIndex(int listIndex) {
        this.listIndex = listIndex;
    }

}
