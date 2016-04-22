package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.LeaderMachineApplyBillListAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.LeaderMachineApplyBill;
import com.gzrijing.workassistant.entity.LeaderMachineApplyBillByMachine;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.DateUtil;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class LeaderMachineApplyBillListActivity extends BaseActivity implements View.OnClickListener {

    private String userNo;
    private EditText et_id;
    private Button btn_query;
    private ListView lv_bill;
    private ArrayList<LeaderMachineApplyBill> billList = new ArrayList<LeaderMachineApplyBill>();
    private LeaderMachineApplyBillListAdapter adapter;
    private ProgressDialog pDialog;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_machine_apply_bill_list);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.com.gzrijing.workassistant.LeaderMachineApplyBillByPlan");
        intentFilter.addAction("action.com.gzrijing.workassistant.LeaderMachineApplyBill");
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_id = (EditText) findViewById(R.id.leader_machine_apply_bill_list_id_et);
        btn_query = (Button) findViewById(R.id.leader_machine_apply_bill_list_query_btn);

        lv_bill = (ListView) findViewById(R.id.leader_machine_apply_bill_list_lv);
        adapter = new LeaderMachineApplyBillListAdapter(LeaderMachineApplyBillListActivity.this, billList, userNo);
        lv_bill.setAdapter(adapter);

        getApplyBill();

    }

    private void setListeners() {
        btn_query.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leader_machine_apply_bill_list_query_btn:
                getApplyBill();
                break;
        }
    }

    private void getApplyBill() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在加载数据");
        pDialog.show();
        String id = et_id.getText().toString().trim();
        String url = null;
        try {
            url = "?cmd=getmachineneedandbacklist&userno=" + URLEncoder.encode(userNo, "UTF-8")
                    + "&savedate=&billno=" + URLEncoder.encode(id, "UTF-8")
                    + "&fileno=&billtype=" + URLEncoder.encode("申请", "UTF-8") + "&isallappoint=";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            pDialog.show();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response", response);
                ArrayList<LeaderMachineApplyBill> list = JsonParseUtils.getLeaderMachineApplyBill(response);
                billList.clear();
                billList.addAll(list);
                if (billList.size() > 1) {
                    Collections.sort(billList, new Comparator<LeaderMachineApplyBill>() {
                        @Override
                        public int compare(LeaderMachineApplyBill lhs, LeaderMachineApplyBill rhs) {
                            Date date1 = DateUtil.stringToDate(lhs.getApplyDate());
                            Date date2 = DateUtil.stringToDate(rhs.getApplyDate());
                            // 对日期字段进行升序，如果欲降序可采用after方法
                            if (date1.before(date2)) {
                                return 1;
                            }
                            return -1;
                        }
                    });
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                pDialog.cancel();
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(LeaderMachineApplyBillListActivity.this,
                                "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
                pDialog.cancel();
            }
        });
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.com.gzrijing.workassistant.LeaderMachineApplyBillByPlan")) {
                String machineName = intent.getStringExtra("machineName");
                String billNo = intent.getStringExtra("billNo");
                for (LeaderMachineApplyBill bill : billList) {
                    if (bill.getBillNo().equals(billNo)) {
                        for (LeaderMachineApplyBillByMachine machine : bill.getMachineList()) {
                            if (machine.getName().equals(machineName)) {
                                int sendNum = Integer.valueOf(machine.getSendNum());
                                sendNum++;
                                machine.setSendNum(String.valueOf(sendNum));
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            if (action.equals("action.com.gzrijing.workassistant.LeaderMachineApplyBill")) {
                getApplyBill();
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
