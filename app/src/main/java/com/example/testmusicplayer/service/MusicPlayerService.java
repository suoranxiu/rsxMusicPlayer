package com.example.testmusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.example.testmusicplayer.IMusicPlayerService;


public class MusicPlayerService extends Service {

    public static final String OPENAUDIO = "com.example.musicplayer_OPENAUDIO";

    private IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {

        }

        @Override
        public void start() throws RemoteException {

        }

        @Override
        public void pause() throws RemoteException {

        }

        @Override
        public int getCurrentProgress() throws RemoteException {
            return 0;
        }

        @Override
        public int getDuration() throws RemoteException {
            return 0;
        }

        @Override
        public String getMusicName() throws RemoteException {
            return null;
        }

        @Override
        public String getArtist() throws RemoteException {
            return null;
        }

        @Override
        public Bitmap getAlnumArt() throws RemoteException {
            return null;
        }

        @Override
        public void last() throws RemoteException {

        }

        @Override
        public void next() throws RemoteException {

        }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {

        }

        @Override
        public int getPlayMode() throws RemoteException {
            return 0;
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /**
     * 根据音乐列表位置，打开对应的音频文件
     * @param position
     */
    private void openAudio(int position){

    }

    /**
     * 播放
     */
    private void start(){

    }
    /**
     * 暂停
     */
    private void pause(){

    }
    /**
     * 得到当前音频的播放进度
     * @return
     */
    private int getCurrentProgress(){
        return 0;
    }
    /**
     * 得到当前音频的总时长
     * @return
     */
    private int getDuration(){
        return 0;
    }
    /**
     * 得到当前音频的名字
     * @return
     */
    private String getMusicName(){
        return null;
    }
    /**
     * 得到当前音频的艺术家
     * @return
     */
    private String getArtist(){
        return null;
    }
    /**
     * 得到当前音频的专辑封面
     * @return
     */
    private Bitmap getAlnumArt(){
        return null;
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
}
