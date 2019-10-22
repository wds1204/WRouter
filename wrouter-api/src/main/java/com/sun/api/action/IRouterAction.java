package com.sun.api.action;

import android.content.Context;

import com.sun.api.result.RouterResult;

import java.util.Map;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: IRouterAction.java
 * Author: wds_sun
 * Date: 2019-10-21 11:53
 * Description:
 */
public interface IRouterAction {
    RouterResult invokeAction(Context context, Map<String,Object> requestData);
}
