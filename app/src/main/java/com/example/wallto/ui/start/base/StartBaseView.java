/*
 * Created by Mark Abramenko on 10.08.19 20:53
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 10.08.19 20:53
 */

package com.example.wallto.ui.start.base;

import com.example.wallto.base.BaseView;

public interface StartBaseView extends BaseView {
    void openNextActivity();
    void openPinCodeActivity();
    void showProgressBar();
    void openStartAuthActivity();
    void showError();
}
