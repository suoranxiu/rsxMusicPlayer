package com.example.testmusicplayer.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class RSBlur {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static Bitmap rsBlur(Context context, Bitmap source, int radius){
        Bitmap inputBmp = source;
        //(1)初始化一个RenderScript Context：RenderScript 上下文环境通过create(Context)方法来创建，它保证RenderScript的使用并且提供一个控制后续所有RenderScript对象
        RenderScript renderScript =  RenderScript.create(context);
        //(2)通过Script至少创建一个Allocation：一个Allocation是提供存储大量可变数据的RenderScript 对象。
        final Allocation input = Allocation.createFromBitmap(renderScript,inputBmp);
        final Allocation output = Allocation.createTyped(renderScript,input.getType());
        //(3)创建ScriptIntrinsic：它内置了RenderScript 的一些通用操作，如高斯模糊、扭曲变换、图像混合等等
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        //(4)填充数据到Allocations：除了使用方法createFromBitmap创建的Allocation外，其它的第一次创建时都是填充的空数据。
        scriptIntrinsicBlur.setInput(input);
        //(5)设置模糊半径 (0---25)
        scriptIntrinsicBlur.setRadius(radius);
        //(6)启动内核，调用方法处理：调用forEach 方法模糊处理。
        scriptIntrinsicBlur.forEach(output);
        //(7)从Allocation 中拷贝数据：为了能在Java层访问Allocation的数据，用Allocation其中一个copy方法来拷贝数据。
        output.copyTo(inputBmp);
        //(8)销毁RenderScript对象
        renderScript.destroy();
        return inputBmp;
    }

}
