// IMusicPlayerService.aidl
package com.example.testmusicplayer;

// Declare any non-default types here with import statements

interface IMusicPlayerService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,double aDouble, String aString);
    /**
     * 根据音乐列表位置，打开对应的音频文件
     * @param position
     */
    void openAudio(int position);
    /**
     * 播放
     */
    void start();
    /**
     * 暂停
     */
    void pause();
    void stop();
    /**
     * 得到当前音频的播放进度
     * @return
     */
    int getCurrentProgress();
    /**
     * 得到当前音频的总时长
     * @return
     */
    int getDuration();
    /**
     * 得到当前音频的名字
     * @return
     */
    String getMusicName();
    /**
     * 得到当前音频的艺术家
     * @return
     */
    String getArtist();
    /**
     * 得到当前音频的专辑封面
     * @return
     */
    Bitmap getAlnumArt();

    /**
     * 播放上一首
     */
    void last();
    /**
     * 播放下一首
     */
    void next();

    /**
     * 设置播放模式
     * @param playMode
     */
    void setPlayMode(int playMode);
    int getPlayMode();

    boolean isPlaying();
}
