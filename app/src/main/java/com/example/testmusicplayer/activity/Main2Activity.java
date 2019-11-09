package com.example.testmusicplayer.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.View;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.base.BasePager;
import com.example.testmusicplayer.pager.AlbumPager;
import com.example.testmusicplayer.pager.ArtistPager;
import com.example.testmusicplayer.pager.ListPager;
import com.example.testmusicplayer.pager.SettingPager;
import com.example.testmusicplayer.service.MusicPlayerService;
import com.example.testmusicplayer.utils.ReplaceFragment;
import com.example.testmusicplayer.view.MusicButton;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private FrameLayout fl_main_content;
    private RadioGroup rg_bottom_tag;
    private RadioButton rb_list;
    private RadioButton rb_album;
    private RadioButton rb_artist;
    private RadioButton rb_setting;
    private ImageView iv_music_player;
    private MusicButton music_btn_rotation;
    private boolean isPlaying = false;

    /**
     * 页面的集合
     */
    private ArrayList<BasePager> basePagers;

    private int position;//底部栏选中的位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        fl_main_content = (FrameLayout)findViewById(R.id.fl_main_content);
        rg_bottom_tag = (RadioGroup)findViewById(R.id.rg_bottom_tag);

        rb_list = (RadioButton)findViewById(R.id.rb_list);
        rb_album = (RadioButton)findViewById(R.id.rb_album);
        rb_artist = (RadioButton)findViewById(R.id.rb_artist);
        rb_setting = (RadioButton)findViewById(R.id.rb_setting);

        music_btn_rotation = (MusicButton)findViewById(R.id.music_btn_rotation);
        iv_music_player = (ImageView)findViewById(R.id.iv_music_player);

        setImageSize(80);

        basePagers = new ArrayList<>();
        basePagers.add(new ListPager(this));
        basePagers.add(new AlbumPager(this));
        basePagers.add(new ArtistPager(this));
        basePagers.add(new SettingPager(this));
        rg_bottom_tag.setOnCheckedChangeListener(this);

        rg_bottom_tag.check(R.id.rb_list);//默认选中播放列表

        music_btn_rotation.setOnClickListener(this);

        initReceiver();
    }

    private void setImageSize(int size) {
        Drawable listDrawable = getResources().getDrawable(R.drawable.rb_list_drawable_selector);
        listDrawable.setBounds(0, 0, size, size);
        rb_list.setCompoundDrawables(null, listDrawable, null, null);

        Drawable albumDrawable = getResources().getDrawable(R.drawable.rb_album_drawable_selector);
        albumDrawable.setBounds(0, 0, size, size);
        rb_album.setCompoundDrawables(null, albumDrawable, null, null);

        Drawable artistDrawable = getResources().getDrawable(R.drawable.rb_artist_drawable_selector);
        artistDrawable.setBounds(0, 0, size, size);
        rb_artist.setCompoundDrawables(null, artistDrawable, null, null);

        Drawable settingDrawable = getResources().getDrawable(R.drawable.rb_setting_drawable_selector);
        settingDrawable.setBounds(0, 0, size, size);
        rb_setting.setCompoundDrawables(null, settingDrawable, null, null);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            default:
                position = 0;
                break;
            case R.id.rb_album:
                position = 1;
                break;
            case R.id.rb_artist:
                position = 2;
                break;
            case R.id.rb_setting:
                position = 3;
                break;
        }
        setFragment();
    }

    /**
     * 把页面添加到Fragment中
     */
    private void setFragment() {
        //得到FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //替换
        fragmentTransaction.replace(R.id.fl_main_content,new ReplaceFragment(getBasePager()));
        //提交事务
        fragmentTransaction.commit();
    }

    /**
     * 根据位置，得到对应的页面
     * @return
     */
    private BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        if(basePager != null && !basePager.isInitData){
            basePager.initDate();
            basePager.isInitData = true;
        }
        return basePager;
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(this,AudioPlayerActivity.class);
        intent.putExtra("Notification",true);
        startActivity(intent);

    }

    private void initReceiver(){
        MyReceiver receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.PLAYED);
        intentFilter.addAction(MusicPlayerService.PAUSED);
        registerReceiver(receiver,intentFilter);
    }
    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == MusicPlayerService.PLAYED){
                isPlaying = true;
            }else if(intent.getAction() == MusicPlayerService.PAUSED){
                isPlaying = false;
            }
            changeLogo();
        }
    }

    private void changeLogo(){
        iv_music_player.setVisibility(View.GONE);
        music_btn_rotation.setVisibility(View.VISIBLE);
        if(isPlaying){
            music_btn_rotation.playMusic();
        }else{
            music_btn_rotation.pauseMusic();
        }

    }
}
