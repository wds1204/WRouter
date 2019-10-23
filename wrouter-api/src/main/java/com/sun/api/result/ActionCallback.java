package com.sun.api.result;

/**
 * Copyright (C), 2016-2019
 * File: ActionCallback.java
 * Author: wds_sun
 * Date: 2019-10-22 11:46
 * Description:
 */
public interface ActionCallback {
    // 被拦截了
    void onInterrupt();

    // 没被拦截返回结果
    void onResult(RouterResult result);

    ActionCallback DEFAULT_ACTION_CALLBACK=new ActionCallback() {
        @Override
        public void onInterrupt() {

        }

        @Override
        public void onResult(RouterResult result) {

        }
    };
}
