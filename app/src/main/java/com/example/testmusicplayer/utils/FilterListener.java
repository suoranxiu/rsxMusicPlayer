package com.example.testmusicplayer.utils;

import com.example.testmusicplayer.domain.MediaItem;

import java.util.List;

public interface FilterListener {

    void getFilterData(List<MediaItem> list);// 获取过滤后的数据
}
