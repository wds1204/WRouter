package com.sun.api.action;

import com.sun.api.extra.ActionWrapper;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: IRouterModule.java
 * Author: wds_sun
 * Date: 2019-10-21 14:32
 * Description:
 */
public interface IRouterModule {
    // 通过 Action 的名称找到 Action
    ActionWrapper findAction(String actionName);

}
