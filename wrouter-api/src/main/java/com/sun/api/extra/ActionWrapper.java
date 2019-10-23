package com.sun.api.extra;

import com.sun.api.action.IRouterAction;
import com.sun.wrouter.base.ThreadMode;

/**
 * Copyright (C), 2016-2019
 * File: ActionWrapper.java
 * Author: wds_sun
 * Date: 2019-10-21 15:09
 * Description:
 */
public class ActionWrapper {

    private Class<? extends IRouterAction> actionClass;
    private String path;
    private ThreadMode threadMode;
    private boolean extraProcess;
    private IRouterAction routerAction;

    ActionWrapper() {

    }

    public void setRouterAction(IRouterAction routerAction) {
        this.routerAction = routerAction;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public IRouterAction getRouterAction() {
        return routerAction;
    }

    private ActionWrapper(Class<? extends IRouterAction> actionClass, String path, boolean extraProcess, ThreadMode threadMode) {
        this.actionClass = actionClass;
        this.path = path;
        this.extraProcess = extraProcess;
        this.threadMode = threadMode;
    }

    public Class<? extends IRouterAction> getActionClass() {
        return actionClass;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public String getPath() {
        return path;
    }

    public boolean isExtraProcess() {
        return extraProcess;
    }

    public static ActionWrapper build(Class<? extends IRouterAction> actionClass, String path, boolean extraProcess, ThreadMode threadMode) {
        return new ActionWrapper(actionClass, path, extraProcess, threadMode);
    }
}
