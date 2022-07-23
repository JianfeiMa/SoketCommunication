package com.buyuphk.soketcommunication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Copyright (C), buyuphk物流中转站
 * author: JianfeiMa
 * email: majianfei93@163.com
 * revised: 2020-07-22 15:08
 * motto: 勇于向未知领域探索
 */
public class MySQLiteOpenDatabase extends SQLiteOpenHelper {
    public MySQLiteOpenDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user(id INTEGER PRIMARY KEY AUTOINCREMENT,user_id VARCHAR(500))");
        db.execSQL("create table message (id integer primary key autoincrement, user_id varchar(200), speaker VARCHAR(30), message varchar(500), message_type integer, is_read tinyint default 0, create_date_time timestamp default(datetime('now', 'localtime')))");
        db.execSQL("create table log(id INTEGER PRIMARY KEY AUTOINCREMENT, log_content text, heart_time VARCHAR(60))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
