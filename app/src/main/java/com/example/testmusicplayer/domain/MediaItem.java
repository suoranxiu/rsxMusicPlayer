package com.example.testmusicplayer.domain;

import androidx.annotation.NonNull;

/**
 * 代表一个音频
 */
public class MediaItem {

    private String name;
    private String data;
    private String artist;
    private String album;
    private long duration;
    private long size;

    public MediaItem() {
    }

    public MediaItem(String name, String data, String artist, String album, long duration, long size) {
        this.name = name;
        this.data = data;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String    toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", data='" + data + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                '}';
    }
}
