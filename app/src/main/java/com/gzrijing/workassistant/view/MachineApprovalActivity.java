package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.MachineApprovalAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.db.MachineData;
import com.gzrijing.workassistant.entity.Machine;
import com.gzrijing.workassistant.entity.MachineNo;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MachineApprovalActivity extends BaseActivity {

    private MachineNo machineNo;
    private ArrayList<Machine> machineList = new ArrayList<Machine>();
    private TextView tv_useTime;
    private TextView tv_returnTime;
    private TextView tv_useAddress;
    private TextView tv_remark;
    private ListView lv_list;
    private MachineApprovalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_approva);

        initData();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        machineNo = (MachineNo) intent.getParcelableExtra("machineNo");
        List<MachineData> machineDataList = DataSupport.where("applyId = ? and receivedState=?", machineNo.getApplyId(), "").find(MachineData.class);
        Log.e("size", machineDataList.size()+"");
        for (MachineData data : machineDataList) {
            Machine machine = new Machine();
            machine.setName(data.getName());
            machine.setUnit(data.getUnit());
            machine.setApplyNum(data.getApplyNum());
            machine.setSendNum(data.getSendNum());
            machineList.add(machine);
        }
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_useTime = (TextView) findViewById(R.id.machine_approval_use_time_tv);
        tv_useTime.setText(machineNo.getUseTime());
        tv_returnTime = (TextView) findViewById(R.id.machine_approval_return_time_tv);
        tv_returnTime.setText(machineNo.getReturnTime());
        tv_useAddress = (TextView) findViewById(R.id.machine_approval_use_address_tv);
        tv_useAddress.setText(machineNo.getUseAddress());
        tv_remark = (TextView) findViewById(R.id.machine_approval_remark_tv);
        tv_remark.setText(machineNo.getRemarks());

        lv_list = (ListView) findViewById(R.id.machine_approval_lv);
        adapter = new MachineApprovalAdapter(this, machineList);
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
