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
import com.gzrijing.workassistant.adapter.ReportInfoProjectAmountAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.ReportInfoProjectAmount;
import com.gzrijing.workassistant.service.GetReportInfoProjectAmountService;
import com.gzrijing.workassistant.util.JsonParseUtils;

import java.util.ArrayList;
import java.util.List;

public class ReportInfoByProjectAmountActivity extends BaseActivity {

    private String togetherid;
    private Intent projectAmountIntent;
    private ListView lv_wait;
    private ListView lv_ok;
    private List<ReportInfoProjectAmount> waitList = new ArrayList<ReportInfoProjectAmount>();
    private List<ReportInfoProjectAmount> okList = new ArrayList<ReportInfoProjectAmount>();
    private ReportInfoProjectAmountAdapter waitAdapter;
    private ReportInfoProjectAmountAdapter okAdapter;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info_by_project_amount);

        initData();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        togetherid = intent.getStringExtra("id");
        orderId = intent.getStringExtra("orderId");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.com.gzrijing.workassistant.ReportInfo.projectAmount");
        intentFilter.addAction("action.com.gzrijing.workassistant.ReportInfo.projectAmount.refresh");
        registerReceiver(mBroadcastReceiver, intentFilter);

        initProjectAmountReportInfo();
    }

    private void initProjectAmountReportInfo() {
        projectAmountIntent = new Intent(this, GetReportInfoProjectAmountService.class);
        projectAmountIntent.putExtra("id", togetherid);
        startService(projectAmountIntent);
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_wait = (ListView) findViewById(R.id.report_info_by_project_amount_wait_lv);
        waitAdapter = new ReportInfoProjectAmountAdapter(
                ReportInfoByProjectAmountActivity.this, waitList, togetherid, orderId);
        lv_wait.setAdapter(waitAdapter);

        lv_ok = (ListView) findViewById(R.id.report_info_by_project_amount_ok_lv);
        okAdapter = new ReportInfoProjectAmountAdapter(
                ReportInfoByProjectAmountActivity.this, okList, togetherid, orderId);
        lv_ok.setAdapter(okAdapter);

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.com.gzrijing.workassistant.ReportInfo.projectAmount")) {
                String jsonData = intent.getStringExtra("jsonData");
                List<ReportInfoProjectAmount> list = JsonParseUtils.getProjectAmountReportInfo(jsonData);
                for(ReportInfoProjectAmount info : list){
                    if(info.getState().equals("未审核")){
                        waitList.add(info);
                    }
                    if(info.getState().equals("已审核")){
                        okList.add(info);
                    }
                }
                waitAdapter.notifyDataSetChanged();
                okAdapter.notifyDataSetChanged();
            }

            if (action.equals("action.com.gzrijing.workassistant.ReportInfo.projectAmount.refresh")) {
                waitList.clear();
                okList.clear();
                initProjectAmountReportInfo();
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
        stopService(projectAmountIntent);
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
