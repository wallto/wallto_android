package com.example.wallto.network.services;

import com.example.wallto.data.*;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.ArrayList;

public interface WalletService {
    @GET("wallets")
    Single<ArrayList<Wallet>> getWallets(@Header("x-access-token") String token,
                                         @Header("x-api-key") String app);

    @GET("balance/{wid}")
    Single<BalanceResponse> getBalance(@Path("wid") int wid,
                                       @Query("utoken") String token,
                                       @Query("app") String app);

    @GET("wallet/{wid}")
    Single<Wallet> getWallet(@Path("wid") int wid,
                             @Header("x-access-token") String token,
                             @Header("x-api-key") String app);

    @GET("history/{wid}")
    Single<ArrayList<History>> getHistory(@Path("wid") int wid,
                               @Query("utoken") String token,
                               @Query("app") String app);

    @GET("create")
    Single<User> createWallet(@Query("type") String type,
                              @Query("testnet") boolean testnet,
                              @Query("utoken") String token,
                              @Query("app") String app,
                              @Query("title") String title);

    @GET("add")
    Single<User> addWallet(@Query("type") String type,
                           @Query("pr") String prKey,
                           @Query("pb") String pbKey,
                           @Query("ad") String address,
                           @Query("testnet") boolean testnet,
                           @Query("utoken") String token,
                           @Query("app") String app,
                           @Query("title") String title);

    @GET("send/{wid}")
    Single<DataResponse> send(@Path("wid") int wid,
                              @Query("ad") String address,
                              @Query("va") String value,
                              @Query("utoken") String token,
                              @Query("app") String app);
}
