package com.example.wallto.network.services;

import com.example.wallto.data.User;
import com.example.wallto.data.body.TokenBody;
import io.reactivex.Single;
import retrofit2.http.*;

public interface TokenService {

    @GET("cvt")
    Single<User> checkValid(@Header("x-access-token") String token,
                            @Header("x-api-key") String app);

    @POST("extend")
    Single<User> refreshToken(@Body TokenBody token,
                              @Header("x-api-key") String app);

}
