package com.example.wallto.network.services;

import com.example.wallto.model.BalanceResponse;
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

}
