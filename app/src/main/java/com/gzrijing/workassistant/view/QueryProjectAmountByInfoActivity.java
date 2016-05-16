package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.ReportInfoProjectAmountByOkAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.Acceptance;
import com.gzrijing.workassistant.entity.QueryProjectAmount;
import com.gzrijing.workassistant.entity.Supplies;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class QueryProjectAmountByInfoActivity extends BaseActivity {

    private TextView tv_approvalName;
    private TextView tv_approvalTime;
    private TextView tv_feeType;
    private TextView tv_content;
    private TextView tv_civil;
    private ListView lv_supplies;
    private QueryProjectAmount projectAmount;
    private ArrayList<Supplies> suppliesList = new ArrayList<Supplies>();
    private ReportInfoProjectAmountByOkAdapter adapter;
    private Handler handler = new Handler();
    private String userNo;
    private String orderId;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_project_amount_by_info);

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
        projectAmount = intent.getParcelableExtra("projectAmount");

        getQueryProjectAmountSupplies();
    }

    private void getQueryProjectAmountSupplies() {//卢工接口45．查看已派工单工程量信息—材料
        String url = "?cmd=getsomeinstallconfirmdetail&togetherid=&confirmid=" + projectAmount.getId() + "&fileno=";
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<Supplies> list = JsonParseUtils.getProjectAmountReportInfoSupplies(response);
                        suppliesList.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(QueryProjectAmountByInfoActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_approvalName = (TextView) findViewById(R.id.query_project_amount_by_info_approval_name_tv);
        tv_approvalName.setText(projectAmount.getApprovalName());
        tv_approvalTime = (TextView) findViewById(R.id.query_project_amount_by_info_approval_time_tv);
        tv_approvalTime.setText(projectAmount.getApprovalTime());
        tv_feeType = (TextView) findViewById(R.id.query_project_amount_by_info_fee_type_tv);
        tv_feeType.setText(projectAmount.getFeeType());
        tv_content = (TextView) findViewById(R.id.query_project_amount_by_info_content_tv);
        tv_content.setText(projectAmount.getContent());
        tv_civil = (TextView) findViewById(R.id.query_project_amount_by_info_civil_tv);
        tv_civil.setText(projectAmount.getCivil());

        lv_supplies = (ListView) findViewById(R.id.query_project_amount_by_info_supplies_lv);
        adapter = new ReportInfoProjectAmountByOkAdapter(this, suppliesList);
        lv_supplies.setAdapter(adapter);
    }

    private void setListeners() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_query_project_amount_by_info, menu);

        String state = projectAmount.getState();
        if(state.equals("已审核")){
            menu.findItem(R.id.action_print).setVisible(true);
        }else {
            menu.findItem(R.id.action_print).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
           return true;
        }

        if (id == R.id.action_print) {
            print();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void print() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在获取打印数据...");
        pDialog.show();

        String url = null;
        try {
            url = "?cmd=getfinishconstruction&userno=" + URLEncoder.encode(userNo, "UTF-8")
                    + "&fileno=" + URLEncoder.encode(orderId, "UTF-8") + "&enddate=&isfinish=2";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e("url", url);
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Acceptance> list = JsonParseUtils.getAcceptanceInfo(response);
                        if(list.size()>0){
                            Intent intent = new Intent(QueryProjectAmountByInfoActivity.this, PrintActivity.class);
                            intent.putExtra("flag", projectAmount.getFeeType());
                            intent.putExtra("acceptance", list.get(0));
                            startActivity(intent);
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
                        ToastUtil.showToast(QueryProjectAmountByInfoActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                });
            }
        });
    }

}
