package com.example.testmusicplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.domain.Artist;
import com.example.testmusicplayer.view.CircleImageView;

import java.util.List;

public class ArtistPagerAdapter extends BaseAdapter {

    public ArtistPagerAdapter(Context context, List<Artist> artistList) {
        this.context = context;
        this.artistList = artistList;
    }

    private Context context;
    private List<Artist> artistList;


    @Override
    public int getCount() {
        return artistList.size();
    }

    @Override
    public Object getItem(int position) {
        return artistList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_artist_pager,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_portrait_artist_pager = (CircleImageView) convertView.findViewById(R.id.iv_portrait_artist_pager);
            viewHolder.tv_name_artistPager = (TextView)convertView.findViewById(R.id.tv_name_artistPager);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Artist artist = artistList.get(position);
        viewHolder.tv_name_artistPager.setText(artist.getName());
        return convertView;
    }

    static class ViewHolder{

        CircleImageView iv_portrait_artist_pager;
        TextView tv_name_artistPager;

    }
}
