package com.buyuphk.soketcommunication;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.buyuphk.soketcommunication.adapter.MyItemClickListener;
import com.buyuphk.soketcommunication.adapter.UserAdapter;
import com.buyuphk.soketcommunication.bean.GroupEntity;
import com.buyuphk.soketcommunication.bean.UserEntity;
import com.buyuphk.soketcommunication.db.MySQLiteOpenDatabase;
import com.buyuphk.soketcommunication.rxretrofit.NetDataLoader;
import com.buyuphk.soketcommunication.rxretrofit.responseresult.ChatRecordDO;
import com.buyuphk.soketcommunication.rxretrofit.responseresult.GetNewsResult;
import com.buyuphk.soketcommunication.service.MyService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import rx.Observer;

/**
 * Copyright (C), buyuphk物流中转站
 * author: JianfeiMa
 * email: majianfei93@163.com
 * revised: 2020-07-21 08:31
 * motto: 勇于向未知领域探索
 */
public class ChatListActivity extends AppCompatActivity implements MyItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;
    private UserAdapter userAdapter;
    private MyBroadcastReceiver myBroadcastReceiver;
    private String userId;
    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        /** 在服务中开始建立netty的socket链接 */
        startService(new Intent(this, MyService.class));
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter("update_message");
        registerReceiver(myBroadcastReceiver, intentFilter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("我的会话列表");
        }

        swipeRefreshLayout = findViewById(R.id.activity_user_list_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        RecyclerView recyclerView = findViewById(R.id.activity_login_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<UserEntity> userList = readUserList();
        userAdapter = new UserAdapter(this, userList);
        userAdapter.setMyItemClickListener(this);
        recyclerView.setAdapter(userAdapter);
        getNewsMessage();
    }

    private void myAlarm() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + (1000 * 60));
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(ALARM_SERVICE);
        android.content.ComponentName componentName = new android.content.ComponentName("com.buyuphk.soketcommunication", "com.buyuphk.soketcommunication.ChatListActivity");
        android.content.Intent intent = new Intent();
        intent.setComponent(componentName);
