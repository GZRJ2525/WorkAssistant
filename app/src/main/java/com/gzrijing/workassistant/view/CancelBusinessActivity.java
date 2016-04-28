package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

public class CancelBusinessActivity extends BaseActivity implements View.OnClickListener{

    private String orderId;
    private String userNo;
    private EditText et_reason;
    private Button btn_submit;
    private ProgressDialog pDialog;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_business);

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

        et_reason = (EditText) findViewById(R.id.cancel_business_reason_et);
        btn_submit = (Button) findViewById(R.id.cancel_business_submit_btn);
    }

    private void setListeners() {
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_business_submit_btn:
                submit();
                break;
        }
    }

    private void submit() {
        if (et_reason.getText().toString().trim().equals("")) {
            ToastUtil.showToast(this, "请填写取消原因", Toast.LENGTH_SHORT);
            return;
        }
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在提交数据...");
        pDialog.show();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "dosaveconsaccidentfreedom")
                .add("userno", userNo)
                .add("fileno", orderId)
                .add("filestate", "取消")
                .add("accidentreason", "")
                .add("handleuno", "")
                .add("handlereason", et_reason.getText().toString().trim())
                .add("relationfileno", "")
                .add("appointinstallid", "")
                .build();
        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.substring(0, 1).equals("E")) {
                            ToastUtil.showToast(CancelBusinessActivity.this,
                                    "提交失败", Toast.LENGTH_SHORT);
                        } else {
                            Intent intent = new Intent("action.com.gzrijing.workassistant.InspectionStationFragment.cancel");
                            intent.putExtra("orderId", orderId);
                            sendBroadcast(intent);
                            ToastUtil.showToast(CancelBusinessActivity.this,
                                    "提交成功", Toast.LENGTH_SHORT);
                            finish();
                        }
                    }
                });
                pDialog.dismiss();
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(CancelBusinessActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                });
            }
        });
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
