package com.example.testmusicplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.testmusicplayer.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于加载图片， 并显示
 */
public class ImageLoader {

    private Map<String, Bitmap> cacheMap = new HashMap<>();//用于缓存Bitmap对象的容器

    private Context context;

    public ImageLoader(Context context) {
        this.context = context;
    }

    public void loadImage(String imagePath, ImageView imageView) throws IOException {

        imageView.setTag(imagePath);//将图片的URL保存到视图身上

        Bitmap bitmap = getFromFirstCache(imagePath);//根据图片的路径从一级缓存中取Bitmap对象
        if(bitmap !=null){
            imageView.setImageBitmap(bitmap);
            return;
        }

        bitmap = getFromSecondCache(imagePath);
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
            cacheMap.put(imagePath,bitmap);
            return;
        }

        getfromThirdCache(imagePath,imageView);

    }


    public Bitmap getFromFirstCache(String imagePath){

        return cacheMap.get(imagePath);
    }
    public Bitmap getFromSecondCache(String imagePath){

        String filesPath = context.getExternalFilesDir(null).getAbsolutePath();
        String filename = imagePath.substring(imagePath.lastIndexOf("/")+1);

        String filePath = filesPath+"/"+filename;

        return BitmapFactory.decodeFile(filePath);
    }
    public void getfromThirdCache(final String imagePath, final ImageView imageView){//使用AsyncTask
        new AsyncTask<Void,Void,Bitmap>(){

            private String newImagePath;
            @Override
            protected void onPreExecute() {
                imageView.setImageResource(R.drawable.portrait_loading);
                newImagePath = (String)imageView.getTag();
            }

            @Override
            protected Bitmap doInBackground(Void... voids) {
                //联网请求得到Bitmap对象
                Bitmap bitmap = null;
                try {
                    if(imagePath != newImagePath){
                        return null;
                    }

                    URL url = new URL(imagePath);

                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if(responseCode == 200){
                        InputStream is = connection.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                        is.close();
                    }
                    connection.disconnect();

                    //放进一级和二级缓存
                    if(bitmap != null){
                        cacheMap.put(imagePath,bitmap);
                        SaveImage(bitmap,imagePath);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if(bitmap == null){
                    imageView.setImageResource(R.drawable.artist_icon_press);
                }else {
                    String newImagePath = (String)imageView.getTag();
                    if(imagePath != newImagePath){
                        return;
                    }
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




}
