package com.buyuphk.soketcommunication.rxretrofit.responseresult;

import java.sql.Timestamp;

/**
 * author : JianfeiMa
 * e-mail : 1017033681@qq.com
 * date   : 2021/7/1 21:27
 * desc   :
 * version: 1.0
 */
public class ChatRecordDO {
    private int id;
    private int fromWho;
    private int toWho;
    private String message;
    private int messageType;
    private int sendStatus;
    private Timestamp createDateTime;
    private Timestamp updateDateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromWho() {
        return fromWho;
    }

    public void setFromWho(int fromWho) {
        this.fromWho = fromWho;
    }

    public int getToWho() {
        return toWho;
    }

    public void setToWho(int toWho) {
        this.toWho = toWho;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Timestamp getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Timestamp createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Timestamp getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(Timestamp updateDateTime) {
        this.updateDateTime = updateDateTime;
    }
}
