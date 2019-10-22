package com.sun.api.utils;

import com.sun.api.interceptor.ActionInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: MapUtils.java
 * Author: wds_sun
 * Date: 2019-10-21 19:44
 * Description:
 */
public class MapUtils {

    public static List<ActionInterceptor> getInterceptorClasses(Map<Integer, ActionInterceptor> map) {
        List<ActionInterceptor> list = new ArrayList();

        for (Object key : map.keySet()) {
            list.add(map.get(key));
        }

        return list;
    }
}
