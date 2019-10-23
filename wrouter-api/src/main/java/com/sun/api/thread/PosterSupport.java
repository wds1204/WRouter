package com.sun.api.thread;

import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: PosterSupport.java
 * Author: wds_sun
 * Date: 2019-10-23 14:57
 * Description:
 */
public class PosterSupport {

    private static Poster mainPoster,backgroundPoster,asyncPoster;

    private final static ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newCachedThreadPool();


    public static ExecutorService getExecutorService() {
        return DEFAULT_EXECUTOR_SERVICE;
    }

    public static Poster getMainPoster(){
        if(mainPoster==null) {
            synchronized (Poster.class){
                if(mainPoster==null) {
                    mainPoster=new HandlerPoster(Looper.getMainLooper(),10);
                }
            }
        }

        return mainPoster;
    }

    public static Poster getBackgroundPoster() {

        if(backgroundPoster==null) {
            synchronized (Poster.class){
                if(backgroundPoster==null) {
                    backgroundPoster=new BackgroundPoster();
                }
            }
        }

        return backgroundPoster;
    }

    public static Poster getAsyncPoster() {
        if(asyncPoster==null) {
            synchronized (Poster.class){
                if(asyncPoster==null) {
                    asyncPoster=new AsyncPoster();
                }
            }
        }

        return asyncPoster;
    }
}
