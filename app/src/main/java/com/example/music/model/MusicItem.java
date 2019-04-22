package com.example.music.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.MenuItem;

import java.io.Serializable;

/**
 * @Description: 建立音乐文件类 继承 Serializable接口, 支持文件数据广播、 activity携带跳转
 *
 * @Author: Pzh
 *
 * @Date: 19-4-8 上午8:51
 */
public class MusicItem implements Serializable {

    private String title;
    private String artist;
    private long duration;
    private String path;
    private String imagePath;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public long getDuration() {
        return duration;
    }

    public String getPath() {
        return path;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public MusicItem(String title, String artist, String path, long duration, String imagePath) {

        this.artist = artist;
        this.duration = duration;
        this.path = path;
        this.title = title;
        this.imagePath = imagePath;
    }


    /**

     * @Description: 通过得到音乐文件地址信息 获得音乐文件的专辑图片

     * @Author: Pzh

     * @Date: 19-4-19 上午10:39

     * @Param: [path]

     * @Return: android.graphics.Bitmap

     */
    public Bitmap loadPicture(String path){

        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        byte[] picture = mediaMetadataRetriever.getEmbeddedPicture();
        Bitmap bitmap= BitmapFactory.decodeByteArray(picture,0,picture.length);
        return bitmap;
    }
}
