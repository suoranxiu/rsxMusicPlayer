package com.example.testmusicplayer.pager;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.base.BasePager;

/**
 * 本地音乐的列表的页面
 */
public class ListPager extends BasePager {

    private ListView listView;
    private ProgressBar progressBar;
    private TextView textView;

    public ListPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.list_pager,null);
        listView = (ListView)view.findViewById(R.id.lv_lp_music_list);
        textView = (TextView)view.findViewById(R.id.tv_noMusic);
        progressBar = (ProgressBar)view.findViewById(R.id.pb_loading);
        return view;
    }

    @Override
    public void initDate() {
        //加载数据
        getDataFromLocal();
    }

    /**
     * 从本地的SD卡读取数据
     * 1.根据后缀名遍历SDcard（用不多）
     * 2.去内容提供者获取音乐文件
     */
    private void getDataFromLocal() {

    }
}
