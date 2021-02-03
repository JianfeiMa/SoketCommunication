package com.buyuphk.soketcommunication.thread;

import android.content.Context;

import com.buyuphk.soketcommunication.client.NodeClient;

/**
 * Copyright (C), buyuphk物流中转站
 * author: JianfeiMa
 * email: majianfei93@163.com
 * revised: 2020-04-21 21:17
 * motto: 勇于向未知领域探索
 */
public class MyThread extends Thread {
    private Context context;

    public MyThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        super.run();
        new NodeClient(context).connect();
    }
}
