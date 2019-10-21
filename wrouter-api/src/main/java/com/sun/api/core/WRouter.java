package com.sun.api.core;

import android.app.Application;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.sun.api.action.IRouterAction;
import com.sun.api.action.IRouterModule;
import com.sun.api.exception.InitException;
import com.sun.api.extra.ActionWrapper;
import com.sun.api.extra.Consts;
import com.sun.api.extra.ErrorActionWrapper;
import com.sun.api.utils.ClassUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C), 2016-2019
 * File: WRouter.java
 * Author: wds_sun
 * Date: 2019-10-21 11:31
 * Description:
 */
public class WRouter {
    private volatile static WRouter instance;
    private volatile boolean hasInit = false;
    private Application mApplicationContext;
    // 缓存的 RouterAction
    private volatile static Map<String, ActionWrapper> cacheRouterActions = new HashMap();

    // 缓存的 RouterModule
    private volatile static Map<String, IRouterModule> cacheRouterModules = new HashMap();


    private static List<String> mAllModuleClassName;

    private WRouter() {

    }

    public static WRouter getInstance() {
        if (instance == null) {
            synchronized (WRouter.class) {
                if (instance == null) {
                    instance = new WRouter();
                }
            }
        }
        return instance;
    }


    public void init(Application context) {
        if (hasInit) {
            throw new InitException("WRouter alreadly initialized,It can only be initialized once");
        }
        hasInit = true;

        this.mApplicationContext = context;

        //获取com.wrouter.assist.module包名下的所有类信息

        try {
            mAllModuleClassName = ClassUtils.getFileNameByPackageName(context, Consts.ROUTER_MODULE_PACK_NAME);

            Log.e("TAG", "mAllModuleClassName------->" + mAllModuleClassName.toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public RouterForward action(String actionName) {
        if (!actionName.contains("/")) {
            String message = "action name  format error -> <" + actionName + ">, like: moduleName/actionName";
//            debugMessage(message);
            return new RouterForward(new ErrorActionWrapper());
        }

        //获取moduleName实例化 并缓存
        String moduleName = actionName.split("/")[0];
        String moduleClassName = searchModuleClassName(moduleName);
        if (TextUtils.isEmpty(moduleClassName)) {
            String message = String.format("Please check to the action name is correct: according to the <%s> cannot find module %s.", actionName, moduleName);
//            debugMessage(message);
            return new RouterForward(new ErrorActionWrapper());
        }
        IRouterModule iRouterModule = cacheRouterModules.get(moduleClassName);
        if (iRouterModule == null) {
            try {
                Class<? extends IRouterModule> moduleClass = (Class<? extends IRouterModule>) Class.forName(moduleClassName);
                iRouterModule = moduleClass.newInstance();
                cacheRouterModules.put(moduleClassName, iRouterModule);

            } catch (Exception e) {
                e.printStackTrace();
                String message = "instance module error: " + e.getMessage();
//                debugMessage(message);
                return new RouterForward(new ErrorActionWrapper());
            }
        }

        //从Module中获取ActionWrapper 类 , 缓存 ActionWrapper
        ActionWrapper actionWrapper = cacheRouterActions.get(actionName);
        if (actionWrapper == null) {
            actionWrapper = iRouterModule.findAction(actionName);
        } else {
            return new RouterForward(actionWrapper);
        }
        if (actionWrapper == null) {
            String message = String.format("Please check to the action name is correct: according to the <%s> cannot find action.", actionName);
//            debugMessage(message);
            return new RouterForward(new ErrorActionWrapper());
        }
        Class<? extends IRouterAction> actionClass = actionWrapper.getActionClass();
        IRouterAction routerAction = actionWrapper.getRouterAction();
        if (routerAction == null) {
            if (!IRouterAction.class.isAssignableFrom(actionClass)) {
                String message = actionClass.getCanonicalName() + " must be implements IRouterAction.";
//                debugMessage(message);
                return new RouterForward(new ErrorActionWrapper());
            }
            try {
                routerAction = actionClass.newInstance();

                actionWrapper.setRouterAction(routerAction);
                cacheRouterActions.put(actionName,actionWrapper);

            } catch (Exception e) {
                String message = "instance action error: " + e.getMessage();
//                debugMessage(message);
                return new RouterForward(new ErrorActionWrapper());
            }

        }

        return new RouterForward(actionWrapper);
    }

    private String searchModuleClassName(String moduleName) {
        for (String moduleClassName : mAllModuleClassName) {
            if (moduleClassName.contains(moduleName)) {
                return moduleClassName;
            }

        }
        return null;
    }


}
