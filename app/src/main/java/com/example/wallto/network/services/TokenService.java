package com.example.wallto.network.services;

import com.example.wallto.data.User;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TokenService {

    @GET("cvt")
    Single<User> checkValid(@Query("utoken") String token,
                        @Query("app") String app);

    @GET("extend")
    Single<User> refreshToken(@Query("utoken") String token,
                        @Query("app") String app);

}
