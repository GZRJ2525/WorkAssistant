package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.GridViewImageForReportInfoAdapter;
import com.gzrijing.workassistant.adapter.SafetyInspectHistoryRecordAdapter;
import com.gzrijing.workassistant.adapter.SafetyInspectHistoryRecordItemAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.SafetyInspectHistoryRecord;
import com.gzrijing.workassistant.entity.SafetyInspectHistoryRecordFailItem;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SafetyInspectHistoryRecordItemActivity extends BaseActivity {

    private String orderId;
    private ArrayList<SafetyInspectHistoryRecord> failureList = new ArrayList<SafetyInspectHistoryRecord>();
    private Handler handler = new Handler();
    private ListView lv_item;
    private SafetyInspectHistoryRecordItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_inspect_history_record_item);

        initViews();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");

        getHistoryRecord();
    }

    private void getHistoryRecord() {
        String url = null;
        try {
            url = "?cmd=GetSafeOldInf&fileid="+ URLEncoder.encode(orderId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<SafetyInspectHistoryRecord> list = JsonParseUtils.getSafetyInspectHistoryRecord(response);
                        for (SafetyInspectHistoryRecord form : list) {
                            if (form.getFlag().equals("1")) {
                                failureList.add(form);
                            }
                        }
                        adapter = new SafetyInspectHistoryRecordItemAdapter(SafetyInspectHistoryRecordItemActivity.this, failureList);
                        lv_item.setAdapter(adapter);
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(SafetyInspectHistoryRecordItemActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_item = (ListView) findViewById(R.id.safety_inspect_history_record_item_lv);
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
