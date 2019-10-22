package com.example.testmusicplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testmusicplayer.IMusicPlayerService;
import com.example.testmusicplayer.R;
import com.example.testmusicplayer.domain.MediaItem;
import com.example.testmusicplayer.service.MusicPlayerService;
import com.example.testmusicplayer.utils.Utils;

import java.util.ArrayList;

public class AudioPlayerActivity extends Activity implements View.OnClickListener {

    private View view_return_line;
    private ImageView iv_album_playing;
    private TextView tv_name_playing;
    private TextView tv_artist_playing;
    private SeekBar sb_playing;
    private TextView tv_time_playing;
    private TextView tv_time_duration;
    private Button btn_last_playing;
    private Button btn_start_playing;
    private Button btn_next_playing;
    private SeekBar sb_volume;
    private ImageView iv_random_playing;
    private ImageView iv_loop_playing;

    private Utils utils;
    private MyReceiver receiver;


    private  int position;
    private static final int PROGRESS = 1;
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
                    iService.openAudio(position);
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
        setContentView(R.layout.activity_audio_player);
        findViews();
        initData();
        getData();
        bindStartService();

    }

    @Override
    protected void onDestroy() {
        if(receiver != null){
            handler.removeMessages(PROGRESS);
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }

    private void initData() {
        //注册广播
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
        registerReceiver(receiver,intentFilter);
    }


    private void bindStartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.example.musicplayer_OPENAUDIO");
        bindService(intent,con, Context.BIND_AUTO_CREATE);
        startService(intent);//不至于实例化多个服务
    }

    private void getData() {
        position = getIntent().getIntExtra("position",0);
    }
    public void findViews(){
        btn_start_playing = (Button)findViewById(R.id.btn_start_playing);
        btn_last_playing = (Button)findViewById(R.id.btn_last_playing);
        btn_next_playing = (Button)findViewById(R.id.btn_next_playing);

        tv_name_playing = (TextView)findViewById(R.id.tv_name_playing);
        tv_artist_playing = (TextView)findViewById(R.id.tv_artist_playing);
        iv_album_playing = (ImageView) findViewById(R.id.iv_album_playing);

        tv_time_playing = (TextView)findViewById(R.id.tv_time_playing);
        tv_time_duration = (TextView)findViewById(R.id.tv_time_duration);

        sb_playing = (SeekBar)findViewById(R.id.sb_playing);
        sb_volume = (SeekBar)findViewById(R.id.sb_volume);


        btn_start_playing.setOnClickListener(this);
        btn_last_playing.setOnClickListener(this);
        btn_next_playing.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if(v == btn_start_playing){
            if(iService != null){
                try {
                    if(iService.isPlaying()){
                        iService.pause();
                        btn_start_playing.setBackgroundResource(R.drawable.btn_start_playing_selector);
                    }else {
                        iService.start();
                        btn_start_playing.setBackgroundResource(R.drawable.btn_pause_playing_selector);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }else if(v == btn_last_playing){

        }else if(v == btn_next_playing){

        }
    }

    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            showViewData();
        }
    }

    private void showViewData() {

        try {
            tv_name_playing.setText(iService.getMusicName());
            tv_artist_playing.setText(iService.getArtist());
            iv_album_playing.setImageBitmap(iService.getAlbumArt());
            sb_playing.setMax(iService.getDuration());

            handler.sendEmptyMessage(PROGRESS);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PROGRESS:
                    try {
                        //获取当前的进度
                        int curProgress = iService.getCurrentProgress();
                        //设置在播放页面的进程SeekBar上
                        sb_playing.setProgress(curProgress);
                        Log.e("curProgress"," "+utils.stringForTime(curProgress));
                        Log.e("max"," "+utils.stringForTime(iService.getDuration()));
                        //时间更新
//                        tv_time_playing.setText(utils.stringForTime(curProgress));
//                        tv_time_duration.setText(utils.stringForTime(iService.getDuration()));
                        tv_time_playing.setText("....");
                        tv_time_duration.setText("max");
                        //每秒更新一次
                        handler.removeMessages(PROGRESS);
                        handler.sendEmptyMessageDelayed(PROGRESS,1000);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
            }



        }
    };
}
