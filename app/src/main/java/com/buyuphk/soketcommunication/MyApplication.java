package com.buyuphk.soketcommunication;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.buyuphk.soketcommunication.db.MySQLiteOpenDatabase;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

/**
 * Copyright (C), buyuphk物流中转站
 * author: JianfeiMa
 * email: majianfei93@163.com
 * revised: 2019-12-21 11:48
 * motto: 勇于向未知领域探索
 */
public class MyApplication extends Application {
    public static MyApplication instance;
    public MySQLiteOpenDatabase mySQLiteOpenDatabase;
    public int keyboardHeight = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        File file = Environment.getExternalStorageDirectory();
        String path = file.getAbsolutePath() + "/SocketCommunication";
        File fileProject = new File(path);
        if (!fileProject.exists()) {
            boolean result = fileProject.mkdir();
            Log.d("debug", "创建APP目录返回的结果:" + result);
        }
        CrashReport.initCrashReport(getApplicationContext(), "e142601d63", true);
        instance = this;
        mySQLiteOpenDatabase = new MySQLiteOpenDatabase(this, "socketCommunication", null, 1);
    }

    public MySQLiteOpenDatabase getMySQLiteOpenDatabase() {
        return mySQLiteOpenDatabase;
    }
}
