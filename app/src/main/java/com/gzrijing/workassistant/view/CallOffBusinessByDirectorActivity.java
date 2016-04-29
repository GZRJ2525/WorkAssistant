package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.BusinessHaveSend;
import com.gzrijing.workassistant.entity.ProblemType;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CallOffBusinessByDirectorActivity extends BaseActivity implements View.OnClickListener {

    private String orderId;
    private String userNo;
    private TextView tv_type;
    private TextView tv_state;
    private EditText et_reason;
    private Button btn_submit;
    private ArrayList<ProblemType> problemTypeList = new ArrayList<ProblemType>();
    private Handler handler = new Handler();
    private int typeIndex = 0;
    private int stateIndex = 0;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_off_business_by_director);

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

        getProblemType();
    }

    private void getProblemType() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在加载数据...");
        pDialog.show();
        String url = null;
        try {
            url = "?cmd=getaccidentreason&userno=" + URLEncoder.encode(userNo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response", response);
                ArrayList<ProblemType> list = JsonParseUtils.getProblemType(response);
                problemTypeList.addAll(list);
                pDialog.cancel();
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(CallOffBusinessByDirectorActivity.this,
                                "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
                pDialog.cancel();
            }
        });
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_type = (TextView) findViewById(R.id.call_off_business_by_director_type_tv);
        tv_state = (TextView) findViewById(R.id.call_off_business_by_director_state_tv);
        et_reason = (EditText) findViewById(R.id.call_off_business_by_director_reason_et);
        btn_submit = (Button) findViewById(R.id.call_off_business_by_director_submit_btn);

    }

    private void setListeners() {
        tv_type.setOnClickListener(this);
        tv_state.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_off_business_by_director_type_tv:
                selectType();
                break;

            case R.id.call_off_business_by_director_state_tv:
                selectState();
                break;

            case R.id.call_off_business_by_director_submit_btn:
                submit();
                break;
        }
    }

    private void selectType() {
        if (problemTypeList.size() != 0) {
            final String[] type = new String[problemTypeList.size()];
            for (int i = 0; i < problemTypeList.size(); i++) {
                type[i] = problemTypeList.get(i).getType();
            }
            final int index = typeIndex;
            new AlertDialog.Builder(this).setTitle("选择归属类型：").setSingleChoiceItems(
                    type, typeIndex, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            typeIndex = which;
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tv_type.setText(problemTypeList.get(typeIndex).getType());
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            typeIndex = index;
                        }
                    }).show();
        }
    }

    private void selectState() {
        final int index = stateIndex;
        new AlertDialog.Builder(this).setTitle("选择工程状态：").setSingleChoiceItems(
                new String[]{"停止", "取消"}, stateIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stateIndex = which;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (stateIndex == 0) {
                            tv_state.setText("停止");
                        }
                        if (stateIndex == 1) {
                            tv_state.setText("取消");
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stateIndex = index;
                    }
                }).show();
    }

    private void submit() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在提交...");
        pDialog.show();

        String handleuno = problemTypeList.get(typeIndex).getUserNo();
        if(handleuno.equals("")){
            handleuno = userNo;
        }
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "dosaveconsaccidentfreedom")
                .add("userno", userNo)
                .add("fileno", orderId)
                .add("filestate", tv_state.getText().toString())
                .add("accidentreason", tv_type.getText().toString())
                .add("handleuno", handleuno)
                .add("handlereason", et_reason.getText().toString().trim())
                .add("relationfileno", "")
                .add("appointinstallid", "")
                .build();
        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.substring(0, 1).equals("E")) {
                            ToastUtil.showToast(CallOffBusinessByDirectorActivity.this,
                                    "提交失败", Toast.LENGTH_SHORT);
                        } else {
                            ToastUtil.showToast(CallOffBusinessByDirectorActivity.this,
                                    "提交成功", Toast.LENGTH_SHORT);
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
                        ToastUtil.showToast(CallOffBusinessByDirectorActivity.this,
                                "与服务器断开连接", Toast.LENGTH_SHORT);
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
