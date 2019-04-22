package com.example.mediaplayer_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.music.model.MusicAdapter;
import com.example.music.model.MusicItem;
import com.example.musicsqlite.dao.MusicDAO;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    public List<MusicItem> musicList;
    MusicDAO musicDAO = new MusicDAO(this);
    MusicAdapter adapter;
    MyAsyncTask myAsyncTask;
    ListView listView;
    public int nowposition = -1;

    public BroadcastReceiver listChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("receiver" , " i get ---------" + nowposition);
            nowposition = intent.getIntExtra("position", -1);
            Log.e("receiver" , " i get " + nowposition);

        }
    };

    //从磁盘获取音乐信息, 每获取一个Music类型的数据就向数据库中写入一条信息
    //这里数据库建立成功，需要在onCreate中调用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new MusicAdapter(this, musicList);

        listView = findViewById(R.id.list_view);

        IntentFilter filter = new IntentFilter();
        filter.addAction("position_for_list");
        filter.addAction("data_from_widget");
        registerReceiver(listChangeReceiver, filter);

        myAsyncTask = new MyAsyncTask(this, musicList, musicDAO, listView);
        myAsyncTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(listChangeReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("pause", "--------------");
        changeList();
        Log.e("pause", "i change it");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        changeList();
    }

    public void changeList(){
        musicDAO.delete();
        musicList = myAsyncTask.getBaseData();
        adapter = new MusicAdapter(this, musicList);
        listView.setAdapter(adapter);

        if (nowposition >= 0 && nowposition <= musicList.size() + 1){
            adapter.updateRed(nowposition - 1, listView);
        }
    }

}