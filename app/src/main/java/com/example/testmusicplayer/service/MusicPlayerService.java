package com.example.testmusicplayer.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;

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
import com.example.testmusicplayer.R;
import com.example.testmusicplayer.activity.AudioPlayerActivity;
import com.example.testmusicplayer.domain.Album;
import com.example.testmusicplayer.domain.MediaItem;
import com.example.testmusicplayer.utils.AlbumArt;
import com.example.testmusicplayer.utils.CacheUtils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    //broadcast 的action
    public static final String OPENAUDIO = "com.example.musicplayer_OPENAUDIO";
    public static final String PLAYED = "com.example.musicplayer_PLAYED";

    //播放模式
    public static final int LOOP = 1;
    public static final int RANDOM = 2;
    public static final int LOOP_RANDOM = 3;
    public static final int LOOP_ONE = 4;

    private int playMode = LOOP;

    //是否从ListPager中传来的请求
    private boolean isLocalList = true;

    //当前列表中的哪一首
    private  int position;
    private int albumPosition;
    private int randomIndex;

    //service中所用到的对象
    private ArrayList<MediaItem> mediaItems;
    private ArrayList<Album> albumList;
    private ArrayList<MediaItem> playList;
    private int[] randomIndexList;
    private MediaItem mediaItem;
    private MediaPlayer mediaPlayer;

    private NotificationManager notifyManager;

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

        @Override
        public void changeToALbumList(int albumPosition) throws RemoteException {
            musicPlayerService.changeToALbumList(albumPosition);
        }

        @Override
        public void changeToLocalList() throws RemoteException {
            musicPlayerService.changeToLocalList();
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return musicPlayerService.getAudioSessionId();
        }


    };



    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Tag","Creating Service");

        playMode = CacheUtils.getPlaymode(this,"playmode");

        getDataFromLocal();
        if(playMode == LOOP_RANDOM){
            disorderItems();
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }



    private void getDataFromLocal() {

        mediaItems = new ArrayList<>();
        albumList = new ArrayList<>();

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
                mediaItem.setAlbumArt(albumArt);

                //查看是否专辑和歌手已经存在
                int albumIndex = checkAlbum(album,artist);
                if(albumIndex < 0){
                    //若不存在
                    Album albumItem = new Album(album,artist);
                    albumItem.putSong(mediaItem);
                    albumList.add(albumItem);
                }else {
                    //若存在
                    Album albumItem = albumList.get(albumIndex);
                    albumItem.putSong(mediaItem);
                }
            }
            Log.e("num of songs"," "+(mediaItems.size()));
            cursor.close();
        }


    }



    /**
     * 根据音乐列表位置，打开对应的音频文件
     * @param position
     */
    private void openAudio(int position){
        this.position = position;
        if(playList != null && playList.size() != 0){
            Log.e("cur position",""+position);
            mediaItem = playList.get(position);

            if(mediaPlayer != null){

                mediaPlayer.reset();
            }
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.setOnErrorListener(this);
                mediaPlayer.setDataSource(mediaItem.getData());
                mediaPlayer.prepareAsync();

                if(playMode == LOOP_ONE){
                    //单曲循环--就不会触发播放完成的回调
                    mediaPlayer.setLooping(true);
                    Log.e("TAG","set looping");
                }else {
                    mediaPlayer.setLooping(false);
                }

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

        //通知主页activity改变logo颜色
        notifyChange(MusicPlayerService.PLAYED);

        //当正在播放歌曲的时候，在状态栏显示当前正在播放的信息，点击此，可以进入播放界面activity
        notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this,AudioPlayerActivity.class);
        intent.putExtra("Notification",true);//用于区分是否来自状态栏
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.music_player_icon2_press)
                .setContentTitle("SRX player")
                .setContentText("Playing: "+getMusicName())
                .setContentIntent(pendingIntent)
                .build();
        notifyManager.notify(1,notification);
    }
    /**
     * 暂停
     */
    private void pause(){
        mediaPlayer.pause();

    }
    private void stop(){
        mediaPlayer.stop();
        notifyManager.cancel(1);
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
        return mediaItem.getAlbumArt().bmp;
    }

    /**
     * 播放上一首
     */
    private void last(){
        setLastPosition();
        openLastAudio();
    }

    private void openLastAudio() {
        openAudio(position);
    }

    private void setLastPosition() {
        int playMode = getPlayMode();
        if(playMode == MusicPlayerService.LOOP){
            position --;
            if(position < 0){
                position = playList.size() - 1;
            }
        }else if(playMode == MusicPlayerService.LOOP_ONE){
            position --;
            if(position < 0){
                position = playList.size()-1;
            }
        }else if(playMode == MusicPlayerService.RANDOM){

            position = (int)(Math.random()*(playList.size()-1));

        }else if(playMode == MusicPlayerService.LOOP_RANDOM){
            randomIndex -- ;
            if(randomIndex < 0){
                randomIndex = randomIndexList.length - 1;
            }
            position = randomIndexList[randomIndex];
        }
    }

    /**
     * 播放下一首
     */
    private void next(){
        setNextPosition();
        openNextAudio();
    }

    private void openNextAudio() {
        openAudio(position);
    }

    private void setNextPosition() {
        int playMode = getPlayMode();
        if(playMode == MusicPlayerService.LOOP){
            position++;
            if(position >= playList.size()){
                position = 0;
            }
        }else if(playMode == MusicPlayerService.LOOP_ONE){
            position++;
            if(position >= playList.size()){
                position = 0;
            }
        }else if(playMode == MusicPlayerService.RANDOM){

            position = (int)(Math.random()*(playList.size()-1));

        }else if(playMode == MusicPlayerService.LOOP_RANDOM){
            randomIndex++;
            if(randomIndex >= randomIndexList.length){
                randomIndex = 0;
            }
            position = randomIndexList[randomIndex];
        }
//        Log.e("playMode:",playMode+"");
//        Log.e("next position:",position+"");

    }

    /**
     * 设置播放模式
     * @param playMode
     */
    private void setPlayMode(int playMode){
        this.playMode = playMode;
        if(playMode == LOOP_RANDOM){
            disorderItems();
        }
        if(playMode == LOOP_ONE){
            //单曲循环--就不会触发播放完成的回调
            mediaPlayer.setLooping(true);
            Log.e("TAG","set looping");
        }else {
            mediaPlayer.setLooping(false);
        }
        CacheUtils.putPlaymode(this,"playmode",playMode);
    }

    /**
     * 随机生成与音乐列表长度相同的乱序索引List
     */
    private void disorderItems() {
        int size = playList.size();
        randomIndexList = new int[size];
        Random random = new Random();
        List<Integer> lst = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            lst.add(i);
        }
        int index = 0;
        for (int i = 0; i < size; i++) {
            index = random.nextInt(size - i);
            randomIndexList[i] = lst.get(index);
            lst.remove(index);
        }
        randomIndex = position;
    }


    /**
     * 返回播放模式
     * @return
     */
    private  int getPlayMode(){

        return playMode;
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

    /**
     * 设置播放列表为专辑列表
     */
    private void changeToALbumList(int albumPosition){
        this.albumPosition = albumPosition;
        int songNums = albumList.get(albumPosition).getAlbumMap().size();
        ArrayList<MediaItem> list = new ArrayList<>(songNums);
        for(int i =0;i<songNums;i++){
            list.add(albumList.get(albumPosition).getAlbumMap().get(i));
        }
        playList = list;
    }

    /**
     * 设置播放列表为所有本地歌曲的列表
     */
    private void changeToLocalList(){
        playList = mediaItems;
    }

    /**
     * 返回当前的播放器对象session_id
     * @return
     */
    private int getAudioSessionId(){
        return mediaPlayer.getAudioSessionId();
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

    public int checkAlbum(String albumName,String artist){
        int existed = -1;

        for(int i = 0;i<albumList.size();i++){
            if(albumName.equals(albumList.get(i).getAlbumName()) && artist.equals(albumList.get(i).getArtist())){
                existed =  i;
                break;
            }
        }
        return existed;
    }
}
