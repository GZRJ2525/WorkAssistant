package com.gzrijing.workassistant.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.MachineVerifyAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.MachineVerify;
import com.gzrijing.workassistant.service.GetMachineVerifyService;
import com.gzrijing.workassistant.util.JsonParseUtils;

import java.util.ArrayList;

public class MachineVerifyActivity extends BaseActivity {

    private ListView lv_wait;
    private ListView lv_ok;
    private String orderId;
    private ArrayList<MachineVerify> waitList = new ArrayList<MachineVerify>();
    private ArrayList<MachineVerify> okList = new ArrayList<MachineVerify>();
    private MachineVerifyAdapter waitAdapter;
    private MachineVerifyAdapter okAdapter;
    private Intent serviceIntent;
    private String userNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_verify);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");

        getMachineVerify();
    }

    private void getMachineVerify() {
        IntentFilter intentFilter = new IntentFilter("action.com.gzrijing.workassistant.MachineVerify");
        registerReceiver(mBroadcastReceiver, intentFilter);
        serviceIntent = new Intent(this, GetMachineVerifyService.class);
        serviceIntent.putExtra("userNo", userNo);
        serviceIntent.putExtra("orderId", orderId);
        startService(serviceIntent);
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_wait = (ListView) findViewById(R.id.machine_verify_wait);
        waitAdapter = new MachineVerifyAdapter(this, waitList);
        lv_wait.setAdapter(waitAdapter);
        lv_ok = (ListView) findViewById(R.id.machine_verify_ok);
        okAdapter = new MachineVerifyAdapter(this, okList);
        lv_ok.setAdapter(okAdapter);
    }

    private void setListeners() {
        lv_wait.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MachineVerifyActivity.this, MachineVerifyOkInfoActivity.class);
                intent.putParcelableArrayListExtra("machineVerifyInfoList", waitList.get(position).getMachineVerifyInfoList());
                intent.putExtra("userTime", waitList.get(position).getUseTime());
                intent.putExtra("returnTime", waitList.get(position).getReturnTime());
                intent.putExtra("useAdress", waitList.get(position).getUseAdress());
                intent.putExtra("remarks", waitList.get(position).getRemarks());
                startActivity(intent);
            }
        });

        lv_ok.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MachineVerifyActivity.this, MachineVerifyOkInfoActivity.class);
                intent.putParcelableArrayListExtra("machineVerifyInfoList", okList.get(position).getMachineVerifyInfoList());
                intent.putExtra("userTime", okList.get(position).getUseTime());
                intent.putExtra("returnTime", okList.get(position).getReturnTime());
                intent.putExtra("useAdress", okList.get(position).getUseAdress());
                intent.putExtra("remarks", okList.get(position).getRemarks());
                startActivity(intent);
            }
        });
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("action.com.gzrijing.workassistant.MachineVerify")){
                String jsonData = intent.getStringExtra("jsonData");
                ArrayList<MachineVerify> machineVerifyList = JsonParseUtils.getMachineVerify(jsonData);
                for(MachineVerify machineVerify : machineVerifyList){
                    if(machineVerify.getState().equals("审核")){
                        okList.add(machineVerify);
                    }
                    if(machineVerify.getState().equals("保存")){
                        waitList.add(machineVerify);
                    }
                }
                okAdapter.notifyDataSetChanged();
                waitAdapter.notifyDataSetChanged();
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
