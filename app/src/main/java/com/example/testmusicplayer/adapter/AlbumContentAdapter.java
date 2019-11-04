package com.example.testmusicplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.testmusicplayer.R;
import com.example.testmusicplayer.domain.MediaItem;

import java.util.Map;

public class AlbumContentAdapter extends BaseAdapter {

    private Context context;
    private Map<Integer, MediaItem> AlbumMap;

    public AlbumContentAdapter(Context context, Map<Integer, MediaItem> albumMap) {
        this.context = context;
        AlbumMap = albumMap;
    }

    @Override
    public int getCount() {
        return AlbumMap.size();
    }

    @Override
    public Object getItem(int position) {
        return AlbumMap.get(position);
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
            convertView = View.inflate(context, R.layout.item_albumcontent,null);
            viewHolder.tv_id_song_abContent = (TextView)convertView.findViewById(R.id.tv_id_song_abContent);
            viewHolder.tv_songName_abContent = (TextView)convertView.findViewById(R.id.tv_songName_abContent);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int id = position + 1;
        String songName = AlbumMap.get(position).getName();
        songName = songName.substring(songName.lastIndexOf("-")+2,songName.lastIndexOf("."));;
        Log.e("id",""+id);
        Log.e("songName",""+songName);
        viewHolder.tv_songName_abContent.setText(songName);
        viewHolder.tv_id_song_abContent.setText("#"+id);

        return convertView;
    }

    static class ViewHolder{
        TextView tv_id_song_abContent;
        TextView tv_songName_abContent;
    }
}
