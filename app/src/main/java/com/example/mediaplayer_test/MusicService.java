package com.example.mediaplayer_test;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.service.carrier.CarrierService;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.musicsqlite.dao.MusicDAO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

/**

 * @Description: 音乐服务类

 * @Author: Pzh

 * @Date: 19-4-19 上午11:14

 * @Param:

 * @Return:

 */
public class MusicService extends Service {

    public MediaPlayer mediaPlayer = new MediaPlayer();
    MusicDAO musicDAO = new MusicDAO(this);
    private int position;
    private int nowPlay;
    MyReceiver myReceiver;
    private Callback callback = null;
    private boolean running = false;
    private Handler handler;
    String action;

    public MusicService() {

    }

    @Override
    public IBinder onBind(Intent intent) {

        return new Binder();
    }

    public class Binder extends android.os.Binder{

        public int getProgress(){
            return mediaPlayer.getCurrentPosition();
        }

        public MusicService getProgressService(){
            return MusicService.this;
        }

        public MediaPlayer getMusicPlayer(){
            return mediaPlayer;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() {

        super.onCreate();
        myReceiver = new MyReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Utils.POSITION);
        filter.addAction(Utils.CONTROL_ACTION);
        filter.addAction("data_from_widget");
        filter.addAction("pop_action");
        filter.addAction(Utils.SEEKBAR);
        registerReceiver(myReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        running = false;

        unregisterReceiver(myReceiver);

    }

    public class MyReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {

            nowPlay = intent.getIntExtra("position", 0);

            String pop = intent.getStringExtra("pop_action");

            action = intent.getStringExtra("action");

            int progress = intent.getIntExtra("progress", -1);

           if (nowPlay !=  0 ){

               position = nowPlay;
               prepareMusic(position);

           } else if (pop != null){

               if (mediaPlayer.isPlaying()){
                   mediaPlayer.pause();
               } else {
                   mediaPlayer.start();
               }
           }else if (action != null){

               switch (Objects.requireNonNull(intent.getAction())){

                   case Utils.CONTROL_ACTION:
                       if (action.equals(Utils.NEXT_ACTION)){
                           next();
                       } else if (action.equals(Utils.PRE_ACTION)){
                           pre();
                       } else if (action.equals(Utils.START_OR_STOP)){
                           if (mediaPlayer.isPlaying()){
                               mediaPlayer.pause();
                           } else {
                               mediaPlayer.start();
                           }
                       }
                       break;

                   default:
                       break;
               }
           } else if (progress != -1){
               mediaPlayer.seekTo(progress);
           }
        }
    }

    private void prepareMusic(final int position) {

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicDAO.find(position).getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer arg0) {

                    Intent intent = new Intent(Utils.FINISH);
                    intent.putExtra("finish", Utils.FINISH);
                    sendBroadcast(intent);
                    next();//如果当前歌曲播放完毕,自动播放下一首.

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void next(){
        if (position == musicDAO.getCount()){
            prepareMusic(1);

            position = 1;
        } else {
            prepareMusic(++ position);

        }
    }

    private void pre(){
        if (position == 1){
            prepareMusic(musicDAO.getCount());

            position = musicDAO.getCount();
        } else {
            prepareMusic(--position);
//            Toast.makeText(this,musicDAO.find(position).getTitle() + "开始播放啦", Toast.LENGTH_SHORT).show();
        }
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public static interface Callback{
        void onProgressChange(int progress);
    }


}
