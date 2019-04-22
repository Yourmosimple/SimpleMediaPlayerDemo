package com.example.mediaplayer_test;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.music.model.MusicAdapter;
import com.example.music.model.MusicItem;
import com.example.musicsqlite.dao.MusicDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 异步任务处理类
 *
 * @Author: Pzh
 *
 * @Date: 19-4-17 下午4:26
 */
public class MyAsyncTask extends AsyncTask<Void, Integer, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    public List<MusicItem> musicList;
    MusicDAO musicDAO;
    MusicAdapter adapter;
    ListView listView;

    public MyAsyncTask(Context context, List<MusicItem> musicList, MusicDAO musicDAO, ListView listView) {

        this.context = context;
        this.listView = listView;
        this.musicList = musicList;
        this.musicDAO = musicDAO;

    }

    @Override
    protected Void doInBackground(Void...voids) {

        musicList = getBaseData();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onPostExecute(Void aVoids) {
        super.onPostExecute(aVoids);

        Intent intent = new Intent(context, MusicService.class);
        context.startService(intent);

        Intent intent1 = new Intent(context, NotificationService.class);
        context.startService(intent1);

        adapter = new MusicAdapter(context, musicList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //通过跳转发送给播放界面
                Intent intent = new Intent(context, PlayActivity.class);
                intent.putExtra("position", position + 1);

                //广播发送给服务
                Intent intent1 = new Intent(Utils.POSITION);
                intent1.putExtra("position", position + 1);

                //广播发送给插件
                Intent intent2 = new Intent(Utils.POSITION_FOR_WIDGET);
                intent2.putExtra("position", position + 1);

                //广播发送给note服务
                Intent intent3 = new Intent("data_for_note");
                intent3.putExtra("position", position + 1);

                //广播发送回ListView
                Intent intent4 = new Intent("position_for_list");
                intent4.putExtra("position", position + 1);

                context.sendBroadcast(intent3);
                context.sendBroadcast(intent1);
                context.sendBroadcast(intent2);
                context.sendBroadcast(intent4);
                context.startActivity(intent);
            }
        });
        progressDialog.dismiss();
    }

    /**

     * @Description: 获取 SD卡音乐文件并写入数据库

     * @Author: Pzh

     * @Date: 19-4-19 上午11:06

     * @Param: []

     * @Return: java.util.List<com.example.music.model.MusicItem>

     */
    public List<MusicItem> getBaseData() {

        musicDAO.delete();
        musicList = new ArrayList<>();
        musicDAO = new MusicDAO(context);

        //先确定搜索对象是外部存储卡的音乐文件
        Cursor cursor = context.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);


        assert cursor != null;

        //对搜索到的音乐文件信息分类
        int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int pathIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int durationIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int imagePathIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

        while (cursor.moveToNext()) {

            String Path = cursor.getString(pathIndex);
            String Title = cursor.getString(titleIndex);
            String Artist = cursor.getString(artistIndex);
            long Duration = cursor.getLong(durationIndex);
            String imagePath = cursor.getString(imagePathIndex);
            MusicItem song = new MusicItem(Title, Artist, Path, Duration, imagePath);

            musicDAO.add(song);
            musicList.add(song);

        }
        cursor.close();
        return musicList;
    }
}
