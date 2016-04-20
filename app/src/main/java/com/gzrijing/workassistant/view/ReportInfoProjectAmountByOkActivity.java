package com.gzrijing.workassistant.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.ReportInfoProjectAmountByOkAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.ReportInfoProjectAmount;
import com.gzrijing.workassistant.entity.Supplies;
import com.gzrijing.workassistant.service.GetReportInfoProjectAmountSuppliesService;
import com.gzrijing.workassistant.util.JsonParseUtils;

import java.util.ArrayList;
import java.util.List;

public class ReportInfoProjectAmountByOkActivity extends BaseActivity {

    private ReportInfoProjectAmount info;
    private Intent serviceIntent;
    private TextView tv_reportName;
    private TextView tv_reportTime;
    private TextView tv_approvalTime;
    private TextView tv_feeType;
    private TextView tv_content;
    private TextView tv_civil;
    private ListView lv_supplies;
    private ArrayList<Supplies> suppliesList = new ArrayList<Supplies>();
    private ReportInfoProjectAmountByOkAdapter suppliesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info_project_amount_by_ok);

        initData();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        String togetherid = intent.getStringExtra("id");
        info = intent.getParcelableExtra("projectAmount");

        IntentFilter intentFilter = new IntentFilter("action.com.gzrijing.workassistant.ReportInfoProjectAmount");
        registerReceiver(mBroadcastReceiver, intentFilter);

        serviceIntent = new Intent(this, GetReportInfoProjectAmountSuppliesService.class);
        serviceIntent.putExtra("togetherid", togetherid);
        serviceIntent.putExtra("confirmid", info.getId());
        startService(serviceIntent);
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_reportName = (TextView) findViewById(R.id.report_info_project_amount_by_ok_report_name_tv);
        tv_reportName.setText(info.getReportName());
        tv_reportTime = (TextView) findViewById(R.id.report_info_project_amount_by_ok_report_time_tv);
        tv_reportTime.setText(info.getReportDate());
        tv_approvalTime = (TextView) findViewById(R.id.report_info_project_amount_by_ok_approval_time_tv);
        tv_approvalTime.setText(info.getCheckData());
        tv_feeType = (TextView) findViewById(R.id.report_info_project_amount_by_ok_fee_type_tv);
        tv_feeType.setText(info.getFeeType());
        tv_content = (TextView) findViewById(R.id.report_info_project_amount_by_ok_content_tv);
        tv_content.setText(info.getContent());
        tv_civil = (TextView) findViewById(R.id.report_info_project_amount_by_ok_civil_tv);
        tv_civil.setText(info.getCivil());

        lv_supplies = (ListView) findViewById(R.id.report_info_project_amount_by_ok_supplies_lv);
        suppliesAdapter = new ReportInfoProjectAmountByOkAdapter(this, suppliesList);
        lv_supplies.setAdapter(suppliesAdapter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.com.gzrijing.workassistant.ReportInfoProjectAmount")) {
                String jsonData = intent.getStringExtra("jsonData");
                List<Supplies> list = JsonParseUtils.getProjectAmountReportInfoSupplies(jsonData);
                suppliesList.addAll(list);
                suppliesAdapter.notifyDataSetChanged();
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
        stopService(serviceIntent);
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
