package com.sun.api.interceptor;

import com.sun.api.ActionPost;
import com.sun.api.result.ActionCallback;

import java.util.List;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: ActionInterceptorChain.java
 * Author: wds_sun
 * Date: 2019-10-22 11:10
 * Description:
 */
public class ActionInterceptorChain implements ActionInterceptor.ActionChain {

    public final ActionCallback callback;
    // 是否被拦截了
    private boolean isInterrupt = false;
    private List<ActionInterceptor> interceptors;
    private ActionPost actionPost;
    private int index;

    public ActionInterceptorChain(List<ActionInterceptor> mInterceptors, ActionPost actionPost, int index, ActionCallback callback) {
        this.interceptors = mInterceptors;
        this.actionPost = actionPost;
        this.index = index;
        this.callback=callback;

    }

    @Override
    public void onInterrupt() {
        isInterrupt=true;

        this.callback.onInterrupt();

    }

    @Override
    public void proceed(ActionPost action) {
        
        if(!isInterrupt&&index<interceptors.size()) {
            ActionInterceptorChain next = new ActionInterceptorChain(interceptors, actionPost, index + 1,this.callback);
            ActionInterceptor interceptor = interceptors.get(index);
            interceptor.intercept(next);
        }

    }

    @Override
    public ActionPost action() {
        return actionPost;
    }

    @Override
    public String actionPath() {
        return actionPost.actionWrapper.getPath();
    }
}
