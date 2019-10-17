package com.example.testmusicplayer.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class AlbumArt {
    private Context context;
    private int albumId;
    private String uriAlbums = "content://media/external/audio/albums";

    public AlbumArt() {
    }

    public AlbumArt(Context context,int albumId) {
        this.context = context;
        this.albumId = albumId;
    }
    public Bitmap getAlbumBmp(){

        String[] projection = {"album_art"};
        Cursor cur = context.getContentResolver().query(Uri.parse(uriAlbums + "/" + Integer.toString(albumId)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        Bitmap bmp = null;
        if (album_art != null) {
            bmp = BitmapFactory.decodeFile(album_art);
        }
        return bmp;
    }

}
