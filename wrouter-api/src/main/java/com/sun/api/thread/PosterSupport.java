package com.sun.api.thread;

import android.os.Looper;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: PosterSupport.java
 * Author: wds_sun
 * Date: 2019-10-23 14:57
 * Description:
 */
public class PosterSupport {

    private static Poster mainPost;
    public static Poster getMainPoster(){
        if(mainPost==null) {
            synchronized (Poster.class){
                if(mainPost==null) {
                    mainPost=new HandlerPoster(Looper.getMainLooper(),10);
                }
            }
        }

        return mainPost;
    }
}
