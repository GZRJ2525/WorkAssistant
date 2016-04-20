package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.SafetyInspectTaskAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.SafetyInspectTask;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SafetyInspectTaskActivity extends BaseActivity implements View.OnClickListener {

    private ListView lv_tasks;
    private ArrayList<SafetyInspectTask> taskList = new ArrayList<SafetyInspectTask>();
    private SafetyInspectTaskAdapter adapter;
    private EditText et_id;
    private Button btn_query;
    private ProgressDialog pDialog;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_inspect_task);

        initViews();
        initData();
        setListeners();
    }

    private void initData() {
        queryTasks();
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_id = (EditText) findViewById(R.id.safety_inspect_task_id_et);
        btn_query = (Button) findViewById(R.id.safety_inspect_task_query_btn);

        lv_tasks = (ListView) findViewById(R.id.safety_inspect_task_lv);
        adapter = new SafetyInspectTaskAdapter(this, taskList);
        lv_tasks.setAdapter(adapter);
    }

    private void setListeners() {
        btn_query.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.safety_inspect_task_query_btn:
                queryTasks();
                break;
        }
    }

    private void queryTasks() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在加载数据");
        pDialog.show();
        String id = et_id.getText().toString().trim();
        String url = null;
        try {
            url = "?cmd=LoadTaskInf&fileid=" + URLEncoder.encode(id, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response", response);
                ArrayList<SafetyInspectTask> list = JsonParseUtils.getSafetyInspectTask(response);
                taskList.clear();
                taskList.addAll(list);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                pDialog.cancel();
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(SafetyInspectTaskActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
                pDialog.cancel();
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
