package com.sun.api.core;

import android.content.Context;

import com.sun.api.thread.ActionPost;
import com.sun.api.extra.ActionWrapper;
import com.sun.api.interceptor.ActionInterceptor;
import com.sun.api.interceptor.ActionInterceptorChain;
import com.sun.api.result.ActionCallback;
import com.sun.wrouter.base.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C), 2016-2019
 * File: RouterForward.java
 * Author: wds_sun
 * Date: 2019-10-21 16:04
 * Description:
 */
public class RouterForward {

    private final List<ActionInterceptor> mInterceptors;
    private ActionWrapper mActionWrapper;
    private Context mContext;
    private Map<String, Object> mParams;
    private ThreadMode mThreadMode = null;
    private ActionInterceptor.ActionChain chain;


    public RouterForward(ActionWrapper actionWrapper, List<ActionInterceptor> interceptors) {
        this.mActionWrapper = actionWrapper;
        this.mInterceptors = interceptors;
        mParams = new HashMap<>();

    }

    /**
     * 执行 Action
     */
    public void invokeAction() {
       invokeAction(ActionCallback.DEFAULT_ACTION_CALLBACK);
    }

    public void invokeAction(ActionCallback callback){

        mActionWrapper.setThreadMode(getThreadMode());

        ActionPost actionPost = ActionPost.obtainActionPost(mActionWrapper, mContext, mParams,callback);
        if(chain==null) {
            chain = new ActionInterceptorChain(mInterceptors, actionPost, 0,callback);
        }
        chain.proceed(actionPost);

    }

    private ThreadMode getThreadMode() {
        return mThreadMode == null ? mActionWrapper.getThreadMode() : mThreadMode;
    }

    public RouterForward param(String key, Object value) {
        mParams.put(key, value);
        return this;
    }

    public RouterForward param(Map<String, Object> params) {
        mParams.putAll(params);
        return this;
    }

    public RouterForward context(Context context) {
        this.mContext = context;
        return this;
    }
}
