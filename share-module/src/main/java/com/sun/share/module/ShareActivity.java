package com.sun.share.module;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        String value = getIntent().getStringExtra("KEY");
        Log.e("TAG", "ShareActivity---------->"+value);
    }

    public void gotoMember(View view) {
    }
}
