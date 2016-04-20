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
import com.gzrijing.workassistant.adapter.LeaderMachineApplyBillByPlanAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.LeaderMachineApplyBill;
import com.gzrijing.workassistant.entity.LeaderMachineApplyBillByMachine;


public class LeaderMachineApplyBillByPlanActivity extends BaseActivity {

    private LeaderMachineApplyBill bill;
    private ListView lv_machineList;
    private LeaderMachineApplyBillByPlanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_machine_apply_bill_by_plan);

        initData();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        bill = intent.getParcelableExtra("bill");

        IntentFilter intentFilter = new IntentFilter("action.com.gzrijing.workassistant.LeaderMachineApplyBillByPlan");
        registerReceiver(mBroadcastReceiver,intentFilter);
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_machineList = (ListView) findViewById(R.id.leader_machine_apply_bill_by_plan_machine_lv);
        adapter = new LeaderMachineApplyBillByPlanAdapter(this, bill);
        lv_machineList.setAdapter(adapter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("action.com.gzrijing.workassistant.LeaderMachineApplyBillByPlan")){
                String machineName = intent.getStringExtra("machineName");
                for(LeaderMachineApplyBillByMachine machine : bill.getMachineList()){
                    if(machine.getName().equals(machineName)){
                        int sendNum = Integer.valueOf(machine.getSendNum());
                        sendNum++;
                        machine.setSendNum(String.valueOf(sendNum));
                        adapter.notifyDataSetChanged();
                    }
                }
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
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
