package com.gzrijing.workassistant.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.SuppliesApplyingAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.Supplies;
import com.gzrijing.workassistant.entity.SuppliesNo;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JudgeDate;
import com.gzrijing.workassistant.util.ToastUtil;
import com.gzrijing.workassistant.widget.selectdate.ScreenInfo;
import com.gzrijing.workassistant.widget.selectdate.WheelMain;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class BusinessLeaderByMyOrderSuppliesApplyingActivity extends BaseActivity implements View.OnClickListener {
    private int position;
    private String userNo;
    private String orderId;
    private SuppliesNo suppliesNo;
    private ArrayList<Supplies> suppliesList = new ArrayList<Supplies>();
    private TextView tv_reason;
    private TextView tv_useTime;
    private EditText et_remarks;
    private Button btn_edit;
    private ListView lv_list;
    private WheelMain wheelMain;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private SuppliesApplyingAdapter adapter;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_leader_by_my_order_supplies_applying);

        initData();
        initViews();
        setListeners();

    }

    private void initData() {
        Intent intent = getIntent();
        userNo = intent.getStringExtra("userNo");
        orderId = intent.getStringExtra("orderId");
        position = intent.getIntExtra("position", -1);
        suppliesNo = intent.getParcelableExtra("suppliesNo");
        suppliesList.addAll(suppliesNo.getSuppliesList());
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_reason = (TextView) findViewById(R.id.supplies_applying_reason_tv);
        tv_reason.setText(suppliesNo.getReason());
        tv_useTime = (TextView) findViewById(R.id.supplies_applying_use_time_tv);
        tv_useTime.setText(suppliesNo.getUseTime());
        et_remarks = (EditText) findViewById(R.id.supplies_applying_remarks_et);
        et_remarks.setText(suppliesNo.getRemarks());
        btn_edit = (Button) findViewById(R.id.supplies_applying_edit_btn);

        lv_list = (ListView) findViewById(R.id.supplies_applying_lv);
        adapter = new SuppliesApplyingAdapter(this, suppliesList);
        lv_list.setAdapter(adapter);
    }

    private void setListeners() {
        tv_useTime.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.supplies_applying_use_time_tv:
                getUseTime();
                break;

            case R.id.supplies_applying_edit_btn:
                Intent intent = new Intent(this, SuppliesApplyEditActivity.class);
                intent.putParcelableArrayListExtra("suppliesList", suppliesList);
                startActivityForResult(intent, 10);
                break;
        }
    }

    private void getUseTime() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View timepickerview = inflater.inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(this);
        wheelMain = new WheelMain(timepickerview, true);
        wheelMain.screenheight = screenInfo.getHeight();
        String time = tv_useTime.getText().toString();
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
                        tv_useTime.setText(wheelMain.getTime());
                        tv_useTime.setTextColor(getResources().getColor(
                                R.color.black));
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == 10) {
                ArrayList<Supplies> suppliess = data.getParcelableArrayListExtra("suppliesList");
                suppliesList.clear();
                suppliesList.addAll(suppliess);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_supplies_applying, menu);
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
        String useTime = tv_useTime.getText().toString();
        String remarks = et_remarks.getText().toString().trim();
        JSONArray jsonArray = new JSONArray();
        for (Supplies supplies : suppliesList) {
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

        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "dosavematerialneedmain")
                .add("mainid", suppliesNo.getApplyId())
                .add("fileno", orderId)
                .add("billtype", "工程材料")
                .add("usedatetime", useTime)
                .add("remark", remarks)
                .add("userno", userNo)
                .add("makingjson", jsonArray.toString())
                .build();


        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                if (response.substring(0, 1).equals("E")) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(BusinessLeaderByMyOrderSuppliesApplyingActivity.this, "申请失败", Toast.LENGTH_SHORT);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            SuppliesNo suppliesNo = savaSuppliesNo(response);
                            Intent intent = getIntent();
                            intent.putExtra("position", position);
                            intent.putExtra("suppliesNo", (Parcelable) suppliesNo);
                            setResult(20, intent);
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
                        ToastUtil.showToast(BusinessLeaderByMyOrderSuppliesApplyingActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });

    }

    private SuppliesNo savaSuppliesNo(String applyId) {
        Calendar rightNow = Calendar.getInstance();
        String applyTime = dateFormat.format(rightNow.getTime());

        ArrayList<Supplies> list = new ArrayList<Supplies>();
        for (int i = 0; i < suppliesList.size(); i++) {
            Supplies supplies = new Supplies();
            supplies.setId(suppliesList.get(i).getId());
            supplies.setName(suppliesList.get(i).getName());
            supplies.setSpec(suppliesList.get(i).getSpec());
            supplies.setUnit(suppliesList.get(i).getUnit());
            supplies.setApplyNum(suppliesList.get(i).getApplyNum());
            supplies.setSendNum("0");
            list.add(supplies);
        }

        SuppliesNo suppliesNo = new SuppliesNo();
        suppliesNo.setApplyId(applyId);
        suppliesNo.setApplyTime(applyTime);
        suppliesNo.setUseTime(tv_useTime.getText().toString());
        suppliesNo.setRemarks(et_remarks.getText().toString().trim());
        suppliesNo.setSuppliesList(list);
        suppliesNo.setApplyState("申请中");

        return suppliesNo;
    }
}
