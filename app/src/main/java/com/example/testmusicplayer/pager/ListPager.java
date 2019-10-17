package com.example.testmusicplayer.pager;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.base.BasePager;
import com.example.testmusicplayer.domain.MediaItem;
import com.example.testmusicplayer.utils.Grant;

import java.util.ArrayList;

/**
 * 本地音乐的列表的页面
 */
public class ListPager extends BasePager {

    private ListView listView;
    private ProgressBar progressBar;
    private TextView textView;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size()>0){

            }else {

            }
        }
    };

    /**
     * 数据集合
     */
    private ArrayList<MediaItem> mediaItems;

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
     * 3.如果是6.0的系统，动态获取读取权限
     */
    private void getDataFromLocal() {

        mediaItems = new ArrayList<>();

        new Thread(){
            @Override
            public void run() {
                super.run();

                ContentResolver resolver = context.getContentResolver();
                if(Grant.isGrantExternalRW((Activity)context)){
                    Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    String[] objs = {
                            MediaStore.Audio.Media.DISPLAY_NAME,
                            MediaStore.Audio.Media.DURATION,
                            MediaStore.Audio.Media.SIZE,
                            MediaStore.Audio.Media.ARTIST,
                            MediaStore.Audio.Media.ALBUM,
                            MediaStore.Audio.Media.DATA,//音乐文件的绝对路径
                    };

                    Cursor cursor = resolver.query(uri,objs,null,null,null);
                    if(cursor != null){
                        do{
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

                        }while (cursor.moveToNext());
                        cursor.close();
                    }

                    handler.sendEmptyMessage(10);

                }

            }
        }.start();
    }
}
