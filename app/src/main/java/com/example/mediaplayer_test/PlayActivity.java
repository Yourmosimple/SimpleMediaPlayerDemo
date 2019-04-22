package com.example.mediaplayer_test;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Widget.MediaplayerWidget;
import com.example.musicsqlite.dao.MusicDAO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**

 * @Description: MediaPlayer音乐播放界面

 * @Author: Pzh

 * @Date: 19-4-19 上午10:47

 */
public class PlayActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, ServiceConnection {

    MusicDAO musicDAO = new MusicDAO(this);
    private TextView tv_Title;
    private TextView tv_Singer;
    private TextView nowTime, totalTime;
    private Button pre_view, start_or_stop_view, next_view;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
    private static int position;
    private int status = 1;
    private SeekBar seekBar;
    private ImageView imageView;
    private MusicService.Binder binder = null;
    public FinishReceiver finishReceiver;
    private Messenger serviceMessenger;

     Handler handler = new Handler() {
         @Override
         public void handleMessage(Message msg) {
             super.handleMessage(msg);
             if (msg.what == 1) {
                 if (binder != null) {
                     seekBar.setProgress(binder.getProgress());
                     nowTime.setText(simpleDateFormat.format(binder.getProgress()));

                 }
             }
             Message message = Message.obtain();
             message.what = 1;
             handler.sendMessageDelayed(message, 500);
         }
     };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        findSetView();

        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);

        setView();

        finishReceiver = new FinishReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Utils.FINISH);
        filter.addAction(Utils.CONTROL_ACTION_FROM_W);
        filter.addAction("data_from_widget");
        filter.addAction("pop_action");
        registerReceiver(finishReceiver, filter);

        bindService(new Intent(this, MusicService.class), this, Context.BIND_AUTO_CREATE);

        handler.sendEmptyMessage(1);
    }

    public class FinishReceiver extends BroadcastReceiver{

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {

            String finish = intent.getStringExtra(Utils.FINISH);
            String action = intent.getStringExtra("action");
            int data_from_widget = intent.getIntExtra("position", -1);
            String pop_action = intent.getStringExtra("pop_action");

            if (finish != null){

                position = position + 1;
                setView();
            } else if (action != null){

                if (action.equals(Utils.NEXT_ACTION)){

                    start_or_stop_view.setBackgroundResource(R.mipmap.ic_pause);
                    status = 1;
                    position ++;
                    setView();
                } else if (action.equals(Utils.PRE_ACTION)){

                    start_or_stop_view.setBackgroundResource(R.mipmap.ic_pause);
                    status = 1;
                    position --;
                    setView();


                } else if (action.equals(Utils.START_OR_STOP)){

                    if (status == 0){

                        start_or_stop_view.setBackgroundResource(R.mipmap.ic_pause);
                        status = 1;
                    } else if (status == 1){

                        start_or_stop_view.setBackgroundResource(R.mipmap.ic_play);
                        status = 0;
                    }

                }

            } else if (data_from_widget != -1){

                position = data_from_widget;
                start_or_stop_view.setBackgroundResource(R.mipmap.ic_pause);
                status = 1;
                imageView.setImageBitmap(musicDAO.find(position).loadPicture(musicDAO.find(position).getPath()));
                totalTime.setText(simpleDateFormat.format(musicDAO.find(position).getDuration()));
                tv_Title.setText(musicDAO.find(position).getTitle());
                tv_Singer.setText(musicDAO.find(position).getArtist());
                seekBar.setMax((int) musicDAO.find(position).getDuration());
            } else if (pop_action != null){

                if (status == 0){

                    start_or_stop_view.setBackgroundResource(R.mipmap.ic_pause);
                    status = 1;
                } else if (status == 1){

                    start_or_stop_view.setBackgroundResource(R.mipmap.ic_play);
                    status = 0;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(Utils.CONTROL_ACTION);
        Intent intent1 = new Intent("data_for_note");
        Intent intent2 = new Intent("position_for_list");

        switch (v.getId()){

            case R.id.Next_Button:

                intent.putExtra("action", Utils.NEXT_ACTION);
                start_or_stop_view.setBackgroundResource(R.mipmap.ic_pause);
                status = 1;
                position ++;
                setView();
                intent1.putExtra("position", position);
                intent2.putExtra("position", position);
                sendBroadcast(intent1);
                sendBroadcast(intent);
                sendBroadcast(intent2);
                break;

            case R.id.pre_Button:

                intent.putExtra("action", Utils.PRE_ACTION);
                start_or_stop_view.setBackgroundResource(R.mipmap.ic_pause);
                status = 1;
                position --;
                setView();
                intent1.putExtra("position", position);
                intent2.putExtra("position", position);
                sendBroadcast(intent1);
                sendBroadcast(intent);
                sendBroadcast(intent2);
                break;

            case R.id.play_or_stop:

                intent.putExtra("action", Utils.START_OR_STOP);
                if (status == 0){

                    start_or_stop_view.setBackgroundResource(R.mipmap.ic_pause);
                    status = 1;
                } else if (status == 1){

                    start_or_stop_view.setBackgroundResource(R.mipmap.ic_play);
                    status = 0;
                }
                sendBroadcast(intent);
                break;

            default:
                break;
        }

    }

    /**

     * @Description: 找到控件 并设置点击事件

     * @Author: Pzh

     * @Date: 19-4-19 上午10:53

     * @Param: []

     * @Return: void

     */
    public void findSetView(){

        imageView = findViewById(R.id.image);
        tv_Singer = findViewById(R.id.Singer);
        tv_Title = findViewById(R.id.Title);
        nowTime =  findViewById(R.id.nowTime);
        totalTime = findViewById(R.id.totalTime);
        pre_view = findViewById(R.id.pre_Button);
        next_view = findViewById(R.id.Next_Button);
        start_or_stop_view = findViewById(R.id.play_or_stop);
        seekBar = findViewById(R.id.seekBar);
        imageView.setOnClickListener(this);
        tv_Singer.setOnClickListener(this);
        tv_Title.setOnClickListener(this);
        nowTime.setOnClickListener(this);
        totalTime.setOnClickListener(this);
        pre_view.setOnClickListener(this);
        next_view.setOnClickListener(this);
        start_or_stop_view.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
    }

    /**

     * @Description: 更新 音乐播放界面UI

     * @Author: Pzh

     * @Date: 19-4-19 上午10:54

     * @Param: []

     * @Return: void

     */
    public void setView(){

        Intent intent = new Intent(Utils.POSITION_FOR_WIDGET);

        if (position == 0) {

            intent.putExtra("position", musicDAO.getCount());
            System.out.println("我要发送"+ musicDAO.find(musicDAO.getCount()).getTitle());
            sendBroadcast(intent);
            imageView.setAlpha(0.5f);
            imageView.setImageBitmap(musicDAO.find(musicDAO.getCount()).loadPicture(musicDAO.find(musicDAO.getCount()).getPath()));
            totalTime.setText(simpleDateFormat.format(musicDAO.find(musicDAO.getCount()).getDuration()));
            tv_Title.setText(musicDAO.find(musicDAO.getCount()).getTitle());
            tv_Singer.setText(musicDAO.find(musicDAO.getCount()).getArtist());
            seekBar.setMax((int) musicDAO.find(musicDAO.getCount()).getDuration());
            position = musicDAO.getCount();
        } else if (position > musicDAO.getCount()) {

            intent.putExtra("position", 1);
            System.out.println("我要发送"+ musicDAO.find(1).getTitle());
            sendBroadcast(intent);
            imageView.setAlpha(0.7f);
            imageView.setImageBitmap(musicDAO.find(1).loadPicture(musicDAO.find(1).getPath()));
            totalTime.setText(simpleDateFormat.format(musicDAO.find(1).getDuration()));
            tv_Title.setText(musicDAO.find(1).getTitle());
            tv_Singer.setText(musicDAO.find(1).getArtist());
            seekBar.setMax((int) musicDAO.find(1).getDuration());
            position = 1;
        } else {

            intent.putExtra("position", position);
            System.out.println("我要发送"+ musicDAO.find(position).getTitle());
            sendBroadcast(intent);
            imageView.setAlpha(0.5f);
            imageView.setImageBitmap(musicDAO.find(position).loadPicture(musicDAO.find(position).getPath()));
            totalTime.setText(simpleDateFormat.format(musicDAO.find(position).getDuration()));
            tv_Title.setText(musicDAO.find(position).getTitle());
            tv_Singer.setText(musicDAO.find(position).getArtist());
            seekBar.setMax((int) musicDAO.find(position).getDuration());
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        nowTime.setText(simpleDateFormat.format(progress));
        if (fromUser){
            Intent intent = new Intent(Utils.SEEKBAR);
            intent.putExtra("progress", progress);
            sendBroadcast(intent);
            Toast.makeText(this, "" + simpleDateFormat.format(seekBar.getProgress()), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        binder = (MusicService.Binder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

        serviceMessenger = null;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        unregisterReceiver(finishReceiver);
        unbindService(this);
        handler.removeCallbacksAndMessages(null);
    }


}
