/*
 * Created by Mark Abramenko on 10.08.19 19:16
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 10.08.19 19:16
 */

package com.example.wallto;

import android.app.Application;
import com.example.wallto.utils.PrefsRepository;

public class WalltoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PrefsRepository.Companion.init(this);
    }
}
