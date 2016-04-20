package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.SafetyInspectFailAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.SafetyInspectFail;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SafetyInspectFailActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_orderId;
    private Button btn_query;
    private ListView lv_failList;
    private ProgressDialog pDialog;
    private ArrayList<SafetyInspectFail> failList = new ArrayList<SafetyInspectFail>();
    private SafetyInspectFailAdapter adapter;
    private Handler handler = new Handler();
    private String userNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_inspect_fail);

        initViews();
        initData();
        setListeners();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        query();
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_orderId = (EditText) findViewById(R.id.safety_inspect_fail_order_id_et);
        btn_query = (Button) findViewById(R.id.safety_inspect_fail_query_btn);

        lv_failList = (ListView) findViewById(R.id.safety_inspect_fail_lv);
        adapter = new SafetyInspectFailAdapter(this, failList);
        lv_failList.setAdapter(adapter);
    }

    private void setListeners() {
        btn_query.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.safety_inspect_fail_query_btn:
                query();
                break;
        }
    }

    private void query() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在加载数据");
        pDialog.show();
        String orderId = et_orderId.getText().toString().trim();
        String url = null;
        try {
            url = "?cmd=getprosafeerrorlist&fileno=" + URLEncoder.encode(orderId, "UTF-8") +
                    "&userno=" + URLEncoder.encode(userNo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                ArrayList<SafetyInspectFail> list = JsonParseUtils.getSafetyInspectFail(response);
                failList.clear();
                failList.addAll(list);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                pDialog.dismiss();
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(SafetyInspectFailActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
                pDialog.dismiss();
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
