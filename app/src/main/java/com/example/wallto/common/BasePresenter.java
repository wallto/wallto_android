/*
 * Created by Mark Abramenko on 09.08.19 15:33
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 09.08.19 15:33
 */

package com.example.wallto.common;

import com.example.wallto.ui.start.StartBaseActivity;

public interface BasePresenter {
    void checkTokenValid();
    void refreshToken();
    void updateTokenData();
}
