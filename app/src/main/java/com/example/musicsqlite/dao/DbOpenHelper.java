package com.example.musicsqlite.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @Description: 数据库建立、升级
 *
 * @Author: Pzh
 *
 * @Date: 19-4-9 上午9:01
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DB_NAME = "music.db";

    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table my_music (" +
                "id integer  primary key autoincrement," +
                "title text," +
                "artist text, " +
                "path text, " +
                "duration long, " +
                "imagePath text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
