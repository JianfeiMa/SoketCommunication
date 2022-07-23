package com.buyuphk.soketcommunication.rxretrofit;

import android.content.Context;

import com.buyuphk.soketcommunication.rxretrofit.responseresult.GetNewsResult;
import com.buyuphk.soketcommunication.rxretrofit.responseresult.OnlineCustomerServiceResult;
import com.buyuphk.soketcommunication.rxretrofit.responseresult.Result;

import rx.Observable;
import rx.functions.Func1;

public class NetDataLoader extends ObjectLoader {
    public static final String TAG = NetDataLoader.class.getSimpleName();
    private ApiService apiService;
    private Context context;
    private static final String md5Code = "0b50f4d1ac63162f1da6b9d9cd56d3ef";

    public NetDataLoader(Context context) {
        this.context = context;
        apiService = RetrofitServiceManager.getInstance().create(ApiService.class);
    }

    public Observable<OnlineCustomerServiceResult> getOnlineCustomerService() {
        return observe(apiService.getOnlineCustomerService().map(new Func1<OnlineCustomerServiceResult, OnlineCustomerServiceResult>() {
            @Override
            public OnlineCustomerServiceResult call(OnlineCustomerServiceResult s) {
                return s;
            }
        }));
    }

    public Observable<Result> sendMessage(String fromWho, String toWho, String message, int messageType) {
        return observe(apiService.sendMessage(fromWho, toWho, message, messageType)).map(new Func1<Result, Result>() {
            @Override
            public Result call(Result result) {
                return result;
            }
        });
    }

    public Observable<GetNewsResult> getNewsMessage(int toWho) {
        return observe(apiService.getNewsMessage(toWho).map(new Func1<GetNewsResult, GetNewsResult>() {
            @Override
            public GetNewsResult call(GetNewsResult getNewsResult) {
                return getNewsResult;
            }
        }));
    }
}
