package com.sun.api.interceptor;

import android.util.Log;

import com.sun.api.thread.ActionPost;
import com.sun.api.extra.ErrorActionWrapper;

/**
 * Copyright (C), 2016-2019
 * File: ErrorActionInterceptor.java
 * Author: wds_sun
 * Date: 2019-10-22 10:50
 * Description:
 */
public class ErrorActionInterceptor implements ActionInterceptor {
    @Override
    public void intercept(ActionChain chain) {
        ActionPost action = chain.action();

        if(action.actionWrapper instanceof ErrorActionWrapper) {
            Log.e("TAG", "action====ErrorActionWrapper");
            chain.onInterrupt();
        }
        chain.proceed(action);
    }
}
