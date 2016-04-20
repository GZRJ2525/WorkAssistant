package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.SuppliesApprovalAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.Supplies;
import com.gzrijing.workassistant.entity.SuppliesNo;

import java.util.ArrayList;

public class BusinessLeaderByMyOrderSuppliesApprovalActivity extends BaseActivity {

    private ListView lv_list;
    private ArrayList<Supplies> suppliesList = new ArrayList<Supplies>();
    private SuppliesApprovalAdapter adapter;
    private SuppliesNo suppliesNo;
    private TextView tv_useTime;
    private TextView tv_remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_leader_by_my_order_supplies_approval);

        initData();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        suppliesNo = (SuppliesNo) intent.getParcelableExtra("suppliesNo");
        suppliesList.addAll(suppliesNo.getSuppliesList());
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_useTime = (TextView) findViewById(R.id.supplies_approval_use_time_tv);
        tv_useTime.setText(suppliesNo.getUseTime());
        tv_remark = (TextView) findViewById(R.id.supplies_approval_remark_tv);
        tv_remark.setText(suppliesNo.getRemarks());

        lv_list = (ListView) findViewById(R.id.supplies_approval_lv);
        adapter = new SuppliesApprovalAdapter(this, suppliesList);
        lv_list.setAdapter(adapter);
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
