package com.buyuphk.soketcommunication.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.buyuphk.soketcommunication.R;
import com.buyuphk.soketcommunication.thread.MyThread;

/**
 * Copyright (C), buyuphk物流中转站
 * author: JianfeiMa
 * email: majianfei93@163.com
 * revised: 2020-04-21 21:12
 * motto: 勇于向未知领域探索
 */
public class MyService extends Service {
    private static final String TAG = MyService.class.getSimpleName();
    private TextToSpeech textToSpeech;
    private MyBroadcastReceiver myBroadcastReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "服务启动");
        textToSpeech = new TextToSpeech(this, null);
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter("voice");
        registerReceiver(myBroadcastReceiver, intentFilter);
        MyThread myThread = new MyThread(this);
        myThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "服务器销毁");
        unregisterReceiver(myBroadcastReceiver);
        textToSpeech.shutdown();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            byte defaultType = 0;
            byte type = intent.getByteExtra("type", defaultType);
            if (type == 0) {
                boolean isActive = intent.getBooleanExtra("isActive", false);
                if (isActive) {

                } else {

                }
            } else if (type == 1) {
                byte[] data = intent.getByteArrayExtra("message");
                String message0 = new String(data);
                Log.d("debug", "message0:" + message0);
                //textToSpeech.speak(message0, TextToSpeech.QUEUE_FLUSH, null);
            } else if (type == 2) {

            }
        }
    }
}
