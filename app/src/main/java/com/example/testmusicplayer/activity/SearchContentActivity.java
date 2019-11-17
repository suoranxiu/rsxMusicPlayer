package com.example.testmusicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;

import android.view.View;

import android.widget.ListView;
import android.widget.TextView;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.view.SearchEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SearchContentActivity extends AppCompatActivity {

    @BindView(R.id.et_search)
    SearchEditText et_search;

    @BindView(R.id.tv_cancel_searching)
    TextView tv_cancel_searching;

    @BindView(R.id.lv_searching_songs)
    ListView lv_searching_songs;

    @BindView(R.id.tv_search_nodata)
    TextView tv_search_nodata;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_content);
        ButterKnife.bind(this);

    }



    @OnClick({R.id.tv_cancel_searching})
    public void onClick(View v){
        if(v == tv_cancel_searching){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
