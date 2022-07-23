package com.buyuphk.soketcommunication.rxretrofit.responseresult;

import java.util.List;

public class GetNewsResult {
    private int code;
    private List<ChatRecordDO> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ChatRecordDO> getData() {
        return data;
    }

    public void setData(List<ChatRecordDO> data) {
        this.data = data;
    }
}
