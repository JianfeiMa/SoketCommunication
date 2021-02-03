package com.buyuphk.soketcommunication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.buyuphk.soketcommunication.rxretrofit.NetDataLoader;
import com.buyuphk.soketcommunication.rxretrofit.responseresult.OnlineCustomerServiceResult;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), buyuphk物流中转站
 * author: JianfeiMa
 * email: majianfei93@163.com
 * revised: 2020-07-21 08:31
 * motto: 勇于向未知领域探索
 */
public class LoginActivity extends AppCompatActivity {
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        button = findViewById(R.id.btn_login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ChatRoomActivity.class);
                startActivity(intent);
            }
        });
        NetDataLoader netDataLoader = new NetDataLoader(this);
        netDataLoader.getOnlineCustomerService()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OnlineCustomerServiceResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(OnlineCustomerServiceResult onlineCustomerServiceResult) {
                        Log.d("debug", "print:" + onlineCustomerServiceResult.toString());
                        Toast.makeText(LoginActivity.this, onlineCustomerServiceResult.getMessage(), Toast.LENGTH_SHORT).show();
                        if (onlineCustomerServiceResult.getCode() == 0) {
                            button.setVisibility(View.VISIBLE);
                        } else {
                            button.setVisibility(View.GONE);
                        }
                    }
                });
    }
}
