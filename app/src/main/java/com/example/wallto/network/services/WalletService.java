package com.example.wallto.network.services;

import com.example.wallto.model.BalanceResponse;
import com.example.wallto.model.History;
import com.example.wallto.model.User;
import com.example.wallto.model.Wallet;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.ArrayList;

public interface WalletService {
    @GET("wallets")
    Single<ArrayList<Wallet>> getWallets(@Query("utoken") String token,
                                         @Query("app") String app);

    @GET("balance/{wid}")
    Single<BalanceResponse> getBalance(@Path("wid") int wid,
                                       @Query("utoken") String token,
                                       @Query("app") String app);

    @GET("wallet/{wid}")
    Single<Wallet> getWallet(@Path("wid") int wid,
                             @Query("utoken") String token,
                             @Query("app") String app);

    @GET("history/{wid}")
    Single<History> getHistory(@Path("wid") int wid,
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
}
