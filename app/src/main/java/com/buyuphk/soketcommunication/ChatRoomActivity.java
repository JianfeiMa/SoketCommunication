package com.buyuphk.soketcommunication;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buyuphk.soketcommunication.adapter.MessageAdapter;
import com.buyuphk.soketcommunication.bean.MessageEntity;
import com.buyuphk.soketcommunication.db.MySQLiteOpenDatabase;
import com.buyuphk.soketcommunication.rxretrofit.NetDataLoader;
import com.buyuphk.soketcommunication.rxretrofit.responseresult.Result;
import com.buyuphk.soketcommunication.service.MyService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import com.mysun.misc.BASE64Decoder;
import com.mysun.misc.BASE64Encoder;

/**
 * Copyright (C), buyuphk物流中转站
 * author: JianfeiMa
 * email: majianfei93@163.com
 * revised: 2020-07-18 10:07
 * motto: 勇于向未知领域探索
 */
public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener, KeyboardChangeListener.KeyBoardListener {
    private static final int REQUEST_CODE_SYSTEM_ALBUM = 999;
    private List<MessageEntity> msgList = null;

    private ConstraintLayout constraintLayout;
    private LinearLayout bodyLayout;

    private EditText editText;
    private RecyclerView recyclerView;
    private MyBroadcastReceiver myBroadcastReceiver;
    private MessageAdapter messageAdapter;
    private String mImagePath = null;
    private KeyboardChangeListener keyboardChangeListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_chat_room);
        keyboardChangeListener = new KeyboardChangeListener(this);
        keyboardChangeListener.setKeyBoardListener(this);
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter("customer_service_message");
        registerReceiver(myBroadcastReceiver, intentFilter);
        constraintLayout = findViewById(R.id.layout_main);
        bodyLayout = findViewById(R.id.layout_body);
        editText = findViewById(R.id.activity_chart_room_edit_text);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                return false;
            }
        });
        Button button = findViewById(R.id.activity_chart_room_button);
        button.setOnClickListener(this);
        recyclerView = findViewById(R.id.recycler_view);
        TextView textViewTopBar = findViewById(R.id.top_bar);
        textViewTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPictureSelected();
            }
        });
        init();
    }

    private void init() {
        msgList = readMessageList();
        recyclerView.setHasFixedSize(true);
        messageAdapter = new MessageAdapter(this, msgList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdapter);
        scrollToBottom();
        /** 在服务中开始建立netty的socket链接 */
        startService(new Intent(this, MyService.class));
    }

    private void scrollToBottom() {
        recyclerView.scrollToPosition(msgList.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    public void onPictureSelected() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_SYSTEM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SYSTEM_ALBUM && resultCode == RESULT_OK && null != data) {
            if (Build.VERSION.SDK_INT >= 19) {
                handleImageOnKitkat(data);
            } else {
                handleImageBeforeKitkat(data);
            }
        }
    }

    @TargetApi(19)
    private void handleImageOnKitkat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" + "//downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是File类型的uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        mImagePath = imagePath;
//        displayImage(imagePath);//根据图片路径显示图片

        File file = new File(imagePath);
        if (file.length() > 512000) {
            Toast.makeText(this, "图片大小超过了512000字节", Toast.LENGTH_SHORT).show();
            return;
        }
        InputStream inputStream = null;
        byte[] bytes = null;
        try {
            inputStream = new FileInputStream(mImagePath);
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String result = base64Encoder.encode(bytes);

        NetDataLoader netDataLoader = new NetDataLoader(this);
        netDataLoader.sendMessage(NettyConstant.CLIENT_ID,"13138742085", result, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Result>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //Toast.makeText(ChatRoomActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Result result) {
                        //Toast.makeText(ChatRoomActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        if (result.getCode() == 0) {
                            Toast.makeText(ChatRoomActivity.this, "图片发送成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//        String saveMessage = NettyConstant.CLIENT_ID + ":发出图片" + mImagePath;
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setUserId(NettyConstant.CLIENT_ID);
        messageEntity.setMessage(mImagePath);
        messageEntity.setMessageType(1);
        saveMessage(NettyConstant.CLIENT_ID, mImagePath, 1);
        msgList.add(messageEntity);
        messageAdapter.setNewData(msgList);
        scrollToBottom();
    }

    private void handleImageBeforeKitkat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        mImagePath = imagePath;
//        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public void onClick(View v) {
        String message = editText.getText().toString();
        if (!message.equals("")) {
            NetDataLoader netDataLoader = new NetDataLoader(this);
            netDataLoader.sendMessage(NettyConstant.CLIENT_ID,"13138742085", message, 0)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Result>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            //Toast.makeText(ChatRoomActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(Result result) {
                            //Toast.makeText(ChatRoomActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                            if (result.getCode() == 0) {
                                Toast.makeText(ChatRoomActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            //String saveMessage = NettyConstant.CLIENT_ID + ":" + message;
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setUserId(NettyConstant.CLIENT_ID);
            messageEntity.setMessage(message);
            messageEntity.setMessageType(0);
            saveMessage(NettyConstant.CLIENT_ID, message, 0);
            msgList.add(messageEntity);
            messageAdapter.setNewData(msgList);
            scrollToBottom();
        }
    }

    @Override
    public void onKeyboardChange(boolean isShow, int keyboardHeight) {
        scrollToBottom();
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String fromWho = intent.getStringExtra("fromWho");
            byte[] data = intent.getByteArrayExtra("message");
            String message = new String(data);
            if (intent.getIntExtra("messageType", 0) == 0) {
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setUserId(fromWho);
                messageEntity.setMessage(message);
                messageEntity.setMessageType(0);
                saveMessage(fromWho, message, 0);
                msgList.add(messageEntity);
                messageAdapter.setNewData(msgList);
                scrollToBottom();
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
                    //String pictureMessage = fromWho + ":发来图片" + imagePath;
                    saveMessage(fromWho, imagePath, 1);
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setUserId(fromWho);
                    messageEntity.setMessage(imagePath);
                    messageEntity.setMessageType(1);
                    msgList.add(messageEntity);
                    messageAdapter.setNewData(msgList);
                    scrollToBottom();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveMessage(String userId, String message, int messageType) {
        MyApplication myApplication = (MyApplication) getApplication();
        MySQLiteOpenDatabase mySQLiteOpenDatabase = myApplication.getMySQLiteOpenDatabase();
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenDatabase.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", userId);
        contentValues.put("message", message);
        contentValues.put("message_type", messageType);
        long result = sqLiteDatabase.insert("message", null, contentValues);
        Log.d("debug", String.valueOf(result));
    }

    public List<MessageEntity> readMessageList() {
        List<MessageEntity> result = new ArrayList<>();
        MyApplication myApplication = (MyApplication) getApplication();
        MySQLiteOpenDatabase mySQLiteOpenDatabase = myApplication.getMySQLiteOpenDatabase();
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenDatabase.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from message", null);
        while (cursor.moveToNext()) {
            String userId = cursor.getString(cursor.getColumnIndex("user_id"));
            String message = cursor.getString(cursor.getColumnIndex("message"));
            int messageType = cursor.getInt(cursor.getColumnIndex("message_type"));
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setUserId(userId);
            messageEntity.setMessage(message);
            messageEntity.setMessageType(messageType);
            result.add(messageEntity);
        }
        cursor.close();
        return result;
    }
}
