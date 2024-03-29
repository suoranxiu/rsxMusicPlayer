package com.example.testmusicplayer.pager;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.testmusicplayer.base.BasePager;

/**
 * 设置页面
 */
public class SettingPager extends BasePager {

    private TextView textView;

    public SettingPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        Log.e("SettingPager","SettingPager is initialized");
        textView = new TextView(context);
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    @Override
    public void initDate() {
        Log.e("SettingPager","SettingPager data is initialized");
        textView.setText("Setting");
    }
}
