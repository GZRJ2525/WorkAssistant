package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.SuppliesReceivedAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.db.SuppliesData;
import com.gzrijing.workassistant.entity.Supplies;
import com.gzrijing.workassistant.entity.SuppliesNo;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class SuppliesReceivedActivity extends BaseActivity {

    private ListView lv_list;
    private ArrayList<Supplies> suppliesList = new ArrayList<Supplies>();
    private SuppliesReceivedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplies_received);

        initData();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        SuppliesNo suppliesNo = (SuppliesNo) intent.getParcelableExtra("suppliesNo");
        List<SuppliesData> suppliesDataList = DataSupport.where("receivedId = ?", suppliesNo.getReceivedId()).find(SuppliesData.class);
        for (SuppliesData data : suppliesDataList) {
            Supplies supplies = new Supplies();
            supplies.setName(data.getName());
            supplies.setSpec(data.getSpec());
            supplies.setUnit(data.getUnit());
            supplies.setSendNum(data.getSendNum());
            suppliesList.add(supplies);
        }
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_list = (ListView) findViewById(R.id.supplies_received_lv);
        adapter = new SuppliesReceivedAdapter(this, suppliesList);
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
