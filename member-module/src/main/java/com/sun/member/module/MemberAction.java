package com.sun.member.module;

import android.content.Context;
import android.content.Intent;

import com.sun.api.result.RouterResult;
import com.sun.api.action.IRouterAction;
import com.sun.wrouter.base.ThreadMode;
import com.sun.wrouter.base.annotation.Action;

import java.util.Map;

/**
 * Copyright (C), 2016-2019
 * File: MemberAction.java
 * Author: wds_sun
 * Date: 2019-10-21 13:59
 * Description:
 */
@Action(path = "member/path",threadMode=ThreadMode.MAIN)
public class MemberAction implements IRouterAction {
    @Override
    public RouterResult invokeAction(Context context, Map<String, Object> requestData) {

        context.startActivity(new Intent(context,MemberActivity.class));
        return null;
    }
}
