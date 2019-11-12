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
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.adapter.ArtistPagerAdapter;
import com.example.testmusicplayer.base.BasePager;
import com.example.testmusicplayer.domain.Album;
import com.example.testmusicplayer.domain.Artist;
import com.example.testmusicplayer.domain.MediaItem;
import com.example.testmusicplayer.utils.AlbumArt;
import com.example.testmusicplayer.utils.Grant;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 本地音乐家的列表的页面
 */
public class ArtistPager extends BasePager {

    private ListView lv_ap_artist_list;

    private ArrayList<Artist> artistList;
    private ArrayList<Album> albumList;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(artistList!=null && artistList.size()!=0){
                ArtistPagerAdapter artistPagerAdapter = new ArtistPagerAdapter(context,artistList);
                lv_ap_artist_list.setAdapter(artistPagerAdapter);
            }
        }
    };

    public ArtistPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        Log.e("ArtistPager","ArtistPager is initialized");
        View view = View.inflate(context, R.layout.artist_pager,null);
        lv_ap_artist_list = (ListView)view.findViewById(R.id.lv_ap_artist_list);
        return view;
    }

    @Override
    public void initDate() {
        Log.e("ArtistPager","ArtistPager data is initialized");
        getLocalData();

    }

    private void getLocalData() {

        artistList = new ArrayList<>();
        albumList = new ArrayList<>();

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

                            String artistName = cursor.getString(3);
                            mediaItem.setArtist(artistName);

                            String album = cursor.getString(4);
                            mediaItem.setAlbum(album);


                            String name = cursor.getString(0);
                            mediaItem.setName(name);

                            long duration = cursor.getLong(1);
                            mediaItem.setDuration(duration);

                            long size = cursor.getLong(2);
                            mediaItem.setSize(size);


                            String data = cursor.getString(5);
                            mediaItem.setData(data);

                            int albumId = cursor.getInt(6);
                            AlbumArt albumArt = new AlbumArt(context,albumId);
                            mediaItem.setAlbumArt(albumArt);


                            //查看是否专辑和歌手已经存在
                            int albumIndex = checkAlbum(album,artistName);
                            int artistIndex = existArtist(artistName);
                            if(albumIndex < 0){
                                //若专辑不存在
                                Album albumItem = new Album(album,artistName);
                                albumItem.putSong(mediaItem);
                                albumList.add(albumItem);
                                if (artistIndex <0){
                                    //若歌手不存在
                                    Artist artist = new Artist(artistName);
                                    artist.putAlbum(albumItem);
                                    artistList.add(artist);
                                }else {
                                    //若歌手存在
                                    Artist artist = artistList.get(artistIndex);
                                    artist.putAlbum(albumItem);
                                }
                            }else {
                                //若专辑存在
                                Album albumItem = albumList.get(albumIndex);
                                albumItem.putSong(mediaItem);

                                //则歌手一定存在
                                if(artistIndex >0){
                                    Artist artist = artistList.get(artistIndex);
                                    int abId = artist.checkAlbum(albumItem);
                                    if(abId >0){
                                        artist.getAlbumMap().get(abId).putSong(mediaItem);
                                    }
                                }
                            }
                        }
                        cursor.close();
                    }
                    handler.sendEmptyMessage(10);
                }

            }
        }.start();
    }

    public int existArtist(String artist){

        for(int i=0; i<artistList.size(); i++){
            if(artistList.get(i).getName() == artist){
                return i;
            }
        }
        return -1;
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
