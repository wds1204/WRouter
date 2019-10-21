package com.sun.api.core;

import android.content.Context;

import com.sun.api.extra.ActionWrapper;
import com.sun.api.extra.ErrorActionWrapper;
import com.sun.wrouter.base.ThreadMode;

import java.util.Map;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: RouterForward.java
 * Author: wds_sun
 * Date: 2019-10-21 16:04
 * Description:
 */
public class RouterForward {

    private ActionWrapper mActionWrapper;
    private Context mContext;
    private Map<String, Object> mParams;
    private ThreadMode mThreadMode = null;

    public RouterForward() {
    }

    public RouterForward(ActionWrapper actionWrapper) {
        this.mActionWrapper=actionWrapper;
    }

    /**
     *  执行 Action
     */
    public void invokeAction() {
            mActionWrapper.setThreadMode(getThreadMode());

            mActionWrapper.getRouterAction().invokeAction(mContext,mParams);
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
        this.mContext=context;
        return this;
    }
}
