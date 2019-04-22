package com.example.mediaplayer_test;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.musicsqlite.dao.MusicDAO;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**

 * @Description: 消息推送服务

 * @Author: Pzh

 * @Date: 上午10:58

 * @Param:

 * @Return:

 */
public class NotificationService extends Service {

    public int position = 0;
    public MusicDAO musicDAO = new MusicDAO(this);
    private PendingIntent messagePendingIntent;
    private Notification messageNotification = null;
    private NotificationManager messageNotificationManager = null;
    public NotificationService() {

    }

    @Override
    public void onCreate() {

        IntentFilter filter = new IntentFilter();
        filter.addAction("data_from_widget");
        filter.addAction("data_for_note");
        registerReceiver(receiver, filter);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //初始化
        messageNotification = new Notification();
        messageNotification.icon = R.mipmap.music;
        messageNotification.tickerText = "新消息";
        messageNotification.defaults = Notification.DEFAULT_SOUND;
        messageNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 点击查看
        Intent messageIntent = new Intent(getApplicationContext(), PlayActivity.class);
        messagePendingIntent = PendingIntent.getActivity(this, 0,
                messageIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {

            if (musicDAO == null){

                musicDAO = new MusicDAO(context);
            }

            position = intent.getIntExtra("position", -1);

            if (position == 0){

                Log.e("推送", "无推送");
            } else {

                Intent intent1 = new Intent(getApplicationContext(), PlayActivity.class);
                intent1.putExtra("position", position);
                messageNotification = new Notification.Builder(context)
                        .setContentTitle("New mail from MediaPlayer")
                        .setContentText(musicDAO.find(position).getTitle() + "开始播放啦")
                        .setSmallIcon(R.mipmap.music)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentIntent(PendingIntent.getActivity(context,0, intent1, FLAG_UPDATE_CURRENT))
                        .build();

                // 通知栏消息
                int messageNotificationID = 1;
                messageNotificationManager.notify(messageNotificationID, messageNotification);
            }
        }
    };



}
