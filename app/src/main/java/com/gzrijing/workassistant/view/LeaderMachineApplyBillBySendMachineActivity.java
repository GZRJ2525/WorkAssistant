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
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.LeaderMachineApplyBill;
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

public class LeaderMachineApplyBillBySendMachineActivity extends BaseActivity implements View.OnClickListener {

    private String userNo;
    private String machineNo;
    private String machineName;
    private String flag;
    private LeaderMachineApplyBill bill;
    private TextView tv_machineNo;
    private TextView tv_machineName;
    private TextView tv_getAddress;
    private TextView tv_sendAddress;
    private TextView tv_sendUser;
    private TextView tv_sendDate;
    private TextView btn_send;
    private String getAddress;
    private ArrayList<Subordinate> subordinates = new ArrayList<Subordinate>();
    private WheelMain wheelMain;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Handler handler = new Handler();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_machine_apply_bill_by_send_machine);

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
        getAddress = intent.getStringExtra("getAddress");
        flag = intent.getStringExtra("flag");
        bill = intent.getParcelableExtra("bill");

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_machineNo = (TextView) findViewById(R.id.leader_machine_apply_bill_by_send_machine_no_tv);
        tv_machineNo.setText(machineNo);
        tv_machineName = (TextView) findViewById(R.id.leader_machine_apply_bill_by_send_machine_name_tv);
        tv_machineName.setText(machineName);
        tv_getAddress = (TextView) findViewById(R.id.leader_machine_apply_bill_by_send_machine_get_address_tv);
        tv_getAddress.setText(getAddress);
        tv_sendAddress = (TextView) findViewById(R.id.leader_machine_apply_bill_by_send_machine_send_address_tv);
        tv_sendAddress.setText(bill.getUseAddress());
        tv_sendUser = (TextView) findViewById(R.id.leader_machine_apply_bill_by_send_machine_send_user_tv);
        tv_sendDate = (TextView) findViewById(R.id.leader_machine_apply_bill_by_send_machine_send_date_tv);
        tv_sendDate.setText(bill.getUseDate());
        btn_send = (TextView) findViewById(R.id.leader_machine_apply_bill_by_send_machine_send_btn);
        if (flag.equals("安排")) {
            btn_send.setText("安排");
        }
        if (flag.equals("调派")) {
            btn_send.setText("调派");
        }
    }

    private void setListeners() {
        tv_sendUser.setOnClickListener(this);
        tv_sendDate.setOnClickListener(this);
        btn_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leader_machine_apply_bill_by_send_machine_send_user_tv:
                Intent intent = new Intent(this, SubordinateActivity.class);
                intent.putExtra("flag", "送机");
                intent.putExtra("userNo", userNo);
                intent.putParcelableArrayListExtra("subordinates", subordinates);
                startActivityForResult(intent, 10);
                break;

            case R.id.leader_machine_apply_bill_by_send_machine_send_date_tv:
                getsendDate();
                break;

            case R.id.leader_machine_apply_bill_by_send_machine_send_btn:
                send();
                break;
        }
    }

    private void send() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在" + flag + "中");
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
            jsonObject.put("SendDate", tv_sendDate.getText().toString());
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
                            Intent intent = new Intent("action.com.gzrijing.workassistant.LeaderMachineApplyBillByPlan");
                            intent.putExtra("machineName", machineName);
                            intent.putExtra("billNo", bill.getBillNo());
                            sendBroadcast(intent);
                            ToastUtil.showToast(LeaderMachineApplyBillBySendMachineActivity.this,
                                    flag + "成功", Toast.LENGTH_SHORT);
                            finish();
                        } else {
                            ToastUtil.showToast(LeaderMachineApplyBillBySendMachineActivity.this,
                                    flag + "失败", Toast.LENGTH_SHORT);
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
                        ToastUtil.showToast(LeaderMachineApplyBillBySendMachineActivity.this,
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
        String time = tv_sendDate.getText().toString();
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
                        tv_sendDate.setText(wheelMain.getTime());
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
                tv_sendUser.setText(executors);
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
