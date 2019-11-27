package com.example.testmusicplayer.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.utils.OnTextChangedListener;

import butterknife.ButterKnife;
import butterknife.OnFocusChange;

public class SearchEditText extends AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {

    private Drawable delete_words;
    private boolean hasFocus; //控件是否有焦点

    public SearchEditText(Context context) {
        super(context);
        init(context, null);
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attr){

        delete_words = getCompoundDrawables()[2];
        if(delete_words == null){
            delete_words =  getResources().getDrawable(R.drawable.cancel_icon_64);
        }
        delete_words.setBounds(0,0,50,50);

        Drawable searchDrawable = getCompoundDrawables()[0];
        searchDrawable.setBounds(0,0,50,50);
        setCompoundDrawables(searchDrawable,null,null,null);

        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                //根据触摸的位置判断是否点击 右侧图标
                boolean isTouchRight = (event.getX() > (getWidth() - getTotalPaddingRight())) &&
                        (event.getX() < (getWidth() - getPaddingRight()));
                //LogUtil.d("isTouchRight： " + isTouchRight);
                if (isTouchRight) {
                    setText("");
                    if(mOnTextChangedListener != null){
                        mOnTextChangedListener.clearSearchingList();
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
        if (hasFocus) {
            //焦点存在，而且有输入值，显示右侧删除图标
            setDrawableRightVisible(getText().length() > 0);
        } else {
            //没有焦点，隐藏删除图标
            setDrawableRightVisible(false);
            clearFocus();
        }

    }
    private void setDrawableRightVisible(boolean visible) {
        Drawable drawableRight = visible ? delete_words : null;
        //getCompoundDrawables()可以获得一个{DrawableLeft, DrawableTop, DrawableRiht, DrawableBottom}的数组。
        //getCompoundDrawables()[2]表示获取EditText的DrawableRight
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], drawableRight, getCompoundDrawables()[3]);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if(mOnTextChangedListener != null){
            mOnTextChangedListener.beforeTextChanged(s,start,count,after);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (hasFocus) {
            setDrawableRightVisible(s.length() > 0);
        }
        if(mOnTextChangedListener != null){
            mOnTextChangedListener.onTextChanged(s,start,before,count);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(mOnTextChangedListener != null){
            mOnTextChangedListener.afterTextChanged(s);
        }
    }


    private OnTextChangedListener mOnTextChangedListener;

    public void setOnTextChangedListener(OnTextChangedListener onTextChangedListener) {
        this.mOnTextChangedListener = onTextChangedListener;
    }


}
