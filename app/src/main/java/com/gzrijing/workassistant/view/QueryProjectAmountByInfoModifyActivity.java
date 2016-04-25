package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.ReportInfoProjectAmountByWaitAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.QueryProjectAmount;
import com.gzrijing.workassistant.entity.Supplies;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.service.ReportProjectAmountService;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class QueryProjectAmountByInfoModifyActivity extends BaseActivity implements View.OnClickListener {

    private String userNo;
    private TextView tv_checkName;
    private TextView tv_checkTime;
    private TextView tv_feeType;
    private EditText et_content;
    private EditText et_civil;
    private ListView lv_supplies;
    private int index;
    private String orderId;
    private QueryProjectAmount projectAmount;
    private Handler handler = new Handler();
    private ArrayList<Supplies> suppliesList = new ArrayList<Supplies>();
    private ReportInfoProjectAmountByWaitAdapter adapter;
    private ProgressDialog pDialog;
    private Button btn_edit;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_project_amount_by_info_modify);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        type = intent.getStringExtra("type");
        projectAmount = intent.getParcelableExtra("projectAmount");

        if (projectAmount.getFeeType().equals("水务")) {
            index = 1;
        }

        IntentFilter intentFilter = new IntentFilter("action.com.gzrijing.workassistant.ReportProjectAmountFragment");
        registerReceiver(mBroadcastReceiver, intentFilter);

        getQueryProjectAmountSupplies();
    }

    private void getQueryProjectAmountSupplies() {
        String url = "?cmd=getsomeinstallconfirmdetail&togetherid=&confirmid=" + projectAmount.getId() + "&fileno=";
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<Supplies> list = JsonParseUtils.getProjectAmountReportInfoSupplies(response);
                        suppliesList.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(QueryProjectAmountByInfoModifyActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_checkName = (TextView) findViewById(R.id.query_project_amount_by_info_modify_check_name_tv);
        tv_checkName.setText(projectAmount.getApprovalName());
        tv_checkTime = (TextView) findViewById(R.id.query_project_amount_by_info_modify_check_time_tv);
        tv_checkTime.setText(projectAmount.getApprovalTime());
        tv_feeType = (TextView) findViewById(R.id.query_project_amount_by_info_modify_fee_type_tv);
        tv_feeType.setText(projectAmount.getFeeType());
        et_content = (EditText) findViewById(R.id.query_project_amount_by_info_modify_content_et);
        et_content.setText(projectAmount.getContent());
        et_civil = (EditText) findViewById(R.id.query_project_amount_by_info_modify_civil_et);
        et_civil.setText(projectAmount.getCivil());

        btn_edit = (Button) findViewById(R.id.query_project_amount_by_info_modify_edit_supplies_btn);

        lv_supplies = (ListView) findViewById(R.id.query_project_amount_by_info_modify_supplies_lv);
        adapter = new ReportInfoProjectAmountByWaitAdapter(this, suppliesList);
        lv_supplies.setAdapter(adapter);
    }

    private void setListeners() {
        tv_feeType.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.query_project_amount_by_info_modify_fee_type_tv:
                selectFeeType();
                break;

            case R.id.query_project_amount_by_info_modify_edit_supplies_btn:
                Intent intent = new Intent(this, ProjectAmountAddSuppliesActivity.class);
                intent.putParcelableArrayListExtra("suppliesList", suppliesList);
                intent.putExtra("type", type);
                startActivityForResult(intent, 10);
                break;
        }
    }

    private void selectFeeType() {
        final int flag = index;
        new AlertDialog.Builder(this).setTitle("选择归属类型：").setSingleChoiceItems(
                new String[]{"客户", "水务"}, index, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = which;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (index == 0) {
                            tv_feeType.setText("客户");
                        }
                        if (index == 1) {
                            tv_feeType.setText("水务");
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = flag;
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == 10) {
                List<Supplies> supplies = data.getParcelableArrayListExtra("suppliesList");
                suppliesList.clear();
                suppliesList.addAll(supplies);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_query_project_amount_by_info_modify, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_apply) {
            apply();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void apply() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在重新申请...");
        pDialog.show();
        Intent intent = new Intent(this, ReportProjectAmountService.class);
        intent.putExtra("userNo", userNo);
        intent.putExtra("orderId", orderId);
        intent.putExtra("type", tv_feeType.getText().toString());
        intent.putExtra("content", et_content.getText().toString().trim());
        intent.putExtra("civil", et_civil.getText().toString().trim());
        intent.putParcelableArrayListExtra("suppliesList", suppliesList);
        intent.putExtra("flag", "1");  //0=施工员审核通过，不通知组长   1=保存状态，通知组长审核
        intent.putExtra("id", projectAmount.getId());
        startService(intent);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.com.gzrijing.workassistant.ReportProjectAmountFragment")) {
                String result = intent.getStringExtra("result");
                ToastUtil.showToast(context, result, Toast.LENGTH_SHORT);
                pDialog.cancel();
                if (result.equals("汇报成功")) {
                    Intent intent1 = new Intent("action.com.gzrijing.workassistant.QueryProjectAmount");
                    sendBroadcast(intent1);
                    finish();
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
