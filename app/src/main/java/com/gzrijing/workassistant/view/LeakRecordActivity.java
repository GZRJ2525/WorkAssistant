package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JudgeDate;
import com.gzrijing.workassistant.util.ToastUtil;
import com.gzrijing.workassistant.widget.selectdate.ScreenInfo;
import com.gzrijing.workassistant.widget.selectdate.WheelMain;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LeakRecordActivity extends BaseActivity implements View.OnClickListener {

    private String orderId;
    private String userNo;
    private TextView tv_findDate;
    private TextView tv_billDate;
    private TextView tv_endDate;
    private EditText et_caliber;
    private EditText et_spend;
    private EditText et_reason;
    private EditText et_supplies;
    private WheelMain wheelMain;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Handler handler = new Handler();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak_record);

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

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_findDate = (TextView) findViewById(R.id.leak_record_find_date_tv);
        tv_billDate = (TextView) findViewById(R.id.leak_record_bill_date_tv);
        tv_endDate = (TextView) findViewById(R.id.leak_record_end_date_tv);
        et_caliber = (EditText) findViewById(R.id.leak_record_caliber_et);
        et_spend = (EditText) findViewById(R.id.leak_record_spend_et);
        et_reason = (EditText) findViewById(R.id.leak_record_reason_et);
        et_supplies = (EditText) findViewById(R.id.leak_record_supplies_et);
    }

    private void setListeners() {
        tv_findDate.setOnClickListener(this);
        tv_billDate.setOnClickListener(this);
        tv_endDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leak_record_find_date_tv:
                getDate(tv_findDate);
                break;

            case R.id.leak_record_bill_date_tv:
                getDate(tv_billDate);
                break;

            case R.id.leak_record_end_date_tv:
                getDate(tv_endDate);
                break;
        }
    }

    private void getDate(final TextView tv_value) {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View timepickerview = inflater.inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(this);
        wheelMain = new WheelMain(timepickerview, true);
        wheelMain.screenheight = screenInfo.getHeight();
        String time = tv_value.getText().toString();
        Calendar calendar = Calendar.getInstance();
        if (JudgeDate.isDate(time, "yyyy-MM-dd HH:mm")) {
            try {
                calendar.setTime(dateFormat.parse(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        int h = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        wheelMain.initDateTimePicker(y, m - 1, d, h, min);
        new AlertDialog.Builder(this)
                .setTitle("选择时间")
                .setView(timepickerview)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_value.setText(wheelMain.getTime());
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_leak_record, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_submit) {
            submit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void submit() {
        if (tv_findDate.getText().toString().equals("")) {
            ToastUtil.showToast(this, "请选择发现日期后，再提交", Toast.LENGTH_SHORT);
            return;
        }
        if (tv_billDate.getText().toString().equals("")) {
            ToastUtil.showToast(this, "请选择发单日期后，再提交", Toast.LENGTH_SHORT);
            return;
        }
        if (tv_endDate.getText().toString().equals("")) {
            ToastUtil.showToast(this, "请选择止水日期后，再提交", Toast.LENGTH_SHORT);
            return;
        }
        if (et_caliber.getText().toString().trim().equals("")) {
            ToastUtil.showToast(this, "请填写口径，再提交", Toast.LENGTH_SHORT);
            return;
        }
        if (et_spend.getText().toString().trim().equals("")) {
            ToastUtil.showToast(this, "请填写耗时，再提交", Toast.LENGTH_SHORT);
            return;
        }
        if (et_reason.getText().toString().trim().equals("")) {
            ToastUtil.showToast(this, "请填写漏水原因，再提交", Toast.LENGTH_SHORT);
            return;
        }

        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在登陆...");
        pDialog.show();
        final RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "dosavewaterleakage")
                .add("userno", userNo)
                .add("fileno", orderId)
                .add("finddate", tv_findDate.getText().toString())
                .add("sendbilldate", tv_billDate.getText().toString())
                .add("stopleakdate", tv_endDate.getText().toString())
                .add("drainsize", et_caliber.getText().toString().trim())
                .add("pipepart", et_supplies.getText().toString().trim())
                .add("leakreason", et_reason.getText().toString().trim())
                .add("leaktime", et_spend.getText().toString().trim())
                .build();
        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.dismiss();
                        if(response.equals("ok")){
                            ToastUtil.showToast(LeakRecordActivity.this, "提交成功", Toast.LENGTH_SHORT);
                            finish();
                        }else{
                            ToastUtil.showToast(LeakRecordActivity.this, "提交失败", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(LeakRecordActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                });
            }
        });
    }
}
