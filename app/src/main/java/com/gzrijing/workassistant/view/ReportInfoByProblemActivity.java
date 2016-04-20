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
import com.gzrijing.workassistant.adapter.ReportInfoByProblemAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.BusinessHaveSend;
import com.gzrijing.workassistant.entity.ReportInfo;
import com.gzrijing.workassistant.service.GetReportInfoProblemService;
import com.gzrijing.workassistant.util.JsonParseUtils;

import java.util.ArrayList;
import java.util.List;

public class
ReportInfoByProblemActivity extends BaseActivity {

    private ListView lv_problem;
    private String togetherid;
    private Intent problemIntent;
    private ReportInfoByProblemAdapter problemAdapter;
    private ArrayList<BusinessHaveSend> BHSList;

    private List<ReportInfo> problemList = new ArrayList<ReportInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info_by_problem);

        initData();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        togetherid = intent.getStringExtra("id");
        BHSList = intent.getParcelableArrayListExtra("BHSList");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.com.gzrijing.workassistant.ReportInfo.problem");
        intentFilter.addAction("action.com.gzrijing.workassistant.ReportInfo.problem.refresh");
        registerReceiver(mBroadcastReceiver, intentFilter);

        initProblemReportInfo();
    }

    private void initProblemReportInfo() {
        problemIntent = new Intent(this, GetReportInfoProblemService.class);
        problemIntent.putExtra("id", togetherid);
        startService(problemIntent);

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_problem = (ListView) findViewById(R.id.report_info_by_problem_lv);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("action.com.gzrijing.workassistant.ReportInfo.problem")){
                String jsonData = intent.getStringExtra("jsonData");
                List<ReportInfo> infos = JsonParseUtils.getProblemReportInfo(jsonData);
                problemList.addAll(infos);
                problemAdapter = new ReportInfoByProblemAdapter(ReportInfoByProblemActivity.this, problemList, BHSList);
                lv_problem.setAdapter(problemAdapter);
            }

            if(action.equals("action.com.gzrijing.workassistant.ReportInfo.problem.refresh")){
                problemList.clear();
                initProblemReportInfo();
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
        stopService(problemIntent);
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
