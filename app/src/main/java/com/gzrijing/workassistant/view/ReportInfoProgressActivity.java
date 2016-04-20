package com.gzrijing.workassistant.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.GridViewImageForReportInfoAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.service.DownLoadProgressImageService;
import com.gzrijing.workassistant.util.JsonParseUtils;

import java.util.ArrayList;

public class ReportInfoProgressActivity extends BaseActivity {

    private TextView tv_content;
    private TextView tv_reportor;
    private TextView tv_reportTime;
    private String id;
    private String fileNo;
    private String reportor;
    private String reportTime;
    private String content;
    private GridView gv_image;
    private ArrayList<PicUrl> picUrls = new ArrayList<PicUrl>();
    private GridViewImageForReportInfoAdapter adapter;
    private Intent imageService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info_progress);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        fileNo = intent.getStringExtra("fileNo");
        reportor = intent.getStringExtra("reportor");
        reportTime = intent.getStringExtra("reportTime");
        content = intent.getStringExtra("content");

        initImageList();
    }

    private void initImageList() {
        IntentFilter intentFilter = new IntentFilter("action.com.gzrijing.workassistant.ReportInfoProgress");
        registerReceiver(mBroadcastReceiver, intentFilter);
        imageService = new Intent(this, DownLoadProgressImageService.class);
        imageService.putExtra("id", id);
        imageService.putExtra("fileNo", fileNo);
        startService(imageService);
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_reportor = (TextView) findViewById(R.id.report_info_progress_reportor_tv);
        tv_reportor.setText(reportor);
        tv_reportTime = (TextView) findViewById(R.id.report_info_progress_report_time_tv);
        tv_reportTime.setText(reportTime);
        tv_content = (TextView) findViewById(R.id.report_info_progress_content_tv);
        tv_content.setText(content);

        gv_image = (GridView) findViewById(R.id.report_info_progress_image_gv);
    }

    private void setListeners() {

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("action.com.gzrijing.workassistant.ReportInfoProgress")){
                String response = intent.getStringExtra("response");
                picUrls = JsonParseUtils.getImageUrl(response);
                adapter = new GridViewImageForReportInfoAdapter(ReportInfoProgressActivity.this, picUrls);
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
