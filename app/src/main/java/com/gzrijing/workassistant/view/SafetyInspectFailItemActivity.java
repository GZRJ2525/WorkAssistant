package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.SafetyInspectFailItemAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.SafetyInspectFailItem;
import com.gzrijing.workassistant.entity.SubordinateLocation;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SafetyInspectFailItemActivity extends BaseActivity {

    private ListView lv_failItem;
    private String userNo;
    private Handler handler = new Handler();
    private ArrayList<SubordinateLocation> workerList = new ArrayList<SubordinateLocation>();
    private ProgressDialog pDialog;
    private String orderId;
    private ArrayList<SafetyInspectFailItem> failItemList = new ArrayList<SafetyInspectFailItem>();
    private SafetyInspectFailItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_inspect_fail_item);

        initViews();
        initData();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");

        getWorker();
    }

    private void getWorker() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在加载数据");
        pDialog.show();
        String url = null;
        try {//卢工接口39. 获取被派工岗位的人员列表  返回：(UserNo=员工编号；UserName=员工名称)
            url = "?cmd=getsitusergl&userno=" + URLEncoder.encode(userNo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response", response);
                ArrayList<SubordinateLocation> list = JsonParseUtils.getSubordinateLocationInfo(response);//下属位置
                workerList.addAll(list);
                getFailItem();
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(SafetyInspectFailItemActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                });
            }
        });
    }

    private void getFailItem() {
        String url = null;
        try {//贺工接口20.获取工程已经保存的安全信息
            url = "?cmd=LoadSafeItemSaved&fileid="+URLEncoder.encode(orderId, "UTF-8");
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
                        ArrayList<SafetyInspectFailItem> list = JsonParseUtils.getSafetyInspectFailItem(response);
                        failItemList.addAll(list);
                        adapter = new SafetyInspectFailItemAdapter(
                                SafetyInspectFailItemActivity.this, failItemList, workerList);
                        lv_failItem.setAdapter(adapter);
                        pDialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(SafetyInspectFailItemActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                });
            }
        });
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_failItem = (ListView) findViewById(R.id.safety_inspect_fail_item_lv);


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
