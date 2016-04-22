package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.LeaderMachineApplyBillByMachineStateAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.LeaderMachineApplyBill;
import com.gzrijing.workassistant.entity.LeaderMachineState;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class LeaderMachineApplyBillByMachineStateActivity extends BaseActivity {

    private LeaderMachineApplyBill bill;
    private ListView lv_machineState;
    private ProgressDialog pDialog;
    private ArrayList<LeaderMachineState> machineList = new ArrayList<LeaderMachineState>();
    private LeaderMachineApplyBillByMachineStateAdapter adapter;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_machine_apply_bill_by_machine_state);

        initData();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        bill = intent.getParcelableExtra("bill");
        String machineName = intent.getStringExtra("machineName");

        getMachineState(machineName);

        IntentFilter intentFilter = new IntentFilter("action.com.gzrijing.workassistant.LeaderMachineApplyBillByPlan");
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void getMachineState(String machineName) {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在加载数据");
        pDialog.show();
        String url = null;
        try {
            url = "?cmd=getmachinelist&machineno=&machinename=" + URLEncoder.encode(machineName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response",response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<LeaderMachineState> list = JsonParseUtils.getLeaderMachineState(response);//yycq 获取机械台账列表
                        machineList.addAll(list);
                        adapter = new LeaderMachineApplyBillByMachineStateAdapter(LeaderMachineApplyBillByMachineStateActivity.this,
                                machineList, bill);
                        lv_machineState.setAdapter(adapter);
                    }
                });
                pDialog.cancel();
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(LeaderMachineApplyBillByMachineStateActivity.this,
                                "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
        pDialog.cancel();
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_machineState = (ListView) findViewById(R.id.leader_machine_apply_bill_by_machine_state_lv);

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("action.com.gzrijing.workassistant.LeaderMachineApplyBillByPlan")){
                finish();
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
