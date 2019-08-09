package com.example.wallto.network.services;

import com.example.wallto.data.Currency;
import io.reactivex.Single;
import retrofit2.http.GET;

import java.util.ArrayList;

public interface InfoService {
    @GET("v1/ticker")
    Single<ArrayList<Currency>> getPrices();
}
