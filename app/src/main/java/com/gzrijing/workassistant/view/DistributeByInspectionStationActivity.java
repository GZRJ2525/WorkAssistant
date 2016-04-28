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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.WorkGroup;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.JudgeDate;
import com.gzrijing.workassistant.util.ToastUtil;
import com.gzrijing.workassistant.widget.selectdate.ScreenInfo;
import com.gzrijing.workassistant.widget.selectdate.WheelMain;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DistributeByInspectionStationActivity extends BaseActivity implements View.OnClickListener{

    private String userNo;
    private String orderId;
    private TextView tv_workGroup;
    private LinearLayout ll_workGroup;
    private TextView tv_deadline;
    private LinearLayout ll_deadline;
    private ArrayList<WorkGroup> workGroupList = new ArrayList<WorkGroup>();
    private WheelMain wheelMain;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Handler handler = new Handler();
    private ProgressDialog pDialog;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribute_by_inspection_station);

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

        getWorkGroup();

    }

    private void getWorkGroup() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在加载工作组列表...");
        pDialog.show();
        String url = null;
        try {
            url = "?cmd=getworksit&userno=" + URLEncoder.encode(userNo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<WorkGroup> list = JsonParseUtils.getWorkGroup(response);
                        workGroupList.addAll(list);
                        pDialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(DistributeByInspectionStationActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
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

        tv_workGroup = (TextView) findViewById(R.id.distribute_by_inspection_station_work_group_tv);
        ll_workGroup = (LinearLayout) findViewById(R.id.distribute_by_inspection_station_work_group_ll);
        tv_deadline = (TextView) findViewById(R.id.distribute_by_inspection_station_deadline_tv);
        ll_deadline = (LinearLayout) findViewById(R.id.distribute_by_inspection_station_deadline_ll);
    }

    private void setListeners() {
        ll_workGroup.setOnClickListener(this);
        ll_deadline.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.distribute_by_inspection_station_work_group_ll:
                selectWorkGroup();
                break;

            case R.id.distribute_by_inspection_station_deadline_ll:
                getDeadline();
                break;
        }
    }

    private void selectWorkGroup() {
        if (workGroupList.size() != 0) {
            final String[] groupName = new String[workGroupList.size()];
            for (int i = 0; i < workGroupList.size(); i++) {
                groupName[i] = workGroupList.get(i).getGroupName();
            }
            final int flag = index;
            new android.support.v7.app.AlertDialog.Builder(this).setTitle("选择施工组：").setSingleChoiceItems(
                    groupName, index, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            index = which;
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tv_workGroup.setText(workGroupList.get(index).getGroupName());
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            index = flag;
                        }
                    }).show();
        }
    }

    private void getDeadline() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View timepickerview = inflater.inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(this);
        wheelMain = new WheelMain(timepickerview, true);
        wheelMain.screenheight = screenInfo.getHeight();
        String time = tv_deadline.getText().toString();
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
                        tv_deadline.setText(wheelMain.getTime());
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
        getMenuInflater().inflate(R.menu.menu_distribute, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_distribute) {
            if (tv_workGroup.getText().toString().equals("")) {
                ToastUtil.showToast(this, "请选择施工组", Toast.LENGTH_SHORT);
                return true;
            }
            distribute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void distribute() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在派工...");
        pDialog.show();
        final RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "dosaveconsappointtask")
                .add("userno", userNo)
                .add("fileno", orderId)
                .add("worksitno", workGroupList.get(index).getGroupNo())
                .add("estimatefinishdate", tv_deadline.getText().toString())
                .build();
        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.equals("ok")) {
                            saveInfo();
                        } else {
                            ToastUtil.showToast(DistributeByInspectionStationActivity.this, "派发失败", Toast.LENGTH_SHORT);
                        }
                        pDialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(DistributeByInspectionStationActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                });
            }
        });
    }

    private void saveInfo() {
        Intent intent = new Intent("action.com.gzrijing.workassistant.InspectionStationFragment.distribute");
        intent.putExtra("orderId", orderId);
        sendBroadcast(intent);
        ToastUtil.showToast(DistributeByInspectionStationActivity.this, "派工成功", Toast.LENGTH_SHORT);
        finish();
    }
}
