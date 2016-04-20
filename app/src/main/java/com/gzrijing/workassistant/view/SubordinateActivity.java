package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.SubordinateAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.Subordinate;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SubordinateActivity extends BaseActivity {

    private String flag;
    private TextView tv_selected;
    private ImageView iv_checkAll;
    private ListView lv_subordinate;
    public static boolean isCheck;
    private ArrayList<Subordinate> subordinates;
    private SubordinateAdapter adapter;
    private HashMap<Integer, String> names = new HashMap<Integer, String>();
    private String orderId;
    private String userNo;
    private Handler handler = new Handler();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subordinate);

        initViews();
        initData();
        setListeners();
    }

    private void initData() {
        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        subordinates = intent.getParcelableArrayListExtra("subordinates");
        if (subordinates.toString().equals("[]")) {
            if (flag.equals("派工")) {
                orderId = intent.getStringExtra("orderId");
                initSubordinates();
            }
            if (flag.equals("送机")) {
                userNo = intent.getStringExtra("userNo");
                initSubordinatesByMachine();
            }
        } else {
            for (int i = 0; i < subordinates.size(); i++) {
                if (subordinates.get(i).isCheck()) {
                    names.put(i, subordinates.get(i).getName());
                }
            }
            adapter = new SubordinateAdapter(SubordinateActivity.this, subordinates, iv_checkAll, tv_selected, names);
            lv_subordinate.setAdapter(adapter);
        }
    }

    private void initSubordinatesByMachine() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在加载数据...");
        pDialog.show();
        String url = null;
        try {
            url = "?cmd=getsitusergl&userno=" + URLEncoder.encode(userNo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<Subordinate> subList = JsonParseUtils.getSubordinate(response);
                        subordinates.addAll(subList);
                        adapter = new SubordinateAdapter(SubordinateActivity.this, subordinates, iv_checkAll, tv_selected, names);
                        lv_subordinate.setAdapter(adapter);
                        pDialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(SubordinateActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                });

            }
        });
    }

    private void initSubordinates() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在加载数据...");
        pDialog.show();
        String url = null;
        try {
            url = "?cmd=getsituser&fileno=" + URLEncoder.encode(orderId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<Subordinate> subList = JsonParseUtils.getSubordinate(response);
                        subordinates.addAll(subList);
                        adapter = new SubordinateAdapter(SubordinateActivity.this, subordinates, iv_checkAll, tv_selected, names);
                        lv_subordinate.setAdapter(adapter);
                        pDialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(SubordinateActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
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

        tv_selected = (TextView) findViewById(R.id.subordinate_selected_tv);
        iv_checkAll = (ImageView) findViewById(R.id.subordinate_check_all_iv);
        lv_subordinate = (ListView) findViewById(R.id.subordinate_lv);
    }

    private void setListeners() {
        iv_checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheck) {
                    for (Subordinate subordinate : subordinates) {
                        subordinate.setCheck(false);
                    }
                    names.clear();
                    iv_checkAll.setImageResource(R.drawable.login_checkbox_off);
                } else {
                    for (int i = 0; i < subordinates.size(); i++) {
                        subordinates.get(i).setCheck(true);
                        names.put(i, subordinates.get(i).getName());
                    }
                    iv_checkAll.setImageResource(R.drawable.login_checkbox_on);
                }
                adapter.notifyDataSetChanged();
                isCheck = !isCheck;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subordinate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {

            finish();
            return true;
        }

        if (id == R.id.action_sure) {
            String executors = tv_selected.getText().toString().trim();
            Intent intent = getIntent();
            intent.putExtra("executors", executors);
            intent.putParcelableArrayListExtra("subordinates", subordinates);
            setResult(10, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
