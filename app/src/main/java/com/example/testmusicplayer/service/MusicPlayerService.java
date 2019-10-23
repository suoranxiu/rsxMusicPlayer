package com.example.testmusicplayer.service;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.testmusicplayer.IMusicPlayerService;
import com.example.testmusicplayer.domain.MediaItem;
import com.example.testmusicplayer.utils.AlbumArt;
import com.example.testmusicplayer.utils.Grant;

import java.io.IOException;
import java.util.ArrayList;


public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    public static final String OPENAUDIO = "com.example.musicplayer_OPENAUDIO";
    private ArrayList<MediaItem> mediaItems;
    private  int position;
    private MediaItem mediaItem;
    private MediaPlayer mediaPlayer;

    private IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {

        MusicPlayerService musicPlayerService = MusicPlayerService.this;
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {
            musicPlayerService.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            musicPlayerService.start();

        }

        @Override
        public void pause() throws RemoteException {
            musicPlayerService.pause();
        }

        @Override
        public void stop() throws RemoteException {
            musicPlayerService.stop();
        }

        @Override
        public int getCurrentProgress() throws RemoteException {
            return musicPlayerService.getCurrentProgress();
        }

        @Override
        public int getDuration() throws RemoteException {
            return musicPlayerService.getDuration();
        }

        @Override
        public String getMusicName() throws RemoteException {
            return musicPlayerService.getMusicName();
        }

        @Override
        public String getArtist() throws RemoteException {
            return musicPlayerService.getArtist();
        }

        @Override
        public Bitmap getAlbumArt() throws RemoteException {
            return musicPlayerService.getAlbumArt();
        }

        @Override
        public void last() throws RemoteException {
            musicPlayerService.last();
        }

        @Override
        public void next() throws RemoteException {
            musicPlayerService.next();
        }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {
            musicPlayerService.setPlayMode(playMode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return musicPlayerService.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return musicPlayerService.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            musicPlayerService.seekTo(position);
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Tag","Creating Service");
        getDataFromLocal();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }


    private void getDataFromLocal() {

        mediaItems = new ArrayList<>();
        ContentResolver resolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] objs = {
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DATA,//音乐文件的绝对路径
                MediaStore.Audio.Media.ALBUM_ID//获取专辑ID
        };

        Cursor cursor = resolver.query(uri,objs,null,null,null);
        if(cursor != null){
            while (cursor.moveToNext()){
                MediaItem mediaItem = new MediaItem();

                mediaItems.add(mediaItem);

                String name = cursor.getString(0);
                mediaItem.setName(name);

                long duration = cursor.getLong(1);
                mediaItem.setDuration(duration);

                long size = cursor.getLong(2);
                mediaItem.setSize(size);

                String artist = cursor.getString(3);
                mediaItem.setArtist(artist);

                String album = cursor.getString(4);
                mediaItem.setAlbum(album);

                String data = cursor.getString(5);
                mediaItem.setData(data);

                int albumId = cursor.getInt(6);
                AlbumArt albumArt = new AlbumArt(MusicPlayerService.this,albumId);
                mediaItem.setAlbumArt(albumArt.getAlbumBmp());
            }
            Log.e("num of songs"," "+mediaItems.size());
            cursor.close();
        }


    }



    /**
     * 根据音乐列表位置，打开对应的音频文件
     * @param position
     */
    private void openAudio(int position){
        this.position = position;
        if(mediaItems != null && mediaItems.size() != 0){
            Log.e("position",""+position);
            Log.e("opening music",mediaItems.size()+"songs");
            mediaItem = mediaItems.get(position);
            if(mediaPlayer != null){
//                mediaPlayer.release();
                mediaPlayer.reset();
            }
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.setOnErrorListener(this);
                mediaPlayer.setDataSource(mediaItem.getData());
                mediaPlayer.prepareAsync();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 播放
     */
    private void start(){

        mediaPlayer.start();
    }
    /**
     * 暂停
     */
    private void pause(){
        mediaPlayer.pause();

    }
    private void stop(){
        mediaPlayer.stop();
    }
    /**
     * 得到当前音频的播放进度
     * @return
     */
    private int getCurrentProgress(){
        return mediaPlayer.getCurrentPosition();
    }
    /**
     * 得到当前音频的总时长
     * @return
     */
    private int getDuration(){
        return mediaPlayer.getDuration();
    }
    /**
     * 得到当前音频的名字
     * @return
     */
    private String getMusicName(){
        String fileName  = mediaItem.getName();
        String songName = fileName.substring(fileName.lastIndexOf("-")+2,fileName.lastIndexOf("."));
        return songName;
    }
    /**
     * 得到当前音频的艺术家
     * @return
     */
    private String getArtist(){
        return mediaItem.getArtist();
    }
    /**
     * 得到当前音频的专辑封面
     * @return
     */
    private Bitmap getAlbumArt(){
        return mediaItem.getAlbumArt();
    }

    /**
     * 播放上一首
     */
    private void last(){
    }

    /**
     * 播放下一首
     */
    private void next(){

    }

    /**
     * 设置播放模式
     * @param playMode
     */
    private void setPlayMode(int playMode){

    }
    private  int getPlayMode(){
        return 0;
    }

    /**
     * 查看播放器是否在播放
     * @return
     */
    private boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    /**
     * 拖动音频到指定位置
     * @param position
     */
    private void seekTo(int position){
        mediaPlayer.seekTo(position);
    }

    //播放器监听处理
    @Override
    public void onPrepared(MediaPlayer mp) {
        //通知activity来获取信息 --- 发送广播
        notifyChange(MusicPlayerService.OPENAUDIO);
        start();
    }

    private void notifyChange(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        next();
        return true;
    }
}
