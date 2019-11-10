package com.example.testmusicplayer.domain;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Artist {

    private String name;
    private Map<Integer,Album> albumMap;
    private Bitmap portrait;
    int position;

    public Artist(String name) {
        this.name = name;
        this.albumMap = new HashMap<>();;
        position = 0;
    }

    public void putAlbum(Album album){
        albumMap.put(position,album);
        position++;
    }

    public int checkAlbum(Album album){
        int index = -1;
        for (int i =0;i<albumMap.size();i++){
            if(albumMap.get(i).getAlbumName() == album.getAlbumName()){
                return i;
            }
        }
        return index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Integer, Album> getAlbumMap() {
        return albumMap;
    }

    public void setAlbumMap(Map<Integer, Album> albumMap) {
        this.albumMap = albumMap;
    }

    public Bitmap getPortrait() {
        return portrait;
    }

    public void setPortrait(Bitmap portrait) {
        this.portrait = portrait;
    }
}
