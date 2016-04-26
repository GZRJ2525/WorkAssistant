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
import com.gzrijing.workassistant.adapter.BusinessLeaderByMyOrderAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.BusinessByWorker;
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
import java.util.List;

public class BusinessLeaderByMyOrderActivity extends BaseActivity {

    private String orderId;
    private String userNo;
    private ListView lv_myOrder;
    private List<BusinessByWorker> orderList = new ArrayList<BusinessByWorker>();
    private BusinessLeaderByMyOrderAdapter adapter;
    private Handler handler = new Handler();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_leader_by_my_order);

        initData();
        initViews();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");

        getMyOrder();

    }

    private void getMyOrder() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在加载数据...");
        pDialog.show();
        String url = null;
        try {
            url = "?cmd=getmycons&userno="+ URLEncoder.encode(userNo, "UTF-8")
                    +"&fileno="+URLEncoder.encode(orderId, "UTF-8")+"&begindate=";
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
                        List<BusinessByWorker> list = JsonParseUtils.getWorkerBusiness(response);
                        orderList.addAll(list);
                        if (orderList.size() > 1) {
                            sequence(orderList);
                            ArrayList<BusinessByWorker> BBWList = new ArrayList<BusinessByWorker>();
                            for (int i = 0; i < orderList.size(); i++) {
                                if (orderList.get(i).getState().equals("已完工")) {
                                    BBWList.add(orderList.get(i));
                                    orderList.remove(i);
                                }
                            }
                            if (BBWList.size() > 0) {
                                orderList.addAll(BBWList);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        pDialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(BusinessLeaderByMyOrderActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
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

        lv_myOrder = (ListView) findViewById(R.id.business_leader_by_my_order_lv);
        adapter = new BusinessLeaderByMyOrderAdapter(this, orderList, userNo);
        lv_myOrder.setAdapter(adapter);
    }

    private void sequence(List<BusinessByWorker> orders) {
        Collections.sort(orders, new Comparator<BusinessByWorker>() {
            @Override
            public int compare(BusinessByWorker lhs, BusinessByWorker rhs) {
                Date date1 = DateUtil.stringToDate2(lhs.getReceivedTime());
                Date date2 = DateUtil.stringToDate2(rhs.getReceivedTime());
                // 对日期字段进行升序，如果欲降序可采用after方法
                if (date1.before(date2)) {
                    return 1;
                }
                return -1;
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
