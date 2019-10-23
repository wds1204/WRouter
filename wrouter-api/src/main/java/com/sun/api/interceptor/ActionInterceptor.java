package com.sun.api.interceptor;

import com.sun.api.thread.ActionPost;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: ActionInterceptor.java
 * Author: wds_sun
 * Date: 2019-10-21 19:08
 * Description:
 */
public interface ActionInterceptor {

    void intercept(ActionChain chain);


    interface ActionChain {
        void onInterrupt();

        void proceed(ActionPost action);

        ActionPost action();

        String actionPath();
    }
}
