package com.example.testmusicplayer.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.base.BasePager;
import com.example.testmusicplayer.pager.AlbumPager;
import com.example.testmusicplayer.pager.ArtistPager;
import com.example.testmusicplayer.pager.ListPager;
import com.example.testmusicplayer.pager.SettingPager;
import com.example.testmusicplayer.utils.ReplaceFragment;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private FrameLayout fl_main_content;
    private RadioGroup rg_bottom_tag;

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


        basePagers = new ArrayList<>();
        basePagers.add(new ListPager(this));
        basePagers.add(new AlbumPager(this));
        basePagers.add(new ArtistPager(this));
        basePagers.add(new SettingPager(this));
        rg_bottom_tag.setOnCheckedChangeListener(this);

        rg_bottom_tag.check(R.id.rb_list);//默认选中播放列表
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
}
