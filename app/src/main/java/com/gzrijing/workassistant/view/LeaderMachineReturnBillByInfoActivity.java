package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.LeaderMachineReturnBillByInfoAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.LeaderMachineReturnBill;

public class LeaderMachineReturnBillByInfoActivity extends BaseActivity {

    private LeaderMachineReturnBill bill;
    private TextView tv_billNo;
    private TextView tv_orderId;
    private TextView tv_billType;
    private TextView tv_returnAddress;
    private TextView tv_returnDate;
    private TextView tv_applyName;
    private TextView tv_applyDate;
    private TextView tv_remark;
    private ListView lv_machineList;
    private LeaderMachineReturnBillByInfoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_machine_return_bill_by_info);

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

        tv_billNo = (TextView) findViewById(R.id.leader_machine_return_bill_by_info_bill_no_tv);
        tv_billNo.setText(bill.getBillNo());
        tv_orderId = (TextView) findViewById(R.id.leader_machine_return_bill_by_info_order_id_tv);
        tv_orderId.setText(bill.getOrderId());
        tv_billType = (TextView) findViewById(R.id.leader_machine_return_bill_by_info_type_tv);
        tv_billType.setText(bill.getBillType());
        tv_returnAddress = (TextView) findViewById(R.id.leader_machine_return_bill_by_info_return_address_tv);
        tv_returnAddress.setText(bill.getReturnAddress());
        tv_returnDate = (TextView) findViewById(R.id.leader_machine_return_bill_by_info_return_date_tv);
        tv_returnDate.setText(bill.getReturnDate());
        tv_applyName = (TextView) findViewById(R.id.leader_machine_return_bill_by_info_apply_name_tv);
        tv_applyName.setText(bill.getApplyName());
        tv_applyDate = (TextView) findViewById(R.id.leader_machine_return_bill_by_info_apply_date_tv);
        tv_applyDate.setText(bill.getApplyDate());
        tv_remark = (TextView) findViewById(R.id.leader_machine_return_bill_by_info_remark_tv);
        tv_remark.setText(bill.getRemark());

        lv_machineList = (ListView) findViewById(R.id.leader_machine_return_bill_by_info_machine_lv);
        adapter = new LeaderMachineReturnBillByInfoAdapter(this, bill.getMachineList());
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
