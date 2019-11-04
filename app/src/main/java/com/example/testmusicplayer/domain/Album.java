package com.example.testmusicplayer.domain;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

public class Album {

    private Map<Integer, MediaItem> AlbumMap;
    private String albumName;
    private String artist;
    int position;
    private Bitmap albumArt;

    public Album(String albumName, String artist) {
        this.albumName = albumName;
        this.artist = artist;
        this.position = 0;
        this.AlbumMap = new HashMap<>();
    }
    public void putSong(MediaItem mediaItem){

        AlbumMap.put(position,mediaItem);
        if(position == 0){
            this.albumArt = mediaItem.getAlbumArt();
        }
        position ++;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getArtist() {
        return artist;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Bitmap getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(Bitmap albumArt) {
        this.albumArt = albumArt;
    }

    public Map<Integer, MediaItem> getAlbumMap(){
        return AlbumMap;
    }
}
