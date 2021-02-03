package com.buyuphk.soketcommunication;

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
 * revised: 2020-07-18 08:38
 * motto: 勇于向未知领域探索
 */
public class MainActivity1 extends AppCompatActivity {
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        button = findViewById(R.id.activity_main_button);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                        Toast.makeText(MainActivity1.this, onlineCustomerServiceResult.getMessage(), Toast.LENGTH_SHORT).show();
                        if (onlineCustomerServiceResult.getCode() == 0) {
                            button.setVisibility(View.VISIBLE);
                        } else {
                            button.setVisibility(View.GONE);
                        }
                    }
                });
    }
}
