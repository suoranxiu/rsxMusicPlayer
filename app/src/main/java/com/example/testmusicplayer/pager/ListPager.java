package com.example.testmusicplayer.pager;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.testmusicplayer.base.BasePager;

/**
 * 本地音乐的列表的页面
 */
public class ListPager extends BasePager {

    private TextView textView;

    public ListPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        Log.e("ListPager","listPage is initialized");
        textView = new TextView(context);
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    @Override
    public void initDate() {
        Log.e("ListPager","listPage data is initialized");
        textView.setText("music");
    }
}
