package com.sun.wrouter.base.annotation;

import com.sun.wrouter.base.ThreadMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright (C)
 * File: Action.java
 * Author: wds_sun
 * Date: 2019-10-21 10:14
 * Description:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Action {
    /**
     * thread mode
     * @return
     */
    ThreadMode threadMode() default ThreadMode.POSTING;

    /**
     * pathc of route
     * @return
     */
    String path();

    /**
     * extraProcess
     * @return
     */
    boolean extraProcess() default false;

}
