package com.example.testmusicplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.domain.MediaItem;
import com.example.testmusicplayer.pager.ListPager;

import java.util.ArrayList;

public class ListPagerAdapter extends BaseAdapter {

    private ArrayList<MediaItem> mediaItems;
    private Context context;

    public ListPagerAdapter(ArrayList<MediaItem> mediaItems, Context context) {
        this.mediaItems = mediaItems;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_list_pager,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_album_icon_listPager = (ImageView) convertView.findViewById(R.id.iv_album_icon_listPager);
            viewHolder.tv_name_listPager = (TextView)convertView.findViewById(R.id.tv_name_listPager);
            viewHolder.tv_artist_listPager = (TextView)convertView.findViewById(R.id.tv_artist_listPager);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MediaItem mediaItem = mediaItems.get(position);
        String fileName = mediaItem.getName();
        String songName = fileName.substring(fileName.lastIndexOf("-")+2,fileName.lastIndexOf("."));
        viewHolder.tv_name_listPager.setText(songName);
        viewHolder.tv_artist_listPager.setText(mediaItem.getArtist());
        viewHolder.iv_album_icon_listPager.setImageBitmap(mediaItem.getAlbumArt());
        return convertView;
    }

    static class ViewHolder{
        ImageView iv_album_icon_listPager;
        TextView tv_name_listPager;
        TextView tv_artist_listPager;
    }
}

