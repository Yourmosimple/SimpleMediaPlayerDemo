package com.example.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.arch.lifecycle.ViewModelProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.mediaplayer_test.MusicService;
import com.example.mediaplayer_test.PlayActivity;
import com.example.mediaplayer_test.R;
import com.example.mediaplayer_test.Utils;
import com.example.musicsqlite.dao.MusicDAO;

//https://blog.csdn.net/ruingman/article/details/51356135




//需要给服务、播放界面发送动作（需要和播放界面自己发送的动作有区别）
//需要接受来自播放界面的position
/**
 * Implementation of App Widg et functionality.
 */
public class MediaplayerWidget extends AppWidgetProvider {

    private static final String TAG = "MediaplayerWidget";

    private static int position = 1;
    String buttonId;
    MusicDAO musicDAO;
    private int test = 1;

    /**
     * 到达指定的更新时间或者当用户向桌面添加AppWidget时被调用
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        if (musicDAO == null) {

            musicDAO = new MusicDAO(context);
        }

        sendUpdate(context, AppWidgetManager.getInstance(context) , "null", "null", true);
    }

    /**
     * AppWidget的实例第一次被创建时调用
     */
    @Override
    public void onEnabled(Context context) {
        // 输入创建第一个窗口小部件时的相关功能

        if (musicDAO == null){
            musicDAO = new MusicDAO(context);
        }
        sendUpdate(context, AppWidgetManager.getInstance(context) , "null", "null", true);
    }


    /**
     * 最后一个appWidget被删除时调用
     */
    @Override
    public void onDisabled(Context context) {
        // 输入禁用最后一个窗口小部件的相关功能
    }

    /**
     * 删除一个AppWidget时调用
     */

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * 接受广播事件
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.mediaplayer_widget);

        if (musicDAO == null){
            musicDAO = new MusicDAO(context);
        }

        if (intent.getAction().equals(Utils.POSITION_FOR_WIDGET)){

            position = intent.getIntExtra("position", 0);
            if (position != 0){

                sendUpdate(context, AppWidgetManager.getInstance(context), musicDAO.find(position).getTitle(), musicDAO.find(position).getArtist(), true);

            } else {

                sendUpdate(context, AppWidgetManager.getInstance(context), "欢迎使用", "欢迎使用", true);
            }
        } else

        if (intent.hasCategory("Click")){
            Uri uri = intent.getData();
            buttonId = uri.getSchemeSpecificPart();
            if (buttonId == null){
                System.out.println("无消息");
            } else {
                switch (buttonId) {
                    case "11":
                    sendAction(context, "pop");
                    Log.w(TAG, "发送了一个广播");
                        break;
                    case "22":


                    -- position;
                    if (position == 0){
                        position = musicDAO.getCount();
                        sendUpdate(context, AppWidgetManager.getInstance(context), musicDAO.find(position).getTitle(), musicDAO.find(position).getArtist(), true);
                    } else {
                        sendUpdate(context, AppWidgetManager.getInstance(context), musicDAO.find(position).getTitle(), musicDAO.find(position).getArtist(), true);
                    }
                        sendPosition(context, position);

                        break;
                    case "00":
                    ++ position;
                    if (position > musicDAO.getCount() + 1){
                        position = 1;
                        sendUpdate(context, AppWidgetManager.getInstance(context), musicDAO.find(position).getTitle(), musicDAO.find(position).getArtist(), true);
                    } else {
                        sendUpdate(context, AppWidgetManager.getInstance(context), musicDAO.find(position).getTitle(), musicDAO.find(position).getArtist(), true);
                    }
                        sendPosition(context, position);

                        break;

                    default:

                }
            }
        }
    }


    private void sendUpdate(Context context, AppWidgetManager appWidgetManager, String title, String singer, Boolean isPlay){

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.mediaplayer_widget);
        remoteViews.setOnClickPendingIntent(R.id.Next_Button, getPendingIntent(context, "00"));



        remoteViews.setOnClickPendingIntent(R.id.play_or_stop, getPendingIntent(context, "11"));
        remoteViews.setOnClickPendingIntent(R.id.pre_Button, getPendingIntent(context, "22"));
        if (!title.equals("") && !singer.equals("")){
            remoteViews.setTextViewText(R.id.Title, title);
            remoteViews.setTextViewText(R.id.Singer, singer);
        }

        ComponentName componentName = new ComponentName(context, MediaplayerWidget.class);
        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }

    private PendingIntent getPendingIntent(Context context, String buttonId){

        Intent intent = new Intent("Click");
        intent.setClass(context, MediaplayerWidget.class);
        intent.addCategory("Click");
        intent.setData(Uri.parse(buttonId));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        return pendingIntent;
    }

    private void sendPosition(Context context, int position){

        Intent positionIntent = new Intent("data_from_widget");
        positionIntent.putExtra("position", position);
        context.sendBroadcast(positionIntent);
    }

    private void sendAction(Context context, String action){
        Intent actionIntent = new Intent("pop_action");
        actionIntent.putExtra("pop_action", action);
        context.sendBroadcast(actionIntent);
    }

}




