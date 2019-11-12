package com.example.testmusicplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.view.CircleImageView;

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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于加载图片， 并显示
 */
public class ImageLoader {

    private Map<String, Bitmap> cacheMap = new HashMap<>();//用于缓存Bitmap对象的容器

    private Context context;

    public ImageLoader(Context context) {
        this.context = context;
    }

    public void loadImage(String artistName, CircleImageView circleImageView) throws IOException {

//        circleImageView.setTag(artistName);//将图片的URL保存到视图身上

        Bitmap bitmap = getFromFirstCache(artistName);//根据图片的路径从一级缓存中取Bitmap对象

        if(bitmap !=null){
            circleImageView.setImageBitmap(bitmap);
            return;
        }

        bitmap = getFromSecondCache(artistName);
        if(bitmap != null){
            circleImageView.setImageBitmap(bitmap);
            cacheMap.put(artistName,bitmap);
            return;
        }

        getfromThirdCache(artistName,circleImageView);

    }


    /**
     * 从内存中读取与歌手名字对应的BitMap对象
     * @param imagePath
     * @return
     */
    public Bitmap getFromFirstCache(String imagePath){

        return cacheMap.get(imagePath);
    }


    /**
     * 根据artist的名字作为关键字去手机内部存储空间中当前app目录下的files目录下，
     * 寻找匹配的目录，并在以artist名字命名的目录下寻找第一张不为空的图像作为所选取的Bitmap对象
     * @param artistName
     * @return
     */
    public Bitmap getFromSecondCache(String artistName){

        String rootPath = context.getExternalFilesDir(null).getAbsolutePath();
        String artistFilePath = rootPath+"/"+artistName;
        File artistFile = new File(artistFilePath);
        File[] imgList = artistFile.listFiles();

        String filePath = null;

        //选取artist目录下图片列表中的第1张不为空的图片作为portrait bitmap对象
        for(File imgFile:imgList){
            if(imgFile.length() != 0 && !imgFile.isDirectory()){
                filePath = imgFile.getPath();
                break;
            }
        }

        return BitmapFactory.decodeFile(filePath);
    }


    public void getfromThirdCache(final String artistName, final CircleImageView imageView){//使用AsyncTask
        new AsyncTask<Void,Void,Bitmap>(){

            @Override
            protected void onPreExecute() {
                imageView.setImageResource(R.drawable.portrait_loading);

            }

            @Override
            protected Bitmap doInBackground(Void... voids) {

                String finalURL = "";
                String tempPath = "";
                String filesPath = context.getExternalFilesDir(null).getAbsolutePath();

                tempPath = filesPath;
                if(!tempPath.endsWith("/")){
                    tempPath = filesPath+"/";
                }
                tempPath = tempPath+artistName+"";
                File f = new File(tempPath);
                if(!f.exists()){
                    f.mkdirs();
                }

                //联网请求得到Bitmap对象
                Bitmap bitmap = null;
                Document document = null;
                try {
                    String url ="http://image.baidu.com/search/avatarjson?tn=resultjsonavatarnew&ie=utf-8&word="+artistName;

                    document = Jsoup.connect(url).data("query", "Java")//请求参数
                            .userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)")//设置urer-agent  get();
                            .timeout(5000)
                            .get();

                    String xmlSource = document.toString();
                    xmlSource = StringEscapeUtils.unescapeHtml3(xmlSource);

                    String reg = "objURL\":\"http://.+?\\.jpg";

                    Pattern pattern = Pattern.compile(reg);
                    Matcher m = pattern.matcher(xmlSource);

                    //根据url下载图片,放入二级缓存
                    int count = 0;
                    while (m.find()) {
                        if(count >= 3){
                            break;
                        }
                        finalURL = m.group().substring(9);
//                        Log.e(keyword,finalURL);
                        download(finalURL,tempPath);
//                    sop(" 下载成功");
                        count ++;
                    }

                    bitmap = getFromSecondCache(artistName);
                    //放进一级缓存
                    if(bitmap != null){
                        cacheMap.put(artistName,bitmap);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                delMultyFile(filesPath);
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if(bitmap == null){
                    imageView.setImageResource(R.drawable.artist_icon_press);
                }else {
                    imageView.setImageBitmap(bitmap);
                }
            }

            public void SaveImage(Bitmap bitmap,String imagePath) throws IOException {

                String filesPath = context.getExternalFilesDir(null).getAbsolutePath();
                String filename = imagePath.substring(imagePath.lastIndexOf("/")+1);

                String filePath = filesPath+"/"+filename;

                FileOutputStream fos = new FileOutputStream(filePath);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                System.out.println("Saving "+filename);
                fos.flush();
                fos.close();

            }


        }.execute();

    }

    //根据图片网络地址下载图片
    public void download(String url,String path) {
        File file = null;
        File dirFile = null;
        FileOutputStream fos = null;
        HttpURLConnection httpCon = null;
        URLConnection con = null;
        URL urlObj = null;
        InputStream in = null;
        byte[] size = new byte[1024];
        int num = 0;
        try {
            String downloadName = url.substring(url.lastIndexOf("/") + 1);
            dirFile = new File(path);
            if (!dirFile.exists() && path.length() > 0) {
                if (dirFile.mkdir()) {
                    Log.e("IO error", "creat document file \"" + path.substring(0, path.length() - 1) + "\" success...\n");
                }
            } else {
                file = new File(path + "/" + downloadName);
                fos = new FileOutputStream(file);
                if (url.startsWith("http")) {
                    urlObj = new URL(url);
                    con = urlObj.openConnection();
                    httpCon = (HttpURLConnection) con;
                    in = httpCon.getInputStream();
                    while ((num = in.read(size)) != -1) {
                        for (int i = 0; i < num; i++)
                            fos.write(size[i]);
                    }
                }
            }
        } catch (FileNotFoundException notFoundE) {
            Log.e("FileNotFoundException", "找不到该网络图片....");
        } catch (NullPointerException nullPointerE) {
            Log.e("NullPointerException", "找不到该网络图片....");
        } catch (IOException ioE) {
            Log.e("IOException", "产生IO异常.....");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public  void delMultyFile(String path){//删除所有空图
        File file = new File(path);
        if(!file.exists())
            throw new RuntimeException("File \""+path+"\" NotFound when excute the method of delMultyFile()....");
        File[] fileList = file.listFiles();
        File tempFile=null;
        for(File f : fileList){
            if(f.isDirectory()){
                delMultyFile(f.getAbsolutePath());
            }else{
                if(f.length() == 0){
                    f.delete();
                }
            }
        }
    }



}
