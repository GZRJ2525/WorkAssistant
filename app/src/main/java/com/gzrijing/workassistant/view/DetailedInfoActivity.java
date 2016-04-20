package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.DetailedInfoAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.db.BusinessData;
import com.gzrijing.workassistant.db.DetailedInfoData;
import com.gzrijing.workassistant.db.ImageData;
import com.gzrijing.workassistant.entity.DetailedInfo;
import com.gzrijing.workassistant.entity.PicUrl;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class DetailedInfoActivity extends BaseActivity {

    private String userNo;
    private String orderId;
    private BusinessData businessData;
    private ListView lv_info;
    private List<DetailedInfo> infos = new ArrayList<DetailedInfo>();
    private ArrayList<PicUrl> picUrls = new ArrayList<PicUrl>();
    private DetailedInfoAdapter adapter;
    private String recordFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_info);

        initData();
        initViews();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        int id = intent.getIntExtra("id", -1);


        businessData = DataSupport.find(BusinessData.class, id, true);
        List<DetailedInfoData> detailedDatas = businessData.getDetailedInfoList();
        for (DetailedInfoData data : detailedDatas) {
            DetailedInfo info = new DetailedInfo();
            info.setKey(data.getKey());
            info.setValue(data.getValue());
            infos.add(info);
        }
        List<ImageData> imageDatas = businessData.getImageDataList();
        for (ImageData data : imageDatas) {
            PicUrl picUrl = new PicUrl();
            picUrl.setPicUrl(data.getUrl());
            picUrls.add(picUrl);
        }

        recordFileName = businessData.getRecordFileName();
        if (recordFileName != null && !recordFileName.equals("")) {
            DetailedInfo info = new DetailedInfo();
            info.setKey("录音文件：");
            info.setValue("录音附件点击查看." + recordFileName.split("\\.")[1]);
            infos.add(info);
        }

        if (picUrls.size() > 0) {
            DetailedInfo info = new DetailedInfo();
            infos.add(info);
        }

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_info = (ListView) findViewById(R.id.detailed_info_lv);
        adapter = new DetailedInfoAdapter(this, infos, picUrls, recordFileName, userNo, orderId);
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
