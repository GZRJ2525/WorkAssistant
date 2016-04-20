package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.MachineApplyingAdapter;
import com.gzrijing.workassistant.adapter.MachineReturnAdapter;
import com.gzrijing.workassistant.adapter.MachineReturnEditReceivedAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.db.MachineData;
import com.gzrijing.workassistant.entity.Machine;
import com.gzrijing.workassistant.entity.MachineNo;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MachineReturnActivity extends BaseActivity {

    private MachineNo machineNo;
    private ArrayList<Machine> machineList = new ArrayList<Machine>();
    private TextView tv_time;
    private TextView tv_address;
    private TextView tv_remark;
    private ListView lv_list;
    private MachineReturnAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_return);

        initData();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        machineNo = (MachineNo) intent.getParcelableExtra("machineNo");
        List<MachineData> machineDataList = DataSupport.where("returnId = ?", machineNo.getReturnId()).find(MachineData.class);
        for (MachineData data : machineDataList) {
            Machine machine = new Machine();
            machine.setId(data.getNo());
            machine.setName(data.getName());
            machine.setUnit(data.getUnit());
            machine.setApplyNum(data.getApplyNum());
            machineList.add(machine);
        }
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_time = (TextView) findViewById(R.id.machine_return_time_tv);
        tv_time.setText(machineNo.getReturnTime());
        tv_address = (TextView) findViewById(R.id.machine_return_address_tv);
        tv_address.setText(machineNo.getReturnAddress());
        tv_remark = (TextView) findViewById(R.id.machine_return_remark_tv);
        tv_remark.setText(machineNo.getRemarks());

        lv_list = (ListView) findViewById(R.id.machine_return_lv);
        adapter = new MachineReturnAdapter(this, machineList);
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
