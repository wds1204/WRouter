package com.sun.api.thread;

import com.sun.api.action.IRouterAction;
import com.sun.api.extra.ActionWrapper;
import com.sun.api.result.RouterResult;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: AsyncPoster.java
 * Author: wds_sun
 * Date: 2019-10-23 17:18
 * Description:
 */
class AsyncPoster implements Runnable, Poster {
    private final ActionPostQueue queue;

    AsyncPoster() {
        queue = new ActionPostQueue();
    }

    @Override
    public void run() {
        ActionPost actionPost = queue.poll();
        if (actionPost == null) {
            throw new IllegalStateException("No pending post available");
        }

        ActionWrapper actionWrapper = actionPost.actionWrapper;
        IRouterAction routerAction = actionWrapper.getRouterAction();
        RouterResult routerResult = routerAction.invokeAction(actionPost.context, actionPost.params);
        actionPost.callback.onResult(routerResult);

        actionPost.releasePendingPost();
    }

    @Override
    public void enqueue(ActionPost actionPost) {
        queue.enqueue(actionPost);
        PosterSupport.getExecutorService().execute(this);
    }

}
