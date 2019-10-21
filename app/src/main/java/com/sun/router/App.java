package com.sun.router;

import android.app.Application;

import com.sun.api.core.WRouter;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: App.java
 * Author: wds_sun
 * Date: 2019-10-21 15:46
 * Description:
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        WRouter.getInstance().init(this);
    }
}
