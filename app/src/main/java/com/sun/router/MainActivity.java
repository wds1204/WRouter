package com.sun.router;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sun.api.core.WRouter;
import com.sun.api.result.ActionCallback;
import com.sun.api.result.RouterResult;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void gotoMember(final View view) {



        new Thread(){
            public void run(){
                WRouter.getInstance().action("member/path").context(view.getContext()).invokeAction(new ActionCallback() {
                    @Override
                    public void onInterrupt() {
                        Log.e("TAG", "拦截了");
                    }

                    @Override
                    public void onResult(RouterResult result) {
//                Log.e("TAG", "result"+result.toString());

                    }
                });
            }
        }.start();
    }

    public void gotoShare(View view) {

        WRouter.getInstance().action("share/path").context(this).param("KEY","value").invokeAction();
    }
}
