package com.example.testmusicplayer.utils;

import android.text.Editable;

//文本改变接口监听
public interface OnTextChangedListener {

    void beforeTextChanged(CharSequence s, int start, int count, int after);

    void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter);

    void afterTextChanged(Editable s);

    void clearSearchingList();
}
