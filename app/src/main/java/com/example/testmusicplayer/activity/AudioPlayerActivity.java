package com.example.testmusicplayer.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.testmusicplayer.IMusicPlayerService;
import com.example.testmusicplayer.R;
import com.example.testmusicplayer.domain.MediaItem;
import com.example.testmusicplayer.service.MusicPlayerService;

import java.util.ArrayList;

public class AudioPlayerActivity extends Activity {

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


    private  int position;
    private IMusicPlayerService iService;//服务的代理类，通过它可以调用服务类方法
    private ServiceConnection con = new ServiceConnection() {
        /**
         * 当连接成功的时候回调此方法
         * @param name
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
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
        getData();
        bindStartService();

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
}
