package com.example.testmusicplayer.pager;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.activity.AudioPlayerActivity;
import com.example.testmusicplayer.adapter.ListPagerAdapter;
import com.example.testmusicplayer.base.BasePager;
import com.example.testmusicplayer.domain.MediaItem;
import com.example.testmusicplayer.utils.AlbumArt;
import com.example.testmusicplayer.utils.Grant;

import java.util.ArrayList;

/**
 * 本地音乐的列表的页面
 */
public class ListPager extends BasePager implements AdapterView.OnItemClickListener {

    private ListView listView;
    private ProgressBar pb_loading;
    private TextView tv_noMusic;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size()>0){
                //设置适配器
                BaseAdapter adapter = new ListPagerAdapter(mediaItems,context);
                listView.setAdapter(adapter);
            }else {
                tv_noMusic.setVisibility(View.VISIBLE);
            }
            pb_loading.setVisibility(View.GONE);
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
        tv_noMusic = (TextView)view.findViewById(R.id.tv_noMusic);
        pb_loading = (ProgressBar)view.findViewById(R.id.pb_loading);

        //设置ListView的Item点击事件
        listView.setOnItemClickListener(this);
        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MediaItem mediaItem = mediaItems.get(position);

        /**
         * 调用全系统的可用播放器进行播放--隐式Intent
         */
//        Intent intent = new Intent();
//        intent.setDataAndType(Uri.parse(mediaItem.getData()),"audio/*");
//        context.startActivity(intent);

        /**
         * 调用自己的播放器--显示Intent
         */
        Intent intent = new Intent(context, AudioPlayerActivity.class);
        intent.putExtra("position",position);
        context.startActivity(intent);

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
                            MediaStore.Audio.Media.ALBUM_ID//获取专辑ID
                    };

                    Cursor cursor = resolver.query(uri,objs,null,null,null);
                    if(cursor != null){
                        while (cursor.moveToNext()){
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

                            int albumId = cursor.getInt(6);
                            AlbumArt albumArt = new AlbumArt(context,albumId);
                            mediaItem.setAlbumArt(albumArt.getAlbumBmp());
                        }
                        cursor.close();
                    }
                    handler.sendEmptyMessage(10);
                }

            }
        }.start();
    }

}
