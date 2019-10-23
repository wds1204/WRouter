package com.sun.api.interceptor;

import android.os.Looper;
import android.util.Log;

import com.sun.api.thread.ActionPost;
import com.sun.api.result.RouterResult;
import com.sun.api.action.IRouterAction;
import com.sun.api.extra.ActionWrapper;
import com.sun.api.thread.PosterSupport;
import com.sun.wrouter.base.ThreadMode;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: CallActionInterceptor.java
 * Author: wds_sun
 * Date: 2019-10-22 11:00
 * Description:
 */
public class CallActionInterceptor implements ActionInterceptor {
    @Override
    public void intercept(ActionChain chain) {
        ActionPost action = chain.action();

        invokeAction(action, Looper.myLooper() == Looper.getMainLooper());
    }

    private void invokeAction(ActionPost action, boolean isMainThread) {
        ActionWrapper actionWrapper = action.actionWrapper;
        ThreadMode threadMode = actionWrapper.getThreadMode();
        switch (threadMode) {
            case POSTING:
                invokeAction(action);
                break;
            case MAIN:
                if (isMainThread) {
                    invokeAction(action);
                } else {
                    Log.e("TAG", "invokeAction--------->"+Thread.currentThread().getName());
                    PosterSupport.getMainPoster().enqueue(action);
                }
                break;
            case BACKGROUND:
                    if(isMainThread) {
                        PosterSupport.getBackgroundPoster().enqueue(action);
                    }else {
                        invokeAction(action);
                    }
                break;
            case ASYNC:
                PosterSupport.getAsyncPoster().enqueue(action);
                break;
            default:
                throw new IllegalStateException("Unknown thread mode: " + action.actionWrapper.getThreadMode());
        }

    }

    /**
     * 执行 Action
     *
     * @param actionPost
     */
    private void invokeAction(ActionPost actionPost) {
        ActionWrapper actionWrapper = actionPost.actionWrapper;
        IRouterAction routerAction = actionWrapper.getRouterAction();
        RouterResult routerResult = routerAction.invokeAction(actionPost.context, actionPost.params);
        actionPost.callback.onResult(routerResult);

    }
}
