package com.gzrijing.workassistant.view;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.db.ServiceOpenAndClose;
import com.igexin.sdk.PushManager;

import org.litepal.crud.DataSupport;

public class SettingActivity extends BaseActivity {

    private Switch sw_push;
    private ServiceOpenAndClose serviceOpenAndClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        serviceOpenAndClose = DataSupport.find(ServiceOpenAndClose.class, 1);
        Log.e("11111", serviceOpenAndClose.getPushService());
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sw_push = (Switch) findViewById(R.id.setting_push_sw);
        if(serviceOpenAndClose.getPushService().equals("1")){
            sw_push.setChecked(true);
        }else{
            sw_push.setChecked(false);
        }
    }

    private void setListeners() {
        sw_push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    PushManager.getInstance().initialize(getApplicationContext());
                    ContentValues values = new ContentValues();
                    values.put("pushService", "1");
                    DataSupport.update(ServiceOpenAndClose.class, values, 1);
                }else{
                    PushManager.getInstance().stopService(getApplicationContext());
                    ContentValues values = new ContentValues();
                    values.put("pushService", "0");
                    DataSupport.update(ServiceOpenAndClose.class, values, 1);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
