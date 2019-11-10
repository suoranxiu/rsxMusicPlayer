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
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        getPortraitOnline(3);

                    }
                }.start();
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
    public void getPortraitOnline(int maxPics){
        String finalURL = "";
        String tempPath = "";
        String filesPath = context.getExternalFilesDir(null).getAbsolutePath();

        for(Artist artist: artistList){
            String keyword = artist.getName();
            tempPath = filesPath;
            if(!tempPath.endsWith("\\")){
                tempPath = filesPath+"\\";
            }
            tempPath = tempPath+keyword+"\\";
            File f = new File(tempPath);
            if(!f.exists()){
                f.mkdirs();
            }
            Document document = null;
            try {
                String url ="http://image.baidu.com/search/avatarjson?tn=resultjsonavatarnew&ie=utf-8&word="+keyword;
                //"https://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&fm=result&fr=&sf=1&fmq=1573357913208_R&pv=&ic=&nc=1&z=&hd=&latest=&copyright=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&sid=&word=歌手名字"
                document = Jsoup.connect(url).data("query", "Java")//请求参数
                        .userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)")//设置urer-agent  get();
                        .timeout(5000)
                        .get();
                String xmlSource = document.toString();
                xmlSource = StringEscapeUtils.unescapeHtml3(xmlSource);
//                sop(xmlSource);
                String reg = "objURL\":\"http://.+?\\.jpg";
                Pattern pattern = Pattern.compile(reg);
                Matcher m = pattern.matcher(xmlSource);
                int count = 0;
                while (m.find()) {
                    if(count >= maxPics){
                        break;
                    }
                    finalURL = m.group().substring(9);
//                    sop(keyword+":"+finalURL);
                    download(finalURL,tempPath);
//                    sop(" 下载成功");
                    count ++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
//        sop("下载完毕");
        delMultyFile(filesPath);//删除所有空图
//        sop("已经删除所有空图");

    }

    public static void delMultyFile(String path){//删除所有空图
        File file = new File(path);
        if(!file.exists())
            throw new RuntimeException("File \""+path+"\" NotFound when excute the method of delMultyFile()....");
        File[] fileList = file.listFiles();
        File tempFile=null;
        for(File f : fileList){
            if(f.isDirectory()){
                delMultyFile(f.getAbsolutePath());
            }else{
            }
        }
    }

    //根据图片网络地址下载图片
    public static void download(String url,String path){
        //path = path.substring(0,path.length()-2);
        File file= null;
        File dirFile=null;
        FileOutputStream fos=null;
        HttpURLConnection httpCon = null;
        URLConnection con = null;
        URL urlObj = null;
        InputStream in =null;
        byte[] size = new byte[1024];
        int num=0;
        try {
            String downloadName= url.substring(url.lastIndexOf("/")+1);
            dirFile = new File(path);
            if(!dirFile.exists() && path.length()>0){
                if(dirFile.mkdir()){
                    Log.e("IO error","creat document file \""+path.substring(0,path.length()-1)+"\" success...\n");
                }
            }else{
                file = new File(path+downloadName);
                fos = new FileOutputStream(file);
                if(url.startsWith("http")){
                    urlObj = new URL(url);
                    con = urlObj.openConnection();
                    httpCon =(HttpURLConnection) con;
                    in = httpCon.getInputStream();
                    while((num=in.read(size)) != -1){
                        for(int i=0;i<num;i++)
                            fos.write(size[i]);
                    }
                }
            }
        }catch (FileNotFoundException notFoundE) {
            Log.e("FileNotFoundException","找不到该网络图片....");
        }catch(NullPointerException nullPointerE){
            Log.e("NullPointerException","找不到该网络图片....");
        }catch(IOException ioE){
            Log.e("IOException","产生IO异常.....");
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
