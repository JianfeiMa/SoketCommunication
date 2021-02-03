package com.buyuphk.soketcommunication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buyuphk.soketcommunication.service.MyService;
//import com.tencent.bugly.crashreport.CrashReport;

import cn.yunzhisheng.tts.offline.basic.ITTSControl;
import cn.yunzhisheng.tts.offline.basic.TTSFactory;

public class NettyListenerActivity extends AppCompatActivity {
    private TextView textView;
    private ProgressBar progressBar;
    private TextView textViewAlive;
    private MyBroadcastReceiver myBroadcastReceiver;
    private ITTSControl ittsControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netty_listener);
        textView = findViewById(R.id.activity_main_text_view);
        progressBar = findViewById(R.id.activity_main_progress_bar);
        textViewAlive = findViewById(R.id.activity_main_alive);

        ittsControl = TTSFactory.createTTSControl(this, "jianfei");
        ittsControl.setStreamType(AudioManager.STREAM_RING);
        ittsControl.setVoiceSpeed(2.5f);
        ittsControl.setVoicePitch(1.1f);
        ittsControl.init();

        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter("jianfei");
        registerReceiver(myBroadcastReceiver, intentFilter);

       // MyThread myThread = new MyThread(this);
       // myThread.start();
        //CrashReport.testJavaCrash();
        startService(new Intent(this, MyService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        ittsControl.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ittsControl.release();
        unregisterReceiver(myBroadcastReceiver);
    }

//    @Override
//    public void onClick(View v) {
//        Log.d("debug", "onclick");
//        socketChannel.writeAndFlush("1");
//    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            byte defaultType = 0;
            byte type = intent.getByteExtra("type", defaultType);
            if (type == 0) {
                boolean isActive = intent.getBooleanExtra("isActive", false);
                if (isActive) {
                    textView.setText("在线");
                    progressBar.setVisibility(View.GONE);
                    textView.setBackgroundResource(R.drawable.my_shape_positive);
                } else {
                    textView.setText("离线");
                    textView.setBackgroundResource(R.drawable.my_shape_negative);
                }
            } else if (type == 1) {
                byte[] data = intent.getByteArrayExtra("message");
                String message0 = new String(data);
                Log.d("debug", "message0:" + message0);
                //String message = intent.getStringExtra("message");
                ittsControl.play(message0);
            } else if (type == 2) {
                String times = textViewAlive.getText().toString();
                int iTimes = Integer.valueOf(times);
                iTimes ++;
                textViewAlive.setText(String.valueOf(iTimes));

                textView.setText("在线");
                progressBar.setVisibility(View.GONE);
                textView.setBackgroundResource(R.drawable.my_shape_positive);
            }
        }
    }
}
