package com.example.testmusicplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.testmusicplayer.IMusicPlayerService;
import com.example.testmusicplayer.R;
import com.example.testmusicplayer.adapter.AlbumContentAdapter;
import com.example.testmusicplayer.domain.Album;
import com.example.testmusicplayer.domain.MediaItem;
import com.example.testmusicplayer.service.MusicPlayerService;
import com.example.testmusicplayer.utils.AlbumArt;
import com.example.testmusicplayer.utils.Grant;
import com.example.testmusicplayer.utils.Utils;
import com.example.testmusicplayer.view.BaseVisualizerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumContentActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    @BindView(R.id.ly_albumContent)
    LinearLayout ly_albumContent;

    @BindView(R.id.iv_album_content)
    ImageView iv_album_content;

    @BindView(R.id.tv_albumName_albumContent)
    TextView tv_albumName_albumContent;

    @BindView(R.id.tv_artist_albumContent)
    TextView tv_artist_albumContent;

    @BindView(R.id.tv_album_info)
    TextView tv_album_info;

    @BindView(R.id.ly_return_albumContent)
    LinearLayout ly_return_albumContent;

    @BindView(R.id.lv_song_albumContent)
    ListView lv_song_albumContent;


    private BaseVisualizerView baseVisualizerView;

    private int albumPosition;
    private int songPosition;

    private ArrayList<Album> albumList;

    private int totalTime;
    private int totalNums;
    private Utils utils = new Utils();
    private Visualizer visualizer;

    private MyReceiver receiver;
    private boolean onBinded = false;
    private IMusicPlayerService iService;//服务的代理类，通过它可以调用服务类方法
    private ServiceConnection con = new ServiceConnection() {
        /**
         * 当连接成功的时候回调此方法
         * @param name
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            Log.e("Tag","onServiceConnected!");
            iService = IMusicPlayerService.Stub.asInterface(iBinder);
            if(iService != null){
                try {
                    iService.changeToALbumList(albumPosition);
                    iService.openAudio(songPosition);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 当断开连接的时候回调这个方法
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            if(iService != null){
                try {
                    iService.stop();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_content);
        ButterKnife.bind(this);
        setListener();
        getPos();
        getData();
        initData();
        initReceiver();

    }

    public void setListener(){

        ly_return_albumContent.setOnClickListener(this);
        lv_song_albumContent.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == ly_return_albumContent){
            finish();
//            Log.e("click","onDestroy");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        songPosition = position;
        Log.e("click"," "+position);
        if(!onBinded){
            bindStartService(songPosition);
        }else {
            try {
                iService.openAudio(songPosition);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }


    }


    private void bindStartService(int songPosition) {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.putExtra("isLocalList",false);
        intent.putExtra("songPosition",songPosition);
        intent.setAction(MusicPlayerService.OPENAUDIO);
        bindService(intent,con, Context.BIND_AUTO_CREATE);
        startService(intent);//不至于实例化多个服务
        onBinded = true;
    }
    public void getPos(){

        Intent intent = getIntent();

        albumPosition = intent.getIntExtra("position",0);

    }

    public void getData(){

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

        Cursor cursor = resolver.query(uri, objs, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MediaItem mediaItem = new MediaItem();

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
                AlbumArt albumArt = new AlbumArt(this, albumId);
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
            cursor.close();
        }

    }

    public void initData(){

        Album album = albumList.get(albumPosition);

        iv_album_content.setImageBitmap(album.getAlbumArt().bmp);
        ly_albumContent.setBackground(new BitmapDrawable(album.getAlbumArt().blurBitmap(20,4)));
        tv_albumName_albumContent.setText(album.getAlbumName());
        tv_artist_albumContent.setText(album.getArtist());
        getTotalNums(album);
        getTotalTime(album);
        tv_album_info.setText(totalNums+" songs,  "+totalTime+" minutes");
        AlbumContentAdapter albumContentAdapter = new AlbumContentAdapter(this,album.getAlbumMap());
        lv_song_albumContent.setAdapter(albumContentAdapter);


    }

    @Override
    protected void onDestroy() {
        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null; }
        if (onBinded){
            unbindService(con);
            onBinded = false;
        }
        super.onDestroy();
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

    public void getTotalTime(Album album){
        long temp = 0;
        MediaItem mediaItem;
        for(int i =0;i<album.getAlbumMap().size();i++){
           mediaItem =  album.getAlbumMap().get(i);
           long duration = mediaItem.getDuration();
           temp += duration;
        }
        int t = (int)(temp);
        String time = utils.stringForTime(t);
//        Log.e("duration",time);
        totalTime = Integer.parseInt(time.split(":")[0]);
    }
    public void getTotalNums(Album album){
        totalNums = album.getAlbumMap().size();
    }

    private void setUpVisualizer(BaseVisualizerView view){

        try {
            if(Grant.isGrantRecordAudio(this)){
                int audioSessionid = iService.getAudioSessionId();
                visualizer = new Visualizer(audioSessionid);
                visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
                view.setVisualizer(visualizer);
                visualizer.setEnabled(true);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void updateItemView(){

        View view = lv_song_albumContent.getChildAt(songPosition);
        AlbumContentAdapter.ViewHolder viewHolder = (AlbumContentAdapter.ViewHolder)view.getTag();
        baseVisualizerView = viewHolder.baseVisualizerView;
        baseVisualizerView.setVisibility(View.VISIBLE);
        setUpVisualizer(baseVisualizerView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(visualizer != null){
            visualizer.release();
        }
    }


    private void initReceiver(){
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.PLAYED);
        registerReceiver(receiver,intentFilter);
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateItemView();
//            Log.e("abContent","received");
        }
    }

}
