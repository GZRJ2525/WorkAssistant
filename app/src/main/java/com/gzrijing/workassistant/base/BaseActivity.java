package com.gzrijing.workassistant.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gzrijing.workassistant.util.ActivityManagerUtil;


public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManagerUtil.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManagerUtil.removeActivity(this);
    }

}
