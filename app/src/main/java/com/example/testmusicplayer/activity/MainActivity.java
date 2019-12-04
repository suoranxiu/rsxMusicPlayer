package com.example.testmusicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import com.example.testmusicplayer.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Handler handler = new Handler();

    //手写单例模式
    private boolean isStartMain = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler.postDelayed(new Runnable() {
             @Override
             public void run() {
                 //在主线程中
                 startMain2Activity();
//                 Log.e(TAG,"current_thread==="+Thread.currentThread().getName());
             }
         },2000);
    }

    /**
     * 跳转到主页面，并关闭当前页面
     */
    private void startMain2Activity() {
        if(!isStartMain){
            isStartMain = true;
            Intent intent  = new Intent(this, Main2Activity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG,"onTouchEvent=== Action"+event.getAction());
        startMain2Activity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);//移除handler，防止退出此activity后，还会出现handler处理的新的activity
        super.onDestroy();
    }
}
