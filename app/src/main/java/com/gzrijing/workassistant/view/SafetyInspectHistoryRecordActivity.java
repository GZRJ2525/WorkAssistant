package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.GridViewImageForReportInfoAdapter;
import com.gzrijing.workassistant.adapter.SafetyInspectHistoryRecordAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.entity.SafetyInspectHistoryRecord;
import com.gzrijing.workassistant.entity.SafetyInspectHistoryRecordFailItem;

import java.util.ArrayList;

public class SafetyInspectHistoryRecordActivity extends BaseActivity {

    private SafetyInspectHistoryRecord form;
    private TextView tv_submitTime;
    private TextView tv_situation;
    private TextView tv_process;
    private ListView lv_failure;
    private GridView gv_image;
    private ArrayList<SafetyInspectHistoryRecordFailItem> failureList = new ArrayList<SafetyInspectHistoryRecordFailItem>();
    private ArrayList<PicUrl> picUrls = new ArrayList<PicUrl>();
    private SafetyInspectHistoryRecordAdapter failureAdapter;
    private GridViewImageForReportInfoAdapter picUrlsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_inspect_history_record);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        Intent intent = getIntent();
        form = intent.getParcelableExtra("SafetyInspectHistoryRecord");
        failureList.addAll(form.getFailure());
        picUrls.addAll(form.getPicUrls());
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_submitTime = (TextView) findViewById(R.id.safety_inspect_history_record_submit_time_tv);
        tv_submitTime.setText(form.getSubmitDate());
        tv_situation = (TextView) findViewById(R.id.safety_inspect_history_record_situation_tv);
        tv_situation.setText(form.getSituation());
        tv_process = (TextView) findViewById(R.id.safety_inspect_history_record_process_tv);
        tv_process.setText(form.getProcess());

        lv_failure = (ListView) findViewById(R.id.safety_inspect_history_record_failure_lv);
        failureAdapter = new SafetyInspectHistoryRecordAdapter(SafetyInspectHistoryRecordActivity.this, failureList);
        lv_failure.setAdapter(failureAdapter);

        gv_image = (GridView) findViewById(R.id.safety_inspect_history_record_image_gv);
        picUrlsAdapter = new GridViewImageForReportInfoAdapter(SafetyInspectHistoryRecordActivity.this, picUrls);
        gv_image.setAdapter(picUrlsAdapter);
    }

    private void setListeners() {
        gv_image.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (picUrls.size() > 0) {
                    Intent intent = new Intent(SafetyInspectHistoryRecordActivity.this, ImageBrowserForHttpActivity.class);
                    intent.putExtra("position", position);
                    intent.putParcelableArrayListExtra("picUrls", picUrls);
                    startActivity(intent);
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
