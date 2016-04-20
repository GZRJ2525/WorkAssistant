package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.SuppliesAdapter;
import com.gzrijing.workassistant.adapter.SuppliesReceivedAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.Supplies;
import com.gzrijing.workassistant.entity.SuppliesNo;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.gzrijing.workassistant.widget.MyListView;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class BusinessLeaderByMyOrderSuppliesReturnEditActivity extends BaseActivity implements View.OnClickListener {

    private String userNo;
    private String orderId;
    private ImageView iv_delAll;
    private MyListView lv_return;
    private ArrayList<Supplies> returnList = new ArrayList<Supplies>();
    private ArrayList<Supplies> receivedList = new ArrayList<Supplies>();
    private MyListView lv_received;
    private SuppliesReceivedAdapter receivedAdapter;
    private SuppliesAdapter returnAdapter;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_leader_by_my_order_supplies_return_edit);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        Intent intent = getIntent();
        userNo = intent.getStringExtra("userNo");
        orderId = intent.getStringExtra("orderId");
        ArrayList<SuppliesNo> suppliesNoList = intent.getParcelableArrayListExtra("suppliesNoList");
        for (SuppliesNo suppliesNo : suppliesNoList) {
            receivedList.addAll(suppliesNo.getSuppliesList());
        }
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iv_delAll = (ImageView) findViewById(R.id.supplies_return_edit_del_all_iv);

        lv_return = (MyListView) findViewById(R.id.supplies_return_edit_return_lv);
        returnAdapter = new SuppliesAdapter(this, returnList);
        lv_return.setAdapter(returnAdapter);

        lv_received = (MyListView) findViewById(R.id.supplies_return_edit_received_lv);
        receivedAdapter = new SuppliesReceivedAdapter(this, receivedList);
        lv_received.setAdapter(receivedAdapter);
    }

    private void setListeners() {
        iv_delAll.setOnClickListener(this);

        lv_received.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Supplies received = receivedList.get(position);
                Supplies supplies = new Supplies();
                supplies.setId(received.getId());
                supplies.setName(received.getName());
                supplies.setSpec(received.getSpec());
                supplies.setUnit(received.getUnit());
                supplies.setApplyNum("1");
                returnList.add(supplies);
                returnAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.supplies_return_edit_del_all_iv:
                returnList.clear();
                returnAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_supplies_return_edit, menu);
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
        JSONArray jsonArray = new JSONArray();
        for(Supplies supplies : returnList){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("MakingNo", supplies.getId());
                jsonObject.put("MakingName", supplies.getName());
                jsonObject.put("MakingSpace", supplies.getSpec());
                jsonObject.put("MakingUnit", supplies.getUnit());
                jsonObject.put("Qty", supplies.getApplyNum());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.e("json", jsonArray.toString());
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "dosavematerialneedmain")
                .add("mainid", "")
                .add("fileno", orderId)
                .add("billtype", "工程退料")
                .add("usedatetime", "")
                .add("remark", "")
                .add("userno", userNo)
                .add("makingjson", jsonArray.toString())
                .build();

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                if(response.substring(0, 1).equals("E")){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(BusinessLeaderByMyOrderSuppliesReturnEditActivity.this, "申请失败", Toast.LENGTH_SHORT);
                        }
                    });
                }else{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            SuppliesNo suppliesNo = savaSuppliesNo(response);
                            Intent intent = getIntent();
                            intent.putExtra("suppliesNo", (Parcelable)suppliesNo);
                            setResult(30, intent);
                            finish();
                        }
                    });

                }
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(BusinessLeaderByMyOrderSuppliesReturnEditActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private SuppliesNo savaSuppliesNo(String returnId){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar rightNow = Calendar.getInstance();
        String returnTime = dateFormat.format(rightNow.getTime());

        for (int i = 0; i < returnList.size(); i++) {
            Supplies supplies = new Supplies();
            supplies.setId(returnList.get(i).getId());
            supplies.setName(returnList.get(i).getName());
            supplies.setSpec(returnList.get(i).getSpec());
            supplies.setUnit(returnList.get(i).getUnit());
            supplies.setApplyNum(returnList.get(i).getApplyNum());
        }

        SuppliesNo suppliesNo = new SuppliesNo();
        suppliesNo.setReturnId(returnId);
        suppliesNo.setReturnTime(returnTime);
        suppliesNo.setReturnState("申请中");

        return suppliesNo;
    }
}
