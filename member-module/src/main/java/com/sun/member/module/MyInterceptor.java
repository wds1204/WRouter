package com.sun.member.module;

import android.content.Intent;
import android.util.Log;

import com.sun.api.ActionPost;
import com.sun.api.core.WRouter;
import com.sun.api.interceptor.ActionInterceptor;
import com.sun.wrouter.base.annotation.Interceptor;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: MyInterceptor.java
 * Author: wds_sun
 * Date: 2019-10-21 19:24
 * Description:
 */
@Interceptor(priority = 10)
public class MyInterceptor implements ActionInterceptor {

    @Override
    public void intercept(ActionChain chain) {
        ActionPost actionPost = chain.action();
        if(chain.actionPath().equals("member/action")) {
            WRouter.getInstance().action("share/path").context(actionPost.context).invokeAction();
            chain.onInterrupt();
        }
        // 继续向下转发
        chain.proceed(actionPost);

    }
}
