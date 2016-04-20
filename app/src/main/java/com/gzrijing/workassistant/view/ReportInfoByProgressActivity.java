package com.gzrijing.workassistant.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.ReportInfoByProgressAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.ReportInfo;
import com.gzrijing.workassistant.service.GetReportInfoProgressService;
import com.gzrijing.workassistant.util.JsonParseUtils;

import java.util.ArrayList;
import java.util.List;

public class ReportInfoByProgressActivity extends BaseActivity {

    private String togetherid;
    private Intent progressIntent;
    private ListView lv_progress;
    private List<ReportInfo> progressList = new ArrayList<ReportInfo>();
    private ReportInfoByProgressAdapter progressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info_by_progress);

        initData();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        togetherid = intent.getStringExtra("id");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.com.gzrijing.workassistant.ReportInfo.progress");
        intentFilter.addAction("action.com.gzrijing.workassistant.ReportInfo.progress.refresh");
        registerReceiver(mBroadcastReceiver, intentFilter);

        initProgressReportInfo();
    }

    private void initProgressReportInfo() {
        progressIntent = new Intent(this, GetReportInfoProgressService.class);
        progressIntent.putExtra("id", togetherid);
        startService(progressIntent);
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_progress = (ListView) findViewById(R.id.report_info_by_progress_lv);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("action.com.gzrijing.workassistant.ReportInfo.progress")){
                String jsonData = intent.getStringExtra("jsonData");
                List<ReportInfo> infos = JsonParseUtils.getProgressReportInfo(jsonData);
                progressList.addAll(infos);
                progressAdapter = new ReportInfoByProgressAdapter(ReportInfoByProgressActivity.this, progressList);
                lv_progress.setAdapter(progressAdapter);
            }

            if(action.equals("action.com.gzrijing.workassistant.ReportInfo.progress.refresh")){
                progressList.clear();
                initProgressReportInfo();
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        stopService(progressIntent);
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
