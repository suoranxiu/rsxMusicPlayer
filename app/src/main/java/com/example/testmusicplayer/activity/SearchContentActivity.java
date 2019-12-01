package com.example.testmusicplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.testmusicplayer.IMusicPlayerService;
import com.example.testmusicplayer.R;
import com.example.testmusicplayer.adapter.SearchSongAdapter;
import com.example.testmusicplayer.domain.MediaItem;
import com.example.testmusicplayer.service.MusicPlayerService;
import com.example.testmusicplayer.utils.AlbumArt;
import com.example.testmusicplayer.utils.Grant;
import com.example.testmusicplayer.utils.OnTextChangedListener;
import com.example.testmusicplayer.utils.RecordsSqliteHelper;
import com.example.testmusicplayer.utils.SearchSqliteHelper;
import com.example.testmusicplayer.view.ListViewForScrollView;
import com.example.testmusicplayer.view.SearchEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;


public class SearchContentActivity extends AppCompatActivity {


    private ArrayList<MediaItem> mediaItems;
    private int songPosition;
    SimpleCursorAdapter simpleCursorAdapter;
    SearchSqliteHelper searchSqliteHelper;
    RecordsSqliteHelper recordsSqliteHelper;
    SQLiteDatabase db_search;
    SQLiteDatabase db_records;
    Cursor cursor;

    private IMusicPlayerService iService;//服务的代理类，通过它可以调用服务类方法
    private boolean onBinded = false;
    private ServiceConnection con = new ServiceConnection() {
        /**
         * 当连接成功的时候回调此方法
         * @param name
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            Log.e("Tag","onServiceConnected!");
            iService = IMusicPlayerService.Stub.asInterface(iBinder);
            if(iService != null){
                try {
                    iService.openAudio(songPosition);
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

    @BindView(R.id.ly_search_history)
    LinearLayout ly_search_history;

    @BindView(R.id.lv_search_history)
    ListViewForScrollView lv_search_history;

    @BindView(R.id.tv_clear_records)
    TextView tv_clear_records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_content);
        ButterKnife.bind(this);
        getLocalData();
        initLocalDB();
    }

    @OnItemClick(R.id.lv_search_history)
    public void OnItemClick0(AdapterView<?> parent, View view, int position, long id){
        String ketWord = (String)((TextView)view.findViewById(R.id.tv_search_record_word)).getText();
        et_search.setText(ketWord);
    }
    @OnItemClick(R.id.lv_searching_songs)
    public void OnItemClick1(AdapterView<?> parent, View view, int position, long id){

        songPosition = (int)view.getTag(R.id.tag_listIndex);
        Log.e("click"," "+position);
        if(!onBinded){
            bindStartService(songPosition);
        }else {
            try {
                iService.openAudio(songPosition);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void bindStartService(int songPosition) {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.putExtra("isLocalList",true);
        intent.putExtra("songPosition",songPosition);
        intent.setAction(MusicPlayerService.OPENAUDIO);
        bindService(intent,con, Context.BIND_AUTO_CREATE);
        startService(intent);//不至于实例化多个服务
        onBinded = true;
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
        ly_search_history.setVisibility(View.GONE);

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
                if(! et_search.getText().toString().trim().equals("")){
                    //保存搜索记录
                    insertRecords(et_search.getText().toString().trim());
                }
            }

            @Override
            public void clearSearchingList() {
                lv_searching_songs.setVisibility(View.GONE);
                ly_search_history.setVisibility(View.VISIBLE);
                cursor = recordsSqliteHelper.getReadableDatabase().rawQuery("select * from table_records order by _id desc", null);
                refreshRecordsListView();
                Log.e("searching et","clearSearchingList");
            }
        });
    }


    @OnClick({R.id.tv_cancel_searching,R.id.tv_clear_records})
    public void onClick(View v){
        if(v == tv_cancel_searching){
            finish();
        }else if(v == tv_clear_records){
            deleteAllReocords();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db_records != null) {
            db_records.close();
        }
        if (db_search != null) {
            db_search.close();
        }
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

    private void initLocalDB() {

        searchSqliteHelper = new SearchSqliteHelper(this);
        recordsSqliteHelper = new RecordsSqliteHelper(this);
        //初始化本地数据库
        initRecords();
        //尝试从保存查询纪录的数据库中获取历史纪录并显示
        cursor = recordsSqliteHelper.getReadableDatabase().rawQuery("select * from table_records order by _id desc", null);
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.item_search_history, cursor
                , new String[]{"keyword"}, new int[]{R.id.tv_search_record_word}
                , CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lv_search_history.setAdapter(simpleCursorAdapter);
    }

    /**
     * 初始化本地数据库数据
     */
    private void initRecords() {
        deleteData();
        db_search = searchSqliteHelper.getWritableDatabase();
        for (int i = 0; i < 20; i++) {
            db_search.execSQL("insert into table_search values(null,?)",
                    new String[]{"keyword" + i + 10});
        }
        db_search.close();
    }

    /**
     * 避免重复初始化数据
     */
    private void deleteData() {

        db_search = searchSqliteHelper.getWritableDatabase();
        db_search.execSQL("delete from table_search");
        db_search.close();

    }

    /**
     * 保存搜索记录
     * @param str
     */
    private void insertRecords(String str) {
        if (!hasDataRecords(str)) {
            db_records = recordsSqliteHelper.getWritableDatabase();
            db_records.execSQL("insert into table_records values(null,?)", new String[]{str});
            db_records.close();
        }
    }

    /**
     * 检查是否存在此搜索记录
     * @param str
     * @return
     */
    private boolean hasDataRecords(String str) {
        cursor = recordsSqliteHelper.getReadableDatabase()
                .rawQuery("select _id,keyword from table_records where keyword = ?"
                        , new String[]{str});

        return cursor.moveToNext();
    }

    /**
     * 在数据库中搜索
     */
    private void queryData(String searchData) {
        cursor = searchSqliteHelper.getReadableDatabase()
                .rawQuery("select * from table_search where keyword like '%" + searchData + "%'", null);
        refreshRecordsListView();
    }

    /**
     * 删除历史记录
     */
    private void deleteAllReocords(){
        db_records = recordsSqliteHelper.getWritableDatabase();
        db_records.execSQL("delete from table_records");

        cursor = recordsSqliteHelper.getReadableDatabase().rawQuery("select * from table_records", null);
        if (et_search.getText().toString().equals("")) {
            refreshRecordsListView();
        }
    }

    private void refreshRecordsListView() {
        simpleCursorAdapter.notifyDataSetChanged();
        simpleCursorAdapter.swapCursor(cursor);
    }

}

