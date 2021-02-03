package com.buyuphk.soketcommunication.rxretrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Copyright (C), buyuphk物流中转站
 * author: JianfeiMa
 * email: majianfei93@163.com
 * 2019-09-10 10:11
 */
public class JsonOrXmlConverterFactory extends Converter.Factory {
    private final Converter.Factory factory = GsonConverterFactory.create();

    public static JsonOrXmlConverterFactory create() {
        return new JsonOrXmlConverterFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        super.responseBodyConverter(type, annotations, retrofit);
        for (Annotation annotation : annotations) {
            if (annotation instanceof ResponseFormat) {
                ResponseFormat responseFormat = (ResponseFormat) annotation;
                String value = responseFormat.value();
                if (value.equals(ResponseFormat.JSON)) {
                    return factory.responseBodyConverter(type, annotations, retrofit);
                } else if (value.equals(ResponseFormat.XML)) {
                    return null;
                } else {
                    return null;
                }
            }
        }
        return null;
    }
}
