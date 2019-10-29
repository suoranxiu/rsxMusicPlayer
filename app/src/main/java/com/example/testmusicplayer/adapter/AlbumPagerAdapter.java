package com.example.testmusicplayer.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.domain.Album;

import java.util.ArrayList;

public class AlbumPagerAdapter extends BaseAdapter {

    private ArrayList<Album> albumList;
    private Context context;
    private int sumCount;

    public AlbumPagerAdapter(ArrayList<Album> albumList, Context context) {
        this.albumList = albumList;
        this.context = context;
    }

    static class ViewHolder {
        LinearLayout albumItem1;
        ImageView iv_albumaArt1;
        TextView tv_albumName1;
        TextView tv_artistName1;

        LinearLayout albumItem2;
        ImageView iv_albumaArt2;
        TextView tv_albumName2;
        TextView tv_artistName2;
    }

    @Override
    public int getCount() {
        int count = albumList.size();

        if (count % 2 == 0) {
            sumCount = count / 2; // 如果是双数直接减半
        } else {
            sumCount = (int) Math.floor((double) count / 2) + 1;
        }
        return sumCount;
    }

    @Override
    public Object getItem(int position) {
        return albumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(context,R.layout.item_album_pager, null);

            viewHolder.albumItem1 = (LinearLayout)convertView.findViewById(R.id.ly_album_item1);
            viewHolder.iv_albumaArt1 = (ImageView) convertView.findViewById(R.id.iv_album_icon_albumPager1);
            viewHolder.tv_albumName1 = (TextView) convertView.findViewById(R.id.tv_name_albumPager1);
            viewHolder.tv_artistName1 = (TextView) convertView.findViewById(R.id.tv_artist_albumPager1);

            viewHolder.albumItem2 = (LinearLayout)convertView.findViewById(R.id.ly_album_item2);
            viewHolder.iv_albumaArt2 = (ImageView) convertView.findViewById(R.id.iv_album_icon_albumPager2);
            viewHolder.tv_albumName2 = (TextView) convertView.findViewById(R.id.tv_name_albumPager2);
            viewHolder.tv_artistName2 = (TextView) convertView.findViewById(R.id.tv_artist_albumPager2);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Album album1 = albumList.get(position*2);
        viewHolder.iv_albumaArt1.setImageBitmap(album1.getAlbumArt());
        viewHolder.tv_albumName1.setText(album1.getAlbumName());
        viewHolder.tv_artistName1.setText(album1.getArtist());

        if (position * 2 + 1 == albumList.size()) {
            viewHolder.albumItem2.setVisibility(View.INVISIBLE); // 如果是单数的话，那么最后一个item，右侧内容为空
        } else {
            viewHolder.albumItem2.setVisibility(View.VISIBLE); // 必须进行设置，负责存在复用holder的时候，会出现右侧的出现留白，跟最后一个一样，这个也是我写这篇文章最想锁的
            Album album2 = albumList.get(position*2+1);
            viewHolder.iv_albumaArt2.setImageBitmap(album2.getAlbumArt());
            viewHolder.tv_albumName2.setText(album2.getAlbumName());
            viewHolder.tv_artistName2.setText(album2.getArtist());
        }

        return convertView;
    }
}
