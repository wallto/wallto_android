/*
 * Created by Mark Abramenko on 10.08.19 20:51
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 10.08.19 20:50
 */

package com.example.wallto.ui.start.base;

import android.annotation.SuppressLint;
import com.example.wallto.data.User;
import com.example.wallto.data.body.TokenBody;
import com.example.wallto.network.RestApi;
import com.example.wallto.network.services.TokenService;
import com.example.wallto.utils.PrefsRepository;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import java.util.Objects;

public class StartBasePresenterImpl implements StartBasePresenter {

    private TokenService tokenService;
    private StartBaseView view;

    public StartBasePresenterImpl(StartBaseView _view) {
        this.view = _view;
    }


    @SuppressLint("CheckResult")
    private void checkTokenValid() {
        tokenService.checkValid(PrefsRepository.Companion.getValue(PrefsRepository.Keys.TOKEN.toString()), "gnomes")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> {
                            checkTokenResponseSuccess(user);
                        },
                        throwable -> {
                            view.writeLog("Ошибка checkTokenValid: ", throwable);
                            checkForPin();
                        });
    }

    private void checkTokenResponseSuccess(User user) {
        if (Objects.equals(user.getMessage(), "ok")) {
            view.writeLog("Ответ checkTokenValid: " + user.getMessage());
            successAuth();
        }
    }

    @SuppressLint("CheckResult")
    private void refreshToken() {
        TokenBody tokenBody = new TokenBody(PrefsRepository.Companion.getValue(PrefsRepository.Keys.TOKEN.toString()));
        tokenService.refreshToken(tokenBody, "gnomes")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> {
                            view.writeLog("Ответ на refreshToken: " + user.getUser_token());
                            updateTokenData(user);
                        },
                        throwable -> {
                            view.writeLog("Ошибка refreshToken: ", throwable);
                            view.showError();
                        }
                );
    }

    private void updateTokenData(User user) {
        PrefsRepository.Companion.putValue(PrefsRepository.Keys.TOKEN.toString(), Objects.requireNonNull(user.getUser_token()));
        successAuth();
    }

    @Override
    public void initNetwork() {
        Retrofit retrofit = RestApi.getInstance();
        tokenService = retrofit.create(TokenService.class);
    }

    private boolean isTokenExist() {
        return !Objects.equals(PrefsRepository.Companion.getValue(PrefsRepository.Keys.TOKEN.toString()), "");
    }

    private void successAuth() {
        view.openNextActivity();
    }

    private void checkForPin() {
        if (Objects.equals(PrefsRepository.Companion.getValue(PrefsRepository.Keys.PIN.toString()), "")) {
            refreshToken();
        } else {
            view.openPinCodeActivity();
        }
    }

    @Override
    public void checkForToken() {
        if (isTokenExist()) {
            view.showProgressBar();
            initNetwork();
            checkTokenValid();
        } else {
            view.openStartAuthActivity();
        }
    }
}
