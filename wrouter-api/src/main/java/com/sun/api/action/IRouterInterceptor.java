package com.sun.api.action;

import com.sun.api.interceptor.ActionInterceptor;

import java.util.List;


/**
 * Copyright (C), 2016-2019
 * File: IRouterAction.java
 * Author: wds_sun
 * Date: 2019-10-21 11:53
 * Description:
 */
public interface IRouterInterceptor {
    // 通过 Action 的名称找到 Action
    List<ActionInterceptor> getInterceptors();
}
