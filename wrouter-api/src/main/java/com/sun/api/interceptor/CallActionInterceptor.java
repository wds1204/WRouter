package com.sun.api.interceptor;

import android.util.Log;

import com.sun.api.ActionPost;
import com.sun.api.result.RouterResult;
import com.sun.api.action.IRouterAction;
import com.sun.api.extra.ActionWrapper;

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

        invokeAction(action);
    }

    private void invokeAction(ActionPost action, boolean isMainThread) {
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
