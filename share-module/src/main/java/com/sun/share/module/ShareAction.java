package com.sun.share.module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sun.api.result.RouterResult;
import com.sun.api.action.IRouterAction;
import com.sun.wrouter.base.annotation.Action;

import java.util.Map;

/**
 * Copyright (C), 2016-2019, 未来酒店
 * File: ShareAction.java
 * Author: wds_sun
 * Date: 2019-10-21 18:04
 * Description:
 */
@Action(path = "share/path")
public class ShareAction implements IRouterAction {
    @Override
    public RouterResult invokeAction(Context context, Map<String, Object> requestData) {

        Intent intent = new Intent(context,ShareActivity.class);
        intent.putExtra("KEY", (String) requestData.get("KEY"));
//        context.startActivity(intent);
        ((Activity) context).startActivityForResult(intent,10);
        return null;
    }
}
