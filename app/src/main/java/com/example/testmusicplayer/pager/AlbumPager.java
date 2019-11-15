package com.example.testmusicplayer.pager;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import android.view.View;

import android.widget.ListView;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.adapter.AlbumPagerAdapter;
import com.example.testmusicplayer.base.BasePager;
import com.example.testmusicplayer.domain.Album;
import com.example.testmusicplayer.domain.MediaItem;
import com.example.testmusicplayer.utils.AlbumArt;
import com.example.testmusicplayer.utils.Grant;

import java.util.ArrayList;


/**
 * 本地音乐所述专辑列表的页面
 */
public class AlbumPager extends BasePager {

    private ListView listView;
    private ArrayList<Album> albumList;

    public AlbumPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.album_pager,null);
        listView = (ListView)view.findViewById(R.id.lv_ap_album_list);
        Log.e("AlbumPager","AlbumPager is initialized");

        return view;
    }

    @Override
    public void initDate() {
        Log.e("AlbumPager","AlbumPager data is initialized");
        getLocalData();
        setAdapter();

    }

    public void getLocalData() {
        albumList = new ArrayList<>();

        ContentResolver resolver = context.getContentResolver();
        if (Grant.isGrantExternalRW((Activity) context)) {
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

            Cursor cursor = resolver.query(uri, objs, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    MediaItem mediaItem = new MediaItem();

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
                    AlbumArt albumArt = new AlbumArt(context, albumId);
                    mediaItem.setAlbumArt(albumArt);

                    //查看是否专辑和歌手已经存在
                    int albumIndex = checkAlbum(album,artist);
                    if(albumIndex < 0){
                        //若不存在
                        Album albumItem = new Album(album,artist);
                        albumItem.putSong(mediaItem);
                        albumList.add(albumItem);
                    }else {
                        //若存在
                        Album albumItem = albumList.get(albumIndex);
                        albumItem.putSong(mediaItem);
                    }

                }
                cursor.close();

            }
        }

        Log.e("num of AlbumList"," "+albumList.size());

    }
    private void setAdapter(){
        if(albumList!=null && albumList.size()!=0){
            AlbumPagerAdapter albumPagerAdapter = new AlbumPagerAdapter(albumList,context);
            listView.setAdapter(albumPagerAdapter);
        }
    }


    public int checkAlbum(String albumName,String artist){
        int existed = -1;

        for(int i = 0;i<albumList.size();i++){
            if(albumName.equals(albumList.get(i).getAlbumName()) && artist.equals(albumList.get(i).getArtist())){
                existed =  i;
                break;
            }
        }
        return existed;
    }
}
