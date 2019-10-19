package com.example.testmusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;

import androidx.annotation.Nullable;


public class MusicPlayerService extends Service {



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
