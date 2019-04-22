package com.example.musicsqlite.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.music.model.MusicItem;

/**
 * @Description: 数据库的增、删、插操作工具
 *
 * @Author: Pzh
 *
 * @Date: 19-4-9 上午9:11
 */
public class MusicDAO {

    private DbOpenHelper helper;
    private SQLiteDatabase db;

    public MusicDAO(Context context){
        helper = new DbOpenHelper(context);
    }


    /**

     * @Description: 将一个具体的音乐类对象信息写入数据库

     * @Author: Pzh

     * @Date: 19-4-19 上午10:31

     * @Param: [music]

     * @Return: void

     */
    public void add(MusicItem music){

        db = helper.getWritableDatabase();
        db.execSQL("insert into my_music (title, artist, path, duration, imagePath) values (?, ?, ?, ?, ?)",
                new Object[]{music.getTitle(), music.getArtist(), music.getPath(), music.getDuration(), music.getImagePath()});
    }

    /**

     * @Description: 根据传入的id （代码中会传入 position）查找到相应的歌曲类对象

     * @Author: Pzh

     * @Date: 19-4-19 上午10:30

     * @Param: [id]

     * @Return: com.example.music.model.MusicItem

     */
    public MusicItem find(int id){

        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from my_music where id = ?", new String[]{
                String.valueOf(id)
        });
        if (cursor.moveToNext()){
            return new MusicItem(cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("artist")),
                    cursor.getString(cursor.getColumnIndex("path")),
                    cursor.getLong(cursor.getColumnIndex("duration")),
                    cursor.getString(cursor.getColumnIndex("imagePath")));
        }
        return null;
    }

    /**

     * @Description: 获取数据库中的音乐数量

     * @Author: Pzh

     * @Date: 19-4-19 上午10:28

     * @Param: []

     * @Return: int

     */
    public int getCount(){
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(id) from my_music", null);
        if (cursor.moveToNext()){
            return cursor.getInt(0);
        }
        return 0;
    }

    /**

     * @Description: 删除表，并且将自增主键设为 0

     * @Author: Pzh

     * @Date: 19-4-19 上午10:28

     * @Param: []

     * @Return: void

     */
    public void delete(){
        db = helper.getWritableDatabase();
        db.delete("my_music", null, null);
        db.execSQL("update sqlite_sequence set seq=0 where name='my_music'" );
    }
}
