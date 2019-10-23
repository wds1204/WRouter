package com.sun.api.thread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.sun.api.action.IRouterAction;
import com.sun.api.extra.ActionWrapper;
import com.sun.api.result.RouterResult;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: HandlerPoster.java
 * Author: wds_sun
 * Date: 2019-10-23 16:15
 * Description:
 * enqueue()方法并不会每次都发送Message激活handleMessage()，这是通过handlerActive标志位进行控制的。
 * 那么enqueue()中那些没有被消费的事件该怎么消费呢？
 * 答案是handleMessage()中的while死循环，但是为了避免一直在死循环中处理事件影响主线程的性能，
 * 又设置了一个超时时间，一旦执行了超时了，那么再发送一个Message并且退出，
 * 那么Handler的机制可以保证过会儿又能进入到handleMessage()方法中继续处理队列中的事件。
 * 核心还是通过反射进行调用的，这儿也能看出订阅方法的执行是在主线程中的。
 * 但是由于enqueue()的存在，订阅与发布是异步的，订阅的消费不会阻塞发布。
 * ————————————————
 *
 */
public class HandlerPoster extends Handler implements Poster {

    private final int maxMillisInsideHandleMessage;
    private final ActionPostQueue queue;
    private boolean handlerActive;

    protected HandlerPoster(Looper looper, int maxMillisInsideHandleMessage) {
        super(looper);
        this.maxMillisInsideHandleMessage = maxMillisInsideHandleMessage;
        queue = new ActionPostQueue();
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean rescheduled = false;
        try {
            long started = SystemClock.uptimeMillis();
            while (true){
                ActionPost pendingPost = queue.poll();
                if(pendingPost==null) {
                    synchronized (this){
                        // Check again, this time in synchronized
                        pendingPost = queue.poll();
                        if (pendingPost == null) {
                            handlerActive = false;
                            return;
                        }
                    }
                }
                ActionWrapper actionWrapper=pendingPost.actionWrapper;
                IRouterAction routerAction = actionWrapper.getRouterAction();
                RouterResult routerResult = routerAction.invokeAction(pendingPost.context, pendingPost.params);
                pendingPost.callback.onResult(routerResult);

                pendingPost.releasePendingPost();

                long timeInMethod = SystemClock.uptimeMillis()-started;
                if(timeInMethod>=maxMillisInsideHandleMessage) {
                    if(!sendMessage(obtainMessage())) {
                        throw new RuntimeException("Could not send handler message");
                    }
                    rescheduled=true;
                    return;
                }
            }

        }catch (Exception e){

        }finally {
           handlerActive=rescheduled;
        }
    }

    @Override
    public void enqueue(ActionPost actionPost) {
        synchronized (this) {
            queue.enqueue(actionPost);
            if (!handlerActive) {
                handlerActive = true;
                if (!sendMessage(obtainMessage())) {
                    throw new RuntimeException("Could not send handler message");
                }
            }
        }

    }
}
