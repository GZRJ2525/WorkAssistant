package com.gzrijing.workassistant.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.User;
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

public class EntrustActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_replaceLeader;
    private TextView tv_beginTime;
    private TextView tv_endTime;
    private WheelMain wheelMain;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private String userNo;
    private String userSit;
    private ArrayList<User> userList = new ArrayList<User>();
    private Handler handler = new Handler();
    private String[] item;
    private int index;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrust);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");
        userSit = app.getString("userSit", "");
        String userRank = app.getString("userRank", "");

        if(!userRank.equals("0")){
            getReplaceLeader();
        }

    }

    private void getReplaceLeader() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在加载代班人...");
        pDialog.show();
        String url = null;
        try {
            url = "?cmd=getreplaceuser&userno=" + URLEncoder.encode(userNo, "UTF-8")
                    + "&worksit=" + URLEncoder.encode(userSit, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                ArrayList<User> list = JsonParseUtils.getReplaceLeader(response);
                userList.addAll(list);
                if (userList.size() > 0) {
                    item = new String[userList.size()];
                    for (int i = 0; i < userList.size(); i++) {
                        item[i] = userList.get(i).getUserName();
                    }
                }
                pDialog.dismiss();
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(EntrustActivity.this, "与服务器断开", Toast.LENGTH_SHORT);
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

        tv_replaceLeader = (TextView) findViewById(R.id.entrust_replace_leader_tv);
        tv_beginTime = (TextView) findViewById(R.id.entrust_begin_time_tv);
        tv_endTime = (TextView) findViewById(R.id.entrust_end_time_tv);
    }

    private void setListeners() {
        tv_replaceLeader.setOnClickListener(this);
        tv_beginTime.setOnClickListener(this);
        tv_endTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.entrust_replace_leader_tv:
                selectReplaceLeader();
                break;

            case R.id.entrust_begin_time_tv:
                getTime(tv_beginTime);
                break;

            case R.id.entrust_end_time_tv:
                getTime(tv_endTime);
                break;
        }
    }

    private void selectReplaceLeader() {
        final int flag = index;
        new android.support.v7.app.AlertDialog.Builder(this).setTitle("选择代班人：").setSingleChoiceItems(
                item, index, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = which;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_replaceLeader.setText(item[index]);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = flag;
                    }
                }).show();
    }

    private void getTime(final TextView tv_time) {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View timepickerview = inflater.inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(this);
        wheelMain = new WheelMain(timepickerview, true);
        wheelMain.screenheight = screenInfo.getHeight();
        String time = tv_time.getText().toString();
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
                        tv_time.setText(wheelMain.getTime());
                        tv_time.setTextColor(getResources().getColor(
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entrust, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_entrust) {
            entruste();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void entruste() {
        if (tv_replaceLeader.getText().toString().equals("")) {
            ToastUtil.showToast(this, "请选择代班人", Toast.LENGTH_SHORT);
            return;
        }

        if (tv_beginTime.getText().toString().equals("")) {
            ToastUtil.showToast(this, "请选择开始时间", Toast.LENGTH_SHORT);
            return;
        }

        if (tv_endTime.getText().toString().equals("")) {
            ToastUtil.showToast(this, "请选择结束时间", Toast.LENGTH_SHORT);
            return;
        }

        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在委托中...");
        pDialog.show();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "dosavereplacesitleader")
                .add("userno", userNo)
                .add("worksit", userSit)
                .add("replaceuserno", userList.get(index).getUserNo())
                .add("begindate", tv_beginTime.getText().toString())
                .add("enddate", tv_endTime.getText().toString())
                .build();

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(response.equals("ok")){
                            ToastUtil.showToast(EntrustActivity.this, "委托成功", Toast.LENGTH_SHORT);
                        }else{
                            ToastUtil.showToast(EntrustActivity.this, "委托失败", Toast.LENGTH_SHORT);
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
                        ToastUtil.showToast(EntrustActivity.this, "与服务器断开", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                });
            }
        });

    }
}
