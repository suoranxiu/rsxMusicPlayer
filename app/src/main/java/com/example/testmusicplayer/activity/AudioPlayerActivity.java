package com.example.testmusicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.testmusicplayer.R;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);

    }
}
