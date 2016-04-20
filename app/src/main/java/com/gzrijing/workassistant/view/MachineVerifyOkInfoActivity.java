package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.MachineVerifyOkAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.MachineVerify;
import com.gzrijing.workassistant.entity.MachineVerifyInfo;

import java.util.ArrayList;
import java.util.List;

public class MachineVerifyOkInfoActivity extends BaseActivity {

    private TextView tv_useTime;
    private TextView tv_reTime;
    private TextView tv_useAddress;
    private TextView tv_remarks;
    private List<MachineVerifyInfo> mvInfoList;
    private ListView lv_info;
    private MachineVerifyOkAdapter adapter;
    private String userTime;
    private String returnTime;
    private String useAdress;
    private String remarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_verify_ok_info);

        initData();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();

        mvInfoList = intent.getParcelableArrayListExtra("machineVerifyInfoList");
        userTime = intent.getStringExtra("userTime");
        returnTime = intent.getStringExtra("returnTime");
        useAdress = intent.getStringExtra("useAdress");
        remarks = intent.getStringExtra("remarks");

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_useTime = (TextView) findViewById(R.id.machine_verify_ok_info_use_time_tv);
        tv_useTime.setText(userTime);
        tv_reTime = (TextView) findViewById(R.id.machine_verify_ok_info_return_time_tv);
        tv_reTime.setText(returnTime);
        tv_useAddress = (TextView) findViewById(R.id.machine_verify_ok_info_use_address_tv);
        tv_useAddress.setText(useAdress);
        tv_remarks = (TextView) findViewById(R.id.machine_verify_ok_info_remarks_tv);
        tv_remarks.setText(remarks);

        lv_info = (ListView) findViewById(R.id.machine_verify_ok_info_lv);
        adapter = new MachineVerifyOkAdapter(this, mvInfoList);
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
