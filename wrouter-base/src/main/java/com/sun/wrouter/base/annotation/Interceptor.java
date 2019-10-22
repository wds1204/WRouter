package com.sun.wrouter.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: Interceptor.java
 * Author: wds_sun
 * Date: 2019-10-21 10:21
 * Description: 拦截
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Interceptor {
    int priority();
}
