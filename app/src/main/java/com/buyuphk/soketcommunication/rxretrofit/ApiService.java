package com.buyuphk.soketcommunication.rxretrofit;


import com.buyuphk.soketcommunication.rxretrofit.responseresult.GetNewsResult;
import com.buyuphk.soketcommunication.rxretrofit.responseresult.OnlineCustomerServiceResult;
import com.buyuphk.soketcommunication.rxretrofit.responseresult.Result;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

public interface ApiService {

    @ResponseFormat(value = "json")
    @GET(value = "http://192.168.1.33:7777/customerServiceController/getOnlineCustomerService")
    Observable<OnlineCustomerServiceResult> getOnlineCustomerService();

    @ResponseFormat(value = "json")
    @FormUrlEncoded
    @POST(value = "http://43.129.89.253:80/myTestController/sendMessageAgainStudy")
    Observable<Result> sendMessageAgainStudy(@Field(value = "fromWho") String fromWho, @Field(value = "toWho") String toWho, @Field(value = "message") String message, @Field(value = "messageType") int messageType);

    @ResponseFormat(value = "json")
    @FormUrlEncoded
    @POST(value = "http://43.129.89.253:80/myTestController/sendChatMessage")
    Observable<Result> sendMessage(@Field(value = "fromWho") String fromWho, @Field(value = "toWho") String toWho, @Field(value = "message") String message, @Field(value = "messageType") int messageType);

    @ResponseFormat(value = "json")
    @FormUrlEncoded
    @POST(value = "http://43.129.89.253:80/apiCommonController/getNewsMessage")
    Observable<GetNewsResult> getNewsMessage(@Field(value = "toWho") int toWho);
}
