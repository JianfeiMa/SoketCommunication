package com.buyuphk.soketcommunication.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.buyuphk.soketcommunication.MyApplication;
import com.buyuphk.soketcommunication.R;
import com.buyuphk.soketcommunication.bean.MessageEntity;
import com.buyuphk.soketcommunication.db.MySQLiteOpenDatabase;
import com.buyuphk.soketcommunication.thread.MyThread;
import com.mysun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

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
        IntentFilter intentFilter = new IntentFilter("customer_service_message");
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

    protected void news(String fromWho, String message, int messageType) {
        MyApplication myApplication = (MyApplication) getApplication();
        MySQLiteOpenDatabase mySQLiteOpenDatabase = myApplication.getMySQLiteOpenDatabase();
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenDatabase.getReadableDatabase();
        String[] selectionArgs = new String[1];
        selectionArgs[0] = fromWho;
        Cursor cursor = sqLiteDatabase.rawQuery("select * from user where user_id = ?", selectionArgs);
        boolean isExist = false;
        if (cursor.moveToNext()) {
            isExist = true;
        }
        Log.d("debug", "是否已经存在用户->" + isExist);
        if (!isExist) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id", fromWho);
            long insertResult = sqLiteDatabase.insert("user", null, contentValues);
            Log.d("debug", "插入一个新用户" + insertResult);
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", fromWho);
        contentValues.put("speaker", fromWho);
        if (messageType == 0) {
            contentValues.put("message", message);
            contentValues.put("message_type", 0);
        } else {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            try {
                // Base64解码
                byte[] b = base64Decoder.decodeBuffer(message);
                for (int i = 0; i < b.length; ++i) {
                    if (b[i] < 0) {// 调整异常数据
                        b[i] += 256;
                    }
                }
                long currentTimeMillis = System.currentTimeMillis();
                File file = Environment.getExternalStorageDirectory();
                String imagePath = file.getAbsolutePath() + "/SocketCommunication/" + currentTimeMillis + ".png";
                Log.d("debug", "path" + imagePath);
                OutputStream out = new FileOutputStream(imagePath);
                out.write(b);
                out.flush();
                out.close();
                contentValues.put("message", imagePath);
                contentValues.put("message_type", 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        contentValues.put("create_date_time", System.currentTimeMillis());
        long insertMessageResult = sqLiteDatabase.insert("message", null, contentValues);
        Log.d("debug", "插入消息返回->" + insertMessageResult);
        cursor.close();
        Intent intent = new Intent("update_message");
        intent.putExtra("message", message);
        intent.putExtra("fromWho", fromWho);
        intent.putExtra("messageType", messageType);
        sendBroadcast(intent);

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.news_arrived);
        mediaPlayer.start();
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(300);

//        android.app.Notification notification = new android.app.Notification.Builder(this)
//                .setContentText(message)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .build();

        android.app.NotificationManager manager = (android.app.NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        java.util.Random random = new java.util.Random(100);

        Notification notification = new NotificationCompat.Builder(this, "YonC")
                .setContentTitle("有新通知")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.parseColor("#FFff0fff"))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .build();
        // 在安卓8.0以上需要使用NotificationChannel（通知渠道）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //三个参数，第一个就是notification对象对应的id
            //第二个是name，是用户收到通知时会看到的
            //第三个是重要性优先级
            NotificationChannel channel = new NotificationChannel("YonC", "YonC", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            manager.notify(random.nextInt(), notification);
        } else {
            manager.notify(random.nextInt(), notification);
        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            byte defaultType = 0;
            byte type = intent.getByteExtra("type", defaultType);
            String fromWho = intent.getStringExtra("fromWho");
            byte[] dataForMessage = intent.getByteArrayExtra("message");
            String message = new String(dataForMessage);
            int messageType = intent.getIntExtra("messageType", 0);
            news(fromWho, message, messageType);
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
