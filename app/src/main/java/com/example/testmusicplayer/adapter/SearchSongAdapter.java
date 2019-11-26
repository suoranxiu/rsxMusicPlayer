package com.example.testmusicplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.testmusicplayer.domain.MediaItem;
import com.example.testmusicplayer.utils.FilterListener;


import java.util.ArrayList;
import java.util.List;

public class SearchSongAdapter extends BaseAdapter implements Filterable {

    private List<MediaItem> mediaItemList;
    private Context context;
    private SearchFilter filter ;// 创建MyFilter对象
    private FilterListener listener;

    public SearchSongAdapter(List<MediaItem> mediaItemList, Context context) {
        this.mediaItemList = mediaItemList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public Filter getFilter() {
        // 如果MyFilter对象为空，那么重写创建一个
        if (filter == null) {
            filter = new SearchFilter(mediaItemList,listener);
        }
        return filter;
    }

    class SearchFilter extends Filter {

        private List<MediaItem> mediaItemList;//用于保存原始音乐文件的列表

        private com.example.testmusicplayer.utils.FilterListener listener;

        public SearchFilter(List<MediaItem> mediaItemList, com.example.testmusicplayer.utils.FilterListener listener) {
            this.mediaItemList = mediaItemList;
            this.listener = listener;
        }


        /**
         * 该方法返回过滤后的搜索数据
         * @param constraint
         * @return
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();// 创建FilterResults对象

            List<MediaItem> resultList = new ArrayList<>();// 创建集合保存过滤后的数据

            for(MediaItem s: mediaItemList){
                // 过滤规则的具体实现
                if(checkConstraint(s,constraint)){
                    // 规则匹配的话就往集合中添加该数据
                    resultList.add(s);
                }
            }

            results.values = resultList;
            results.count = resultList.size();

            return results;
        }

        /**
         * 该方法用来刷新用户界面，根据过滤后的数据重新展示列表
         * @param constraint
         * @param results
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // 获取过滤后的数据
            List<MediaItem> list = (List<MediaItem>) results.values;

            //如果接口对象不为空，那么调用接口中的方法获取过滤后的数据，具体的实现在new这个接口的时候重写的方法里执行
            if(listener != null){
                listener.getFilterData(list);
            }
            // 刷新数据源显示
//        notifyDataSetChanged();
        }


        /**
         * 该方法用于检查MediaItem对象中是否存在与constraint相匹配的内容
         * @param mediaItem
         * @param constraint
         * @return
         */
        public boolean checkConstraint(MediaItem mediaItem,CharSequence constraint){

            boolean checked = false;

            String fileName = mediaItem.getName();
            String mediaName = fileName.substring(0,fileName.lastIndexOf("."));
            String albumName = mediaItem.getAlbum();

            if(mediaName.trim().toLowerCase().contains(constraint.toString().trim().toLowerCase()) ||
                    albumName.trim().toLowerCase().contains(constraint.toString().trim().toLowerCase())){
                checked = true;
            }

            return checked;
        }

    }

}
