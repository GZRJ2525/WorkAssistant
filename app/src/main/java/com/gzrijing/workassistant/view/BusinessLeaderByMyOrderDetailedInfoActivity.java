package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.BusinessLeaderByMyOrderDetailedInfoAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.DetailedInfo;
import com.gzrijing.workassistant.entity.PicUrl;

import java.util.ArrayList;

public class BusinessLeaderByMyOrderDetailedInfoActivity extends BaseActivity {

    private ListView lv_info;
    private ArrayList<DetailedInfo> detailedInfos;
    private ArrayList<PicUrl> picUrlList = new ArrayList<PicUrl>();
    private BusinessLeaderByMyOrderDetailedInfoAdapter adapter;
    private String recordFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_leader_by_my_order_detailed_info);

        initData();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        detailedInfos = intent.getParcelableArrayListExtra("detailedInfo");
        ArrayList<PicUrl> picUrls = intent.getParcelableArrayListExtra("picUrlList");
        recordFileName = intent.getStringExtra("recordFileName");

        if (recordFileName != null && !recordFileName.equals("")) {
            DetailedInfo info = new DetailedInfo();
            info.setKey("录音文件：");
            info.setValue("录音附件点击查看." + recordFileName.split("\\.")[1]);
            detailedInfos.add(info);
        }

        for (PicUrl picUrl : picUrls) {
            String picPath = "/Pic/" + picUrl.getPicUrl();
            picUrl.setPicUrl(picPath);
            picUrlList.add(picUrl);
        }

        if (picUrlList.size() > 0) {
            DetailedInfo info = new DetailedInfo();
            detailedInfos.add(info);
        }

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_info = (ListView) findViewById(R.id.business_leader_by_my_order_detailed_info_lv);
        adapter = new BusinessLeaderByMyOrderDetailedInfoAdapter(this, detailedInfos, picUrlList, recordFileName);
        lv_info.setAdapter(adapter);
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
