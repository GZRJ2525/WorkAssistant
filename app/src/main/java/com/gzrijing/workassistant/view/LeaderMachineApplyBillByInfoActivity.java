package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.LeaderMachineApplyBillByInfoAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.LeaderMachineApplyBill;

public class LeaderMachineApplyBillByInfoActivity extends BaseActivity {

    private TextView tv_billNo;
    private TextView tv_orderId;
    private TextView tv_useAddress;
    private TextView tv_useDate;
    private TextView tv_finishDate;
    private TextView tv_applyName;
    private TextView tv_applyDate;
    private TextView tv_remark;
    private ListView lv_machineList;
    private LeaderMachineApplyBill bill;
    private LeaderMachineApplyBillByInfoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_machine_apply_bill_by_info);

        initData();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        bill = intent.getParcelableExtra("bill");

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_billNo = (TextView) findViewById(R.id.leader_machine_apply_bill_by_info_bill_no_tv);
        tv_billNo.setText(bill.getBillNo());
        tv_orderId = (TextView) findViewById(R.id.leader_machine_apply_bill_by_info_order_id_tv);
        tv_orderId.setText(bill.getOrderId());
        tv_useAddress = (TextView) findViewById(R.id.leader_machine_apply_bill_by_info_use_address_tv);
        tv_useAddress.setText(bill.getUseAddress());
        tv_useDate = (TextView) findViewById(R.id.leader_machine_apply_bill_by_info_use_date_tv);
        tv_useDate.setText(bill.getUseDate());
        tv_finishDate = (TextView) findViewById(R.id.leader_machine_apply_bill_by_info_finish_date_tv);
        tv_finishDate.setText(bill.getFinishDate());
        tv_applyName = (TextView) findViewById(R.id.leader_machine_apply_bill_by_info_apply_name_tv);
        tv_applyName.setText(bill.getApplyName());
        tv_applyDate = (TextView) findViewById(R.id.leader_machine_apply_bill_by_info_apply_date_tv);
        tv_applyDate.setText(bill.getApplyDate());
        tv_remark = (TextView) findViewById(R.id.leader_machine_apply_bill_by_info_remark_tv);
        tv_remark.setText(bill.getRemark());

        lv_machineList = (ListView) findViewById(R.id.leader_machine_apply_bill_by_info_machine_lv);
        adapter = new LeaderMachineApplyBillByInfoAdapter(this, bill.getMachineList());
        lv_machineList.setAdapter(adapter);
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
