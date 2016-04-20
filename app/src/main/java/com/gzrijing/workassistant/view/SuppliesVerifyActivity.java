package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.SuppliesVerifyAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.SuppliesVerify;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SuppliesVerifyActivity extends BaseActivity {

    private String userNo;
    private ListView lv_ok;
    private String orderId;
    private ArrayList<SuppliesVerify> okList = new ArrayList<SuppliesVerify>();
    private SuppliesVerifyAdapter okAdapter;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplies_verify);

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

        getSuppliesVerify();
    }

    private void getSuppliesVerify() {
        String url = null;
        try {
            url = "?cmd=getmaterialneedlist&userno="+ URLEncoder.encode(userNo, "UTF-8")+"&savedate=&mainid=&fileno="
                    + URLEncoder.encode(orderId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<SuppliesVerify> list = JsonParseUtils.getSuppliesVerify(response);
                        okList.clear();
                        okList.addAll(list);
                        okAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(SuppliesVerifyActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_ok = (ListView) findViewById(R.id.supplies_verify_ok);
        okAdapter = new SuppliesVerifyAdapter(this, okList);
        lv_ok.setAdapter(okAdapter);
    }

    private void setListeners() {
        lv_ok.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SuppliesVerifyActivity.this, SuppliesVerifyOkInfoActivity.class);
                intent.putParcelableArrayListExtra("suppliesVerifyInfoList", okList.get(position).getSuppliesVerifyInfoList());
                intent.putExtra("userTime", okList.get(position).getUseTime());
                intent.putExtra("remarks", okList.get(position).getRemarks());
                startActivity(intent);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
