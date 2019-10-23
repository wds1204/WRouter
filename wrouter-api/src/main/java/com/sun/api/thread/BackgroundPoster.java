package com.sun.api.thread;

import com.sun.api.action.IRouterAction;
import com.sun.api.extra.ActionWrapper;
import com.sun.api.result.RouterResult;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: BackgroundPoster.java
 * Author: wds_sun
 * Date: 2019-10-23 17:07
 * Description:
 */
class BackgroundPoster implements Runnable, Poster {
    ActionPostQueue queue;

    private volatile boolean executorRunning;


    public BackgroundPoster() {
        this.queue = new ActionPostQueue();
    }

    @Override
    public void enqueue(ActionPost actionPost) {
        synchronized (this){
            queue.enqueue(actionPost);
            if(!executorRunning) {
                executorRunning=true;
                PosterSupport.getExecutorService().execute(this);
            }
        }
    }

    @Override
    public void run() {
        try {

            while (true){
                ActionPost pendingPost = queue.poll();
                if(pendingPost==null) {
                    synchronized (this){
                        pendingPost= queue.poll();
                        if(pendingPost==null) {
                            executorRunning=false;
                            return;
                        }
                    }

                }
                ActionWrapper actionWrapper=pendingPost.actionWrapper;
                IRouterAction routerAction = actionWrapper.getRouterAction();
                RouterResult routerResult = routerAction.invokeAction(pendingPost.context, pendingPost.params);
                pendingPost.callback.onResult(routerResult);

                pendingPost.releasePendingPost();
            }

        }finally {
           executorRunning=false;
        }
    }
}
