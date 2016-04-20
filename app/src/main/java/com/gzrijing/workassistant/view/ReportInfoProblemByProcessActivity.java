package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

public class ReportInfoProblemByProcessActivity extends BaseActivity implements View.OnClickListener {

    private String orderId;
    private String userNo;
    private TextView tv_type;
    private TextView tv_state;
    private TextView tv_influence;
    private EditText et_relationOrderId;
    private EditText et_reason;
    private Button btn_submit;
    private ArrayList<BusinessHaveSend> BHSList;
    private ArrayList<ProblemType> problemTypeList = new ArrayList<ProblemType>();
    private Handler handler = new Handler();
    private int typeIndex = 0;
    private int stateIndex = 0;
    private boolean[] checkedItems;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info_problem_by_process);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        Intent intent = getIntent();
        orderId = intent.getStringExtra("fileNo");
        BHSList = intent.getParcelableArrayListExtra("BHSList");

        checkedItems = new boolean[BHSList.size()];

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
                        ToastUtil.showToast(ReportInfoProblemByProcessActivity.this,
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

        tv_type = (TextView) findViewById(R.id.report_info_problem_by_process_type_tv);
        tv_state = (TextView) findViewById(R.id.report_info_problem_by_process_state_tv);
        tv_influence = (TextView) findViewById(R.id.report_info_problem_by_process_influence_tv);
        et_relationOrderId = (EditText) findViewById(R.id.report_info_problem_by_process_relation_order_id_et);
        et_reason = (EditText) findViewById(R.id.report_info_problem_by_process_reason_et);
        btn_submit = (Button) findViewById(R.id.report_info_problem_by_process_submit_btn);

    }

    private void setListeners() {
        tv_type.setOnClickListener(this);
        tv_state.setOnClickListener(this);
        tv_influence.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report_info_problem_by_process_type_tv:
                selectType();
                break;

            case R.id.report_info_problem_by_process_state_tv:
                selectState();
                break;

            case R.id.report_info_problem_by_process_influence_tv:
                selectInfluence();
                break;

            case R.id.report_info_problem_by_process_submit_btn:
                submit();
                break;
        }
    }

    private void selectInfluence() {
        String[] influence = new String[BHSList.size()];
        for (int i = 0; i < BHSList.size(); i++) {
            influence[i] = BHSList.get(i).getId() + "--" + BHSList.get(i).getContent();
        }
        new AlertDialog.Builder(this).setTitle("选择受影响的已派任务：").setMultiChoiceItems(
                influence, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < checkedItems.length; i++) {
                            if (checkedItems[i]) {
                                sb.append(BHSList.get(i).getId() + ",");
                            }
                        }
                        if(sb.length() > 0){
                            String str = sb.toString().substring(0, sb.length() - 1);
                            tv_influence.setText(str);
                        }
                    }
                }).show();

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
                new String[]{"暂缓执行", "停止", "取消"}, stateIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stateIndex = which;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (stateIndex == 0) {
                            tv_state.setText("暂缓执行");
                        }
                        if (stateIndex == 1) {
                            tv_state.setText("停止");
                        }
                        if (stateIndex == 2) {
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
                .add("relationfileno", et_relationOrderId.getText().toString().trim())
                .add("appointinstallid", tv_influence.getText().toString())
                .build();
        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.substring(0, 1).equals("E")) {
                            ToastUtil.showToast(ReportInfoProblemByProcessActivity.this,
                                    "提交失败", Toast.LENGTH_SHORT);
                        } else {
                            ToastUtil.showToast(ReportInfoProblemByProcessActivity.this,
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
                        ToastUtil.showToast(ReportInfoProblemByProcessActivity.this,
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
