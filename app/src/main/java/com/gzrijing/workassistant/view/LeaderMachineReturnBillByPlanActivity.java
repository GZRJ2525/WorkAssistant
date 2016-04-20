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
import com.gzrijing.workassistant.adapter.LeaderMachineReturnBillByPlanAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.LeaderMachineReturnBill;

public class LeaderMachineReturnBillByPlanActivity extends BaseActivity {

    private LeaderMachineReturnBill bill;
    private ListView lv_machineList;
    private LeaderMachineReturnBillByPlanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_machine_return_bill_by_plan);

        initData();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        bill = intent.getParcelableExtra("bill");

        IntentFilter intentFilter = new IntentFilter("action.com.gzrijing.workassistant.LeaderMachineReturnBillByPlan");
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_machineList = (ListView) findViewById(R.id.leader_machine_return_bill_by_plan_machine_lv);
        adapter = new LeaderMachineReturnBillByPlanAdapter(this, bill);
        lv_machineList.setAdapter(adapter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("action.com.gzrijing.workassistant.LeaderMachineReturnBillByPlan")){
                int machinePosition = intent.getIntExtra("machinePosition", -1);
                bill.getMachineList().get(machinePosition).setFlag("1");
                adapter.notifyDataSetChanged();
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
