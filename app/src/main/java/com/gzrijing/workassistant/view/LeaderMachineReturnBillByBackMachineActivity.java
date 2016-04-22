package com.gzrijing.workassistant.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.LeaderMachineReturnBill;
import com.gzrijing.workassistant.entity.Subordinate;
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

public class LeaderMachineReturnBillByBackMachineActivity extends BaseActivity implements View.OnClickListener{

    private String userNo;
    private String machineNo;
    private String machineName;
    private LeaderMachineReturnBill bill;
    private TextView tv_machineNo;
    private TextView tv_machineName;
    private TextView tv_getAddress;
    private TextView et_sendAddress;
    private TextView tv_backUser;
    private TextView tv_backDate;
    private TextView btn_plan;
    private EditText getRemark;//yycq
    private ArrayList<Subordinate> subordinates = new ArrayList<Subordinate>();
    private WheelMain wheelMain;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Handler handler = new Handler();
    private ProgressDialog pDialog;
    private int machinePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_machine_return_bill_by_back_machine);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        Intent intent = getIntent();
        machineNo = intent.getStringExtra("machineNo");
        machineName = intent.getStringExtra("machineName");
        bill = intent.getParcelableExtra("bill");
        machinePosition = intent.getIntExtra("machinePosition", -1);

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_machineNo = (TextView) findViewById(R.id.leader_machine_return_bill_by_back_machine_no_tv);
        tv_machineNo.setText(machineNo);
        tv_machineName = (TextView) findViewById(R.id.leader_machine_return_bill_by_back_machine_name_tv);
        tv_machineName.setText(machineName);
        tv_getAddress = (TextView) findViewById(R.id.leader_machine_return_bill_by_back_machine_get_address_tv);
        tv_getAddress.setText(bill.getReturnAddress());
        et_sendAddress = (TextView) findViewById(R.id.leader_machine_return_bill_by_back_machine_send_address_et);
        tv_backUser = (TextView) findViewById(R.id.leader_machine_return_bill_by_back_machine_back_user_tv);
        tv_backDate = (TextView) findViewById(R.id.leader_machine_return_bill_by_back_machine_back_date_tv);
        tv_backDate.setText(bill.getReturnDate());
        btn_plan = (TextView) findViewById(R.id.leader_machine_return_bill_by_back_machine_plan_btn);
        getRemark =(EditText)findViewById(R.id.leader_machine_return_bill_by_back_machine_back_remark_et);//yycq
    }

    private void setListeners() {
        tv_backUser.setOnClickListener(this);
        tv_backDate.setOnClickListener(this);
        btn_plan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leader_machine_return_bill_by_back_machine_back_user_tv:
                Intent intent = new Intent(this, SubordinateActivity.class);
                intent.putExtra("flag", "送机");
                intent.putExtra("userNo", userNo);
                intent.putParcelableArrayListExtra("subordinates", subordinates);
                startActivityForResult(intent, 10);
                break;

            case R.id.leader_machine_return_bill_by_back_machine_back_date_tv:
                getsendDate();
                break;

            case R.id.leader_machine_return_bill_by_back_machine_plan_btn:
                plan();
                break;
        }
    }

    private void plan() {
        if(et_sendAddress.getText().toString().trim().equals("")){
            ToastUtil.showToast(this, "填写送机地点", Toast.LENGTH_SHORT);
            return;
        }
        if(tv_backUser.getText().toString().trim().equals("")){//yycq 添加判断
            ToastUtil.showToast(this, "请选择退机人", Toast.LENGTH_SHORT);
            return;
        }
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在安排中");
        pDialog.show();

        StringBuilder sb = new StringBuilder();
        for (Subordinate sub : subordinates) {
            if (sub.isCheck()) {
                sb.append(sub.getUserNo() + ",");
            }
        }
        String executors = sb.toString().substring(0, sb.toString().length() - 1);

        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("BillNo", bill.getBillNo());
            jsonObject.put("MachineNo", machineNo);
            jsonObject.put("SendUNo", executors);
            jsonObject.put("SendDate", tv_backDate.getText().toString());
            jsonObject.put("MachineAddress", et_sendAddress.getText().toString().trim());
            jsonObject.put("Remark",getRemark.getText().toString().trim());//yycq
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("jsonData", jsonArray.toString());

        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "doappointmachine")
                .add("userno", userNo)
                .add("machinedetailjson", jsonArray.toString())
                .build();
        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.equals("ok")) {
                            Intent intent = new Intent("action.com.gzrijing.workassistant.LeaderMachineReturnBillByPlan");
                            intent.putExtra("machinePosition", machinePosition);
                            intent.putExtra("billNo", bill.getBillNo());
                            sendBroadcast(intent);
                            ToastUtil.showToast(LeaderMachineReturnBillByBackMachineActivity.this,
                                    "安排成功", Toast.LENGTH_SHORT);
                            finish();
                        } else {
                            ToastUtil.showToast(LeaderMachineReturnBillByBackMachineActivity.this,
                                    "安排失败", Toast.LENGTH_SHORT);
                        }
                    }
                });
                pDialog.cancel();
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(LeaderMachineReturnBillByBackMachineActivity.this,
                                "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
                pDialog.cancel();
            }
        });
    }

    private void getsendDate() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View timepickerview = inflater.inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(this);
        wheelMain = new WheelMain(timepickerview, true);
        wheelMain.screenheight = screenInfo.getHeight();
        String time = tv_backDate.getText().toString();
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
                        tv_backDate.setText(wheelMain.getTime());
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
                String executors = data.getStringExtra("executors");
                subordinates = data.getParcelableArrayListExtra("subordinates");
                tv_backUser.setText(executors);
            }
        }
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
