package com.buyuphk.soketcommunication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.buyuphk.soketcommunication.db.MySQLiteOpenDatabase;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        android.widget.ListView listView = (android.widget.ListView) findViewById(R.id.activity_about_list_view);
        MyApplication myApplication = (MyApplication) getApplication();
        MySQLiteOpenDatabase mySQLiteOpenDatabase = myApplication.getMySQLiteOpenDatabase();
        android.database.sqlite.SQLiteDatabase sqLiteDatabase = mySQLiteOpenDatabase.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from log order by id desc", null);
        List<String> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String logContent = cursor.getString(cursor.getColumnIndex("log_content"));
            String heartTime = cursor.getString(cursor.getColumnIndex("heart_time"));
            String item = id + ";" + logContent + ";" + heartTime;
            list.add(item);
        }
        cursor.close();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        android.view.MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_about, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            final EditText editText = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle("输入日期")
                    .setView(editText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            delete(editText.getText().toString());
                        }
                    })
                    .create()
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void delete(String date) {
        MyApplication myApplication = (MyApplication) getApplication();
        MySQLiteOpenDatabase mySQLiteOpenDatabase = myApplication.getMySQLiteOpenDatabase();
        android.database.sqlite.SQLiteDatabase sqLiteDatabase = mySQLiteOpenDatabase.getWritableDatabase();
        String[] whereArgs = new String[2];
        whereArgs[0] = date + " 00:00:00";
        whereArgs[1] = date + " 23:59:59";
        int result = sqLiteDatabase.delete("log", "heart_time >= ? and heart_time <= ?", whereArgs);
        Log.d("debug", "删除结果->" + result);
        Toast.makeText(this, "删除数量:" + result, Toast.LENGTH_SHORT).show();
    }
}