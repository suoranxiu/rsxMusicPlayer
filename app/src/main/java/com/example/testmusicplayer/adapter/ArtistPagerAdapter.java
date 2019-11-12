package com.example.testmusicplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.domain.Artist;
import com.example.testmusicplayer.utils.ImageLoader;
import com.example.testmusicplayer.view.CircleImageView;

import java.io.IOException;
import java.util.List;

public class ArtistPagerAdapter extends BaseAdapter {

    private ImageLoader imageLoader;
    private Context context;
    private List<Artist> artistList;

    public ArtistPagerAdapter(Context context, List<Artist> artistList) {
        this.context = context;
        this.artistList = artistList;
        this.imageLoader = new ImageLoader(context);
    }

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

        String artistName = artist.getName();

        viewHolder.tv_name_artistPager.setText(artistName);

        try {

            imageLoader.loadImage(artistName,viewHolder.iv_portrait_artist_pager);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    static class ViewHolder{

        CircleImageView iv_portrait_artist_pager;
        TextView tv_name_artistPager;

    }
}
