package com.gzrijing.workassistant.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.GridViewImageForReportInfoAdapter;
import com.gzrijing.workassistant.adapter.SafetyInspectRecordAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.entity.SafetyInspectForm;
import com.gzrijing.workassistant.entity.SafetyInspectSecondItem;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SafetyInspectRecordActivity extends BaseActivity {

    private String userNo;
    private String orderId;
    private Handler handler = new Handler();
    private TextView tv_date;
    private EditText et_situation;
    private EditText et_process;
    private ListView lv_failure;
    private GridView gv_image;
    private ArrayList<SafetyInspectSecondItem> failureList = new ArrayList<SafetyInspectSecondItem>();
    private ArrayList<PicUrl> picUrls = new ArrayList<PicUrl>();
    private SafetyInspectRecordAdapter failureAdapter;
    private GridViewImageForReportInfoAdapter picUrlsAdapter;
    private int index = 0;
    private String flag = "0";    //是否处理完（0未处理完，1处理完）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_inspect_record);

        initViews();
        initData();
        setListeners();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");

        getForm();
    }

    private void getForm() {
        String url = null;
        try {
            url = "?cmd=GetSafeOldInf&fileid=" + URLEncoder.encode(orderId, "UTF-8");
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
                        ArrayList<SafetyInspectForm> list = JsonParseUtils.getSafetyInspectForm(response);
                        for (SafetyInspectForm form : list) {
                            if (form.getFlag().equals("0")) {
                                tv_date.setText(form.getCheckDate());
                                et_situation.setText(form.getSituation());
                                et_process.setText(form.getProcess());
                                failureList.addAll(form.getFailure());
                                picUrls.addAll(form.getPicUrls());
                                failureAdapter = new SafetyInspectRecordAdapter(SafetyInspectRecordActivity.this, failureList, orderId);
                                lv_failure.setAdapter(failureAdapter);
                                picUrlsAdapter = new GridViewImageForReportInfoAdapter(SafetyInspectRecordActivity.this, picUrls);
                                gv_image.setAdapter(picUrlsAdapter);
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(SafetyInspectRecordActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_date = (TextView) findViewById(R.id.safety_inspect_record_date_tv);
        et_situation = (EditText) findViewById(R.id.safety_inspect_record_situation_et);
        et_process = (EditText) findViewById(R.id.safety_inspect_record_process_et);

        lv_failure = (ListView) findViewById(R.id.safety_inspect_record_failure_lv);

        gv_image = (GridView) findViewById(R.id.safety_inspect_record_image_gv);
    }

    private void setListeners() {
        gv_image.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (picUrls.size() > 0) {
                    Intent intent = new Intent(SafetyInspectRecordActivity.this, ImageBrowserForHttpActivity.class);
                    intent.putExtra("position", position);
                    intent.putParcelableArrayListExtra("picUrls", picUrls);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_safety_inspect_recode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_submit) {
            mShowDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void mShowDialog() {
        new AlertDialog.Builder(this).setTitle("是否处理完？").setSingleChoiceItems(
                new String[]{"否", "是"}, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = which;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(index == 0){
                            flag = "0";
                        }
                        if(index == 1){
                            flag = "1";
                        }
                        submit();
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private void submit() {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("TaskNo", orderId);
            jsonObject.put("CheckInf", et_situation.getText().toString().trim());
            jsonObject.put("SolveInf", et_process.getText().toString().trim());
            jsonObject.put("SolveFlag", flag);
            jsonObject.put("UpUserno", userNo);
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("jsonArray", jsonArray.toString());
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "UpSafeALLInf")
                .add("fileid", orderId)
                .add("fileJson", jsonArray.toString())
                .build();
        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(response.equals("ok")){
                            ToastUtil.showToast(SafetyInspectRecordActivity.this, "提交成功", Toast.LENGTH_SHORT);
                            finish();
                        }else{
                            ToastUtil.showToast(SafetyInspectRecordActivity.this, "提交失败", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(SafetyInspectRecordActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }
}
