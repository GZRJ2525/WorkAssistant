package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.InspectionStationDetailedInfoAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.DetailedInfo;
import com.gzrijing.workassistant.entity.PicUrl;

import java.util.ArrayList;

public class InspectionStationDetailedInfoActivity extends BaseActivity {

    private String userNo;
    private String orderId;
    private ListView lv_info;
    private ArrayList<DetailedInfo> infos;
    private ArrayList<PicUrl> picUrls;
    private InspectionStationDetailedInfoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_station_detailed_info);

        initData();
        initViews();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        infos = intent.getParcelableArrayListExtra("detailedInfos");
        picUrls = intent.getParcelableArrayListExtra("picUrls");

        if (picUrls.size() > 0) {
            DetailedInfo info = new DetailedInfo();
            infos.add(info);
        }

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_info = (ListView) findViewById(R.id.inspection_station_detailed_info_lv);
        adapter = new InspectionStationDetailedInfoAdapter(this, infos, picUrls, userNo, orderId);
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
