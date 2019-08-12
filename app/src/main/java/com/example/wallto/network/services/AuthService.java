package com.example.wallto.network.services;

import com.example.wallto.data.User;
import com.example.wallto.data.body.UserBody;
import io.reactivex.Single;
import retrofit2.http.*;

public interface AuthService {
    @POST("login")
    Single<User> signIn(@Header("x-api-key") String head, @Body UserBody userBody);

    @POST("register")
    Single<User> signUp(@Header("x-api-key") String head, @Body UserBody userBody);

    @GET("logout")
    Single<User> logOut(@Header("x-access-token") String token,
                        @Header("x-api-key") String app);
}
