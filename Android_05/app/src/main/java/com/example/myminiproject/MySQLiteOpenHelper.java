package com.example.myminiproject ;

import android.content.Context ;
import android.database.sqlite.SQLiteDatabase ;
import android.database.sqlite.SQLiteDatabase.CursorFactory ;
import android.database.sqlite.SQLiteOpenHelper ;

public class MySQLiteOpenHelper extends SQLiteOpenHelper
{
    public MySQLiteOpenHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, version) ;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String sql = "create table datainfo" +
                "(_id integer primary key autoincrement, " +
                "title string, content string, " +
                "lat double, lng double, imgview blob) ;" ;
        db.execSQL(sql) ;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String sql = "drop table if exists datainfo ;" ;
        db.execSQL(sql) ;

        onCreate(db) ;
    }
}