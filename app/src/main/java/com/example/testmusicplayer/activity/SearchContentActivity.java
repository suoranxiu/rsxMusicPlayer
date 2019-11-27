package com.example.testmusicplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.View;

import android.widget.ListView;
import android.widget.TextView;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.adapter.SearchSongAdapter;
import com.example.testmusicplayer.domain.MediaItem;
import com.example.testmusicplayer.utils.AlbumArt;
import com.example.testmusicplayer.utils.FilterListener;
import com.example.testmusicplayer.utils.Grant;
import com.example.testmusicplayer.utils.OnTextChangedListener;
import com.example.testmusicplayer.view.SearchEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SearchContentActivity extends AppCompatActivity {


    private ArrayList<MediaItem> mediaItems;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            initTextListener();
        }
    };

    @BindView(R.id.et_search)
    SearchEditText et_search;

    @BindView(R.id.tv_cancel_searching)
    TextView tv_cancel_searching;

    @BindView(R.id.lv_searching_songs)
    ListView lv_searching_songs;

    @BindView(R.id.tv_search_nodata)
    TextView tv_search_nodata;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_content);
        ButterKnife.bind(this);
        getLocalData();
    }


    public void showSongsList(){

        lv_searching_songs.setVisibility(View.VISIBLE);
        //获得输入的内容
        String str = et_search.getText().toString().trim();
        Log.e("searching key",str);
        //设置adapter
        SearchSongAdapter adapter = new SearchSongAdapter(mediaItems, this);
        //adapter根据关键词过滤结果
        adapter.getFilter().filter(str);
        lv_searching_songs.setAdapter(adapter);
    }

    public void initTextListener(){

        et_search.setOnTextChangedListener(new OnTextChangedListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
                showSongsList();
                Log.e("searching et","onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("searching et","afterTextChanged");
            }

            @Override
            public void clearSearchingList() {
                lv_searching_songs.setVisibility(View.GONE);
                Log.e("searching et","clearSearchingList");
            }
        });
    }


    @OnClick({R.id.tv_cancel_searching})
    public void onClick(View v){
        if(v == tv_cancel_searching){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void getLocalData(){
        mediaItems = new ArrayList<>();

        new Thread(){
            @Override
            public void run() {
                super.run();

                ContentResolver resolver = SearchContentActivity.this.getContentResolver();
                if(Grant.isGrantExternalRW((Activity)SearchContentActivity.this)){
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
                            AlbumArt albumArt = new AlbumArt(SearchContentActivity.this,albumId);
                            mediaItem.setAlbumArt(albumArt);
                        }
                        cursor.close();
                    }
                    handler.sendEmptyMessage(10);
                }

            }
        }.start();
    }

}
