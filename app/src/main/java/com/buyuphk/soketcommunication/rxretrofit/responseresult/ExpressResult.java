package com.buyuphk.soketcommunication.rxretrofit.responseresult;

/**
 * Copyright (C), buyuphk物流中转站
 * author: JianfeiMa
 * email: majianfei93@163.com
 * revised: 2020-03-10 09:32
 * motto: 勇于向未知领域探索
 */
public class ExpressResult {
    private String success;
    private String error;
    private String storagerackinfo;
    private String orderstate;
    private String expressnum;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStoragerackinfo() {
        return storagerackinfo;
    }

    public void setStoragerackinfo(String storagerackinfo) {
        this.storagerackinfo = storagerackinfo;
    }

    public String getOrderstate() {
        return orderstate;
    }

    public void setOrderstate(String orderstate) {
        this.orderstate = orderstate;
    }

    public String getExpressnum() {
        return expressnum;
    }

    public void setExpressnum(String expressnum) {
        this.expressnum = expressnum;
    }

    @Override
    public String toString() {
        return "ExpressResult{" +
                "success='" + success + '\'' +
                ", error='" + error + '\'' +
                ", storagerackinfo='" + storagerackinfo + '\'' +
                ", orderstate='" + orderstate + '\'' +
                ", expressnum='" + expressnum + '\'' +
                '}';
    }
}
