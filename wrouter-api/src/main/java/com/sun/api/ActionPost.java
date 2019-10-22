package com.sun.api;

import android.content.Context;

import com.sun.api.extra.ActionWrapper;
import com.sun.api.result.ActionCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: ActionPost.java
 * Author: wds_sun
 * Date: 2019-10-22 10:51
 * Description:
 */
public class ActionPost {
    private final static List<ActionPost> pendingPostPool = new ArrayList<ActionPost>();

    public ActionWrapper actionWrapper;
    public Context context;
    public Map<String, Object> params;

    public ActionPost next;
    public ActionCallback callback;

    public ActionPost(ActionWrapper mActionWrapper, Context mContext, Map<String, Object> mParams, ActionCallback callback) {
        this.context = mContext;
        this.actionWrapper = mActionWrapper;
        this.params = mParams;
        this.callback = callback;
    }

    public static ActionPost obtainActionPost(ActionWrapper mActionWrapper, Context mContext, Map<String, Object> mParams, ActionCallback callback) {
        synchronized (pendingPostPool) {
            int size = pendingPostPool.size();
            if (size > 0) {
                ActionPost actionPost = pendingPostPool.remove(size - 1);
                actionPost.context = mContext;
                actionPost.actionWrapper = mActionWrapper;
                actionPost.params = mParams;
                actionPost.next = null;
                actionPost.callback = callback;
                return actionPost;
            }
        }
        return new ActionPost(mActionWrapper, mContext, mParams, callback);
    }
}
