package com.example.testmusicplayer.base;

import android.content.Context;
import android.view.View;

/***
 * 基类，公共类
 *
 * 构造方法的时候，视图创建，initView（）,子类强制实现该方法
 * initDate（）,初始化子页面的数据
 */
public abstract class BasePager {

    public final Context context;
    public View rootView;
    public boolean isInitData;

    public BasePager(Context context) {
        this.context = context;
        rootView = initView();
    }

    /**
     * 强制子类去实现,实现特定的效果
     * @return
     */
    public abstract View initView();

    /**
     * 当子页面需要初始化数据时，需要重写该方法
     */
    public abstract void initDate();
}
