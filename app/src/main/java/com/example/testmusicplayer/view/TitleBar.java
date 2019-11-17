package com.example.testmusicplayer.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.activity.SearchContentActivity;

/**
 * 自定义标题栏
 */
public class TitleBar extends LinearLayout implements View.OnClickListener {

    private TextView tv_search;
    private ImageView iv_music_player;
    private Context context;
    /**
     * 在代码中实例化该类，使用这个构造器
     * @param context
     */
    public TitleBar(Context context){
        this(context,null);
    }

    /**
     * 在布局文件使用该类时，通过这个构造器实例化
     * @param context
     * @param attributeSet
     */
    public TitleBar(Context context, AttributeSet attributeSet){
        this(context,attributeSet,0);
    }

    /**
     * 需要设计样式的时候。使用该构造方法
     * @param context
     * @param attributeSet
     * @param defStyleAttr
     */
    public TitleBar(Context context, AttributeSet attributeSet,int defStyleAttr){
        super(context,attributeSet,defStyleAttr);
        this.context = context;
    }

    /**
     * 布局文件加载完成时，回调此方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //得到孩子的实例
        tv_search = (TextView) getChildAt(0);
        iv_music_player = (ImageView) getChildAt(1);

        //设置点击事件
        tv_search.setOnClickListener(this);
        iv_music_player.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_search){
//            Toast.makeText(context,"search music",0).show();
            Intent intent = new Intent(context,SearchContentActivity.class);
            context.startActivity(intent);
        }else if(v.getId() == R.id.iv_music_player){
//            Toast.makeText(context,"music player",0).show();
        }
    }
}
