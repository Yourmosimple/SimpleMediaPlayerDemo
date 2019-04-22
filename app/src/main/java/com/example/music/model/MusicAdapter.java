package com.example.music.model;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mediaplayer_test.R;
import com.example.music.model.MusicItem;
import com.example.musicsqlite.dao.MusicDAO;

import java.util.List;
import java.util.Map;

/**
 * @Description:  ListView的视图管理类
 *
 * @Author: Pzh
 *
 * @Date: 19-4-8 上午10:52
 */
public class MusicAdapter extends BaseAdapter {


    private List<MusicItem> musicItem = null;
    private LayoutInflater mInflater;

    public MusicAdapter(Context context, List<MusicItem> musicItems) {

        this.mInflater = LayoutInflater.from(context);
        this.musicItem = musicItems;
    }

    @Override
    public int getCount() {

        return musicItem.size();
    }

    @Override
    public Object getItem(int position) {

        return musicItem.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    public final class ViewHolder {

        public TextView title;
        public TextView singer;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.music_adapter, null);

            viewHolder.title = convertView.findViewById(R.id.Title);
            viewHolder.singer = convertView.findViewById(R.id.Singer);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(musicItem.get(position).getTitle());
        viewHolder.singer.setText(musicItem.get(position).getArtist());


        return convertView;
    }

    /**

     * @Description: 获得 listView指定位置的组件信息 更改文本 添加标记

     * @Author: Pzh

     * @Date: 19-4-19 上午10:44

     * @Param: [index, listview]

     * @Return: void

     */
    public void updateRed(int index, ListView listview){

        int visibleFirstPosition = listview.getFirstVisiblePosition();
        int visibleLastPosition = listview.getLastVisiblePosition();

        if (index >= visibleFirstPosition && index <= visibleLastPosition){

            View view = listview.getChildAt(index - visibleFirstPosition);
            ViewHolder holder = (ViewHolder) view.getTag();
            String str = holder.title.getText().toString();
            holder.title.setText("正在播放..." + str);
            musicItem.get(index).setTitle("正在播放..." + str);

        } else {

            String str = musicItem.get(index).getTitle();
            musicItem.get(index).setTitle("正在播放..." + str);
        }

    }
}
