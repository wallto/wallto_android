package com.example.wallto.network.services;

import com.example.wallto.data.User;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface TokenService {

    @GET("cvt")
    Single<User> checkValid(@Header("x-access-token") String token,
                            @Header("x-api-key") String app);

    @GET("extend")
    Single<User> refreshToken(@Header("x-access-token") String token,
                              @Header("x-api-key") String app);

}
