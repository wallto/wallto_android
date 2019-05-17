package com.example.wallto.network;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApi {
    private static final String BASE_URL = "http://176.53.162.231:5000/";
    private static Retrofit ourInstance;

    public static Retrofit getInstance() {
        if (ourInstance == null) {
            ourInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return ourInstance;
    }
}
