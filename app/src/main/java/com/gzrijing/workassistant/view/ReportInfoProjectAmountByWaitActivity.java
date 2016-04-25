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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.ReportInfoProjectAmountByWaitAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.ReportInfoProjectAmount;
import com.gzrijing.workassistant.entity.Supplies;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.service.GetReportInfoProjectAmountSuppliesService;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReportInfoProjectAmountByWaitActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_feeType;
    private TextView tv_reportName;
    private TextView tv_reportTime;
    private EditText et_content;
    private EditText et_civil;
    private ListView lv_supplies;
    private LinearLayout ll_yes;
    private ImageView iv_yes;
    private LinearLayout ll_no;
    private ImageView iv_no;
    private Button btn_submit;
    private String userNo;
    private ReportInfoProjectAmount info;
    private boolean isCheck = true;
    private Intent serviceIntent;
    private ArrayList<Supplies> suppliesList = new ArrayList<Supplies>();
    private ReportInfoProjectAmountByWaitAdapter suppliesAdapter;
    private ProgressDialog pDialog;
    private Handler handler = new Handler();
    private String orderId;
    private int index = 0;
    private Button btn_edit;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info_project_amount_by_wait);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        Intent intent = getIntent();
        String togetherid = intent.getStringExtra("id");
        info = intent.getParcelableExtra("projectAmount");
        orderId = intent.getStringExtra("orderId");
        type = intent.getStringExtra("type");

        if(info.getFeeType().equals("水务")){
            index = 1;
        }

        IntentFilter intentFilter = new IntentFilter("action.com.gzrijing.workassistant.ReportInfoProjectAmount");
        registerReceiver(mBroadcastReceiver, intentFilter);

        serviceIntent = new Intent(this, GetReportInfoProjectAmountSuppliesService.class);
        serviceIntent.putExtra("togetherid", togetherid);
        serviceIntent.putExtra("confirmid", info.getId());
        startService(serviceIntent);
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_feeType = (TextView) findViewById(R.id.report_info_project_amount_by_wait_fee_type_tv);
        tv_feeType.setText(info.getFeeType());
        tv_reportName = (TextView) findViewById(R.id.report_info_project_amount_by_wait_report_name_tv);
        tv_reportName.setText(info.getReportName());
        tv_reportTime = (TextView) findViewById(R.id.report_info_project_amount_by_wait_report_time_tv);
        tv_reportTime.setText(info.getReportDate());
        et_content = (EditText) findViewById(R.id.report_info_project_amount_by_wait_content_et);
        et_content.setText(info.getContent());
        et_civil = (EditText) findViewById(R.id.report_info_project_amount_by_wait_civil_et);
        et_civil.setText(info.getCivil());

        btn_edit = (Button) findViewById(R.id.report_info_project_amount_by_wait_edit_supplies_btn);

        lv_supplies = (ListView) findViewById(R.id.report_info_project_amount_by_wait_supplies_lv);
        suppliesAdapter = new ReportInfoProjectAmountByWaitAdapter(this, suppliesList);
        lv_supplies.setAdapter(suppliesAdapter);

        ll_yes = (LinearLayout) findViewById(R.id.report_info_project_amount_by_wait_yes_ll);
        iv_yes = (ImageView) findViewById(R.id.report_info_project_amount_by_wait_yes_iv);
        ll_no = (LinearLayout) findViewById(R.id.report_info_project_amount_by_wait_no_ll);
        iv_no = (ImageView) findViewById(R.id.report_info_project_amount_by_wait_no_iv);

        btn_submit = (Button) findViewById(R.id.report_info_project_amount_by_wait_submit_btn);
    }

    private void setListeners() {
        tv_feeType.setOnClickListener(this);
        ll_yes.setOnClickListener(this);
        ll_no.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.report_info_project_amount_by_wait_fee_type_tv:
                selectFeeType();
                break;

            case R.id.report_info_project_amount_by_wait_yes_ll:
                if (!isCheck) {
                    iv_yes.setImageResource(R.drawable.spinner_item_check_on);
                    iv_no.setImageResource(R.drawable.spinner_item_check_off);
                    isCheck = !isCheck;
                }
                break;

            case R.id.report_info_project_amount_by_wait_no_ll:
                if (isCheck) {
                    iv_no.setImageResource(R.drawable.spinner_item_check_on);
                    iv_yes.setImageResource(R.drawable.spinner_item_check_off);
                    isCheck = !isCheck;
                }
                break;

            case R.id.report_info_project_amount_by_wait_submit_btn:
                submit();
                break;

            case R.id.report_info_project_amount_by_wait_edit_supplies_btn:
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
                        if(index == 0) {
                            tv_feeType.setText("客户");
                        }
                        if(index == 1) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == 10) {
                List<Supplies> supplies = data.getParcelableArrayListExtra("suppliesList");
                suppliesList.clear();
                suppliesList.addAll(supplies);
                suppliesAdapter.notifyDataSetChanged();
            }
        }
    }

    private void submit() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("提交中...");
        pDialog.show();
        String isPass;
        if (isCheck) {
            isPass = "1";
        } else {
            isPass = "0";
        }

        JSONArray jsonArray = new JSONArray();
        for (Supplies supplies : suppliesList) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("MakingNo", supplies.getId());
                jsonObject.put("Qty", supplies.getNum());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "docheckconsconfirm")
                .add("userno", userNo)
                .add("id", info.getId())
                .add("fileno", orderId)
                .add("conscontent", et_content.getText().toString().trim())
                .add("earthworkcontent", et_civil.getText().toString().trim())
                .add("receivables", tv_feeType.getText().toString())
                .add("isPass", isPass)
                .add("consmakingjson", jsonArray.toString())
                .build();

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response", response);
                if (response.equals("ok")) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(ReportInfoProjectAmountByWaitActivity.this, "提交成功", Toast.LENGTH_SHORT);
                            Intent intent = new Intent("action.com.gzrijing.workassistant.ReportInfo.projectAmount.refresh");
                            sendBroadcast(intent);
                            finish();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(ReportInfoProjectAmountByWaitActivity.this, "提交失败", Toast.LENGTH_SHORT);
                        }
                    });
                }
                pDialog.cancel();
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(ReportInfoProjectAmountByWaitActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
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
            if (action.equals("action.com.gzrijing.workassistant.ReportInfoProjectAmount")) {
                String jsonData = intent.getStringExtra("jsonData");
                List<Supplies> list = JsonParseUtils.getProjectAmountReportInfoSupplies(jsonData);
                suppliesList.addAll(list);
                suppliesAdapter.notifyDataSetChanged();
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
        stopService(serviceIntent);
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
