package com.example.wallto.network.services;

import com.example.wallto.model.Currency;
import com.example.wallto.model.PriceResponse;
import com.example.wallto.model.User;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.ArrayList;

public interface InfoService {
    @GET("price")
    Single<PriceResponse> getPrices(@Query("app") String app);
}