//        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(this, 0x0, intent, 0);
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(getApplication(), 0x0, intent, 0);
        alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 9000, pendingIntent);
    }

    private List<UserEntity> readUserList() {
        MyApplication myApplication = (MyApplication) getApplication();
        MySQLiteOpenDatabase mySQLiteOpenDatabase = myApplication.getMySQLiteOpenDatabase();
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenDatabase.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from user", null);
        List<UserEntity> userList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String userId = cursor.getString(cursor.getColumnIndex("user_id"));
            UserEntity userEntity = new UserEntity();
            userEntity.setUserId(userId);
            userList.add(userEntity);
        }
        cursor.close();
        if (userList.size() > 0) {
            List<GroupEntity> unreadList = getUnreadList();
            for (int i = 0; i < userList.size();i ++) {
                UserEntity userEntity = userList.get(i);
                for (int j = 0; i < unreadList.size(); i ++) {
                    GroupEntity groupEntity = unreadList.get(i);
                    if (userEntity.getUserId().equals(String.valueOf(groupEntity.getUserId()))) {
                        userEntity.setUnreadCount(groupEntity.getUnreadCount());
                    }
                }
            }
        }
        return userList;
    }

    private List<GroupEntity> getUnreadList() {
        List<GroupEntity> unreadList = new ArrayList<>();
        MyApplication myApplication = (MyApplication) getApplication();
        MySQLiteOpenDatabase mySQLiteOpenDatabase = myApplication.getMySQLiteOpenDatabase();
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenDatabase.getReadableDatabase();
        String[] columns = new String[2];
        columns[0] = "user_id";
        columns[1] = "count (id) as id";
        String selection = "is_read = ?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = "0";
        String groupBy = "user_id";
        String having = null;
        String orderBy = "id DESC";
        Cursor cursor = sqLiteDatabase.query("message", columns, selection, selectionArgs, groupBy, having, orderBy);
        while (cursor.moveToNext()) {
            int userId = cursor.getInt(cursor.getColumnIndex("user_id"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            GroupEntity groupEntity = new GroupEntity();
            groupEntity.setUserId(userId);
            groupEntity.setUnreadCount(id);
            unreadList.add(groupEntity);
        }
        cursor.close();
        return unreadList;
    }

    @Override
    public void onItemClick(View view, int position) {
        UserEntity userEntity = userAdapter.getUserList().get(position);
        userId = userEntity.getUserId();
        int unreadCount = userEntity.getUnreadCount();
        Log.d("debug", "被点击的UserId->" + userId);
        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.putExtra("userId", userId);
        startActivityForResult(intent, 88);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("debug", "回退刷新数据");
        MyApplication myApplication = (MyApplication) getApplication();
        MySQLiteOpenDatabase mySQLiteOpenDatabase = myApplication.getMySQLiteOpenDatabase();
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenDatabase.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int isRead = 1;
        contentValues.put("is_read", isRead);
        String whereClause = "is_read = 0 and user_id = ?";
        String[] whereArgs = new String[1];
        whereArgs[0] = userId;
        int updateResult = sqLiteDatabase.update("message", contentValues, whereClause, whereArgs);
        Log.d("debug", "用户的全部更新未已读->" + updateResult);
        List<UserEntity> userList = readUserList();
        userAdapter.setNewData(userList);
    }

    @Override
    public void onRefresh() {
        List<UserEntity> userList = readUserList();
        userAdapter.setNewData(userList);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_chat_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_user_list_status) {
            editText = new EditText(this);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请输入客人编号");
            builder.setView(editText);
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String userId = editText.getText().toString();
                    if (!userId.equals("")) {
                        Set<Integer> set = new TreeSet<>();
                        set.add(Integer.valueOf(userId));
                        saveUser(set);
                        onRefresh();
                    }
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if (item.getItemId() == R.id.menu_user_list_alarm) {
            myAlarm();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            List<UserEntity> userList = readUserList();
            userAdapter.setNewData(userList);
        }
    }

    private void getNewsMessage() {
        swipeRefreshLayout.setRefreshing(true);
        NetDataLoader netDataLoader = new NetDataLoader(this);
        netDataLoader.getNewsMessage(Integer.valueOf(NettyConstant.CLIENT_ID))
                .subscribe(new Observer<GetNewsResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(ChatListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GetNewsResult getNewsResult) {
                        responseNewsMessage(getNewsResult);
                    }
                });
    }

    private void responseNewsMessage(GetNewsResult getNewsResult) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        List<ChatRecordDO> chatRecordDOList = getNewsResult.getData();
        if (chatRecordDOList != null && chatRecordDOList.size() > 0) {
            Set<Integer> userIdSet = new TreeSet<>();
            MyApplication myApplication = (MyApplication) getApplication();
            MySQLiteOpenDatabase mySQLiteOpenDatabase = myApplication.getMySQLiteOpenDatabase();
            SQLiteDatabase sqLiteDatabase = mySQLiteOpenDatabase.getWritableDatabase();
            for (int i = 0; i < chatRecordDOList.size(); i ++) {
                ChatRecordDO chatRecordDO = chatRecordDOList.get(i);
                int fromWho = chatRecordDO.getFromWho();
                userIdSet.add(fromWho);
                ContentValues contentValues = new ContentValues();
                contentValues.put("user_id", String.valueOf(fromWho));
                contentValues.put("speaker", String.valueOf(fromWho));
                contentValues.put("message", chatRecordDO.getMessage());
                contentValues.put("message_type", chatRecordDO.getMessageType());
                contentValues.put("create_date_time", chatRecordDO.getCreateDateTime().getTime());
                long insertResult = sqLiteDatabase.insert("message", null, contentValues);
                Log.d("debug", "插入从接口获取的未读消息->" + insertResult);
            }
            saveUser(userIdSet);
            List<UserEntity> userList = readUserList();
            userAdapter.setNewData(userList);
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.news_arrived);
            mediaPlayer.start();
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(300);
        }
    }

    private boolean isExist(String userId) {
        boolean result = false;
        MyApplication myApplication = (MyApplication) getApplication();
        MySQLiteOpenDatabase mySQLiteOpenDatabase = myApplication.getMySQLiteOpenDatabase();
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenDatabase.getReadableDatabase();
        String[] selectionArgs = new String[1];
        selectionArgs[0] = userId;
        Cursor cursor = sqLiteDatabase.rawQuery("select id from user where user_id = ?", selectionArgs);
        while (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        return result;
    }

    private void saveUser(Set<Integer> userIdSet) {
        MyApplication myApplication = (MyApplication) getApplication();
        MySQLiteOpenDatabase mySQLiteOpenDatabase = myApplication.getMySQLiteOpenDatabase();
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenDatabase.getReadableDatabase();
        Iterator<Integer> integerIterator = userIdSet.iterator();
        while (integerIterator.hasNext()) {
            Integer userId = integerIterator.next();
            if (!isExist(String.valueOf(userId))) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("user_id", String.valueOf(userId));
                long insertResult = sqLiteDatabase.insert("user", null, contentValues);
                Log.d("debug", "插入新用户->" + insertResult);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("刪除")) {
            Log.d("debug", "删除按钮点击");
            UserEntity userEntity = userAdapter.getUserList().get(userAdapter.getPosition());
            MyApplication myApplication = (MyApplication) getApplication();
            MySQLiteOpenDatabase mySQLiteOpenDatabase = myApplication.getMySQLiteOpenDatabase();
            SQLiteDatabase sqLiteDatabase = mySQLiteOpenDatabase.getWritableDatabase();
            String[] whereArgs = new String[1];
            whereArgs[0] = userEntity.getUserId();
            int deleteResult = sqLiteDatabase.delete("message", "user_id = ?", whereArgs);
            int deleteResult1 = sqLiteDatabase.delete("user", "user_id = ?", whereArgs);
            Log.d("debug", "删除消息表->" + deleteResult);
            Log.d("debug", "删除用户表->" + deleteResult1);
            onRefresh();
        }
        return super.onContextItemSelected(item);
    }
}
