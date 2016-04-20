package com.gzrijing.workassistant.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.GridViewImageForReportInfoAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.entity.ReportInfo;
import com.gzrijing.workassistant.service.DownLoadProblemImageService;
import com.gzrijing.workassistant.util.JsonParseUtils;

import java.util.ArrayList;

public class ReportInfoProblemActivity extends BaseActivity {

    private TextView tv_describe;
    private TextView tv_reportor;
    private TextView tv_reportTime;
    private GridView gv_image;
    private ArrayList<PicUrl> picUrls = new ArrayList<PicUrl>();
    private GridViewImageForReportInfoAdapter adapter;
    private Intent imageService;
    private ReportInfo info;
    private TextView tv_handleName;
    private TextView tv_handleResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info_problem);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        Intent intent = getIntent();

        info = intent.getParcelableExtra("problemInfo");

        initImageList();

    }

    private void initImageList() {
        gv_image = (GridView) findViewById(R.id.report_info_problem_image_gv);
        IntentFilter intentFilter = new IntentFilter("action.com.gzrijing.workassistant.ReportInfoProblem");
        registerReceiver(mBroadcastReceiver, intentFilter);
        imageService = new Intent(this, DownLoadProblemImageService.class);
        imageService.putExtra("id", info.getId());
        imageService.putExtra("fileNo", info.getFileNo());
        startService(imageService);
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_reportor = (TextView) findViewById(R.id.report_info_problem_reportor_tv);
        tv_reportor.setText(info.getReportor());
        tv_reportTime = (TextView) findViewById(R.id.report_info_problem_report_time_tv);
        tv_reportTime.setText(info.getReportTime());
        tv_describe = (TextView) findViewById(R.id.report_info_problem_describe_tv);
        tv_describe.setText(info.getContent());
        tv_handleName = (TextView) findViewById(R.id.report_info_problem_handle_name_tv);
        tv_handleName.setText(info.getHandleName());
        tv_handleResult = (TextView) findViewById(R.id.report_info_problem_handle_result_tv);
        tv_handleResult.setText(info.getHandleResult());

    }

    private void setListeners() {

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("action.com.gzrijing.workassistant.ReportInfoProblem")){
                String response = intent.getStringExtra("response");
                picUrls = JsonParseUtils.getImageUrl(response);
                adapter = new GridViewImageForReportInfoAdapter(ReportInfoProblemActivity.this, picUrls);
                gv_image.setAdapter(adapter);
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
        stopService(imageService);
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
