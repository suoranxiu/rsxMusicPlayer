package com.example.testmusicplayer.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

public class AlbumArt {
    private Context context;
    private int albumId;
    private String uriAlbums = "content://media/external/audio/albums";
    public Bitmap bmp;

    public AlbumArt() {
    }

    public AlbumArt(Context context,int albumId) {
        this.context = context;
        this.albumId = albumId;
        this.bmp = getAlbumBmp();

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
    public Bitmap blurBitmap(int radius,int mSampling){
    //缩放
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int scaledWidth = width / 4;
        int scaledHeight = height / 4;
        Bitmap bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1.0F / (float)mSampling, 1.0F / (float)mSampling);
        Paint paint = new Paint();
        paint.setFlags(2);
        canvas.drawBitmap(bmp, 0.0F, 0.0F, paint);
    //模糊处理
        Bitmap newBitmap;
        if(Build.VERSION.SDK_INT >= 18) {
            newBitmap = RSBlur.rsBlur(context, bitmap, radius);
        } else {
            newBitmap = FastBlur.blur(bitmap,radius, true);
        }
        return newBitmap;
    }

}
