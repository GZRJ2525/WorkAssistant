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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.QueryBeforeSuppliesExpandableAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.OrderTypeSupplies;
import com.gzrijing.workassistant.entity.Supplies;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class QueryBeforeSuppliesActivity extends BaseActivity implements View.OnClickListener {

    private ExpandableListView elv_supplies;
    private TextView tv_caliber;
    private Button btn_query;
    private String userNo;
    private String type;
    private Handler handler = new Handler();
    private String[] item;
    private int index;
    private ArrayList<OrderTypeSupplies> groupList = new ArrayList<OrderTypeSupplies>();
    private QueryBeforeSuppliesExpandableAdapter adapter;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_before_supplies);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        getCaliberItem();
    }

    private void getCaliberItem() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在加载数据...");
        pDialog.show();
        String url = null;
        try {
            url = "?cmd=getdrainsize&userno=" + URLEncoder.encode(userNo, "UTF-8")
                    + "&constypename=" + URLEncoder.encode(type, "UTF-8") + "&consclass=";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                ArrayList<String> list = JsonParseUtils.getCaliber(response);
                if (list.size() > 0) {
                    item = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        item[i] = list.get(i);
                    }
                }
                pDialog.dismiss();
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        item = new String[0];
                        ToastUtil.showToast(QueryBeforeSuppliesActivity.this, "与服务器断开", Toast.LENGTH_SHORT);
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

        tv_caliber = (TextView) findViewById(R.id.query_before_supplies_caliber_tv);
        btn_query = (Button) findViewById(R.id.query_before_supplies_query_btn);

        elv_supplies = (ExpandableListView) findViewById(R.id.query_before_supplies_elv);
        adapter = new QueryBeforeSuppliesExpandableAdapter(this, groupList);
        elv_supplies.setAdapter(adapter);

    }

    private void setListeners() {
        tv_caliber.setOnClickListener(this);
        btn_query.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.query_before_supplies_caliber_tv:
                selectCaliber();
                break;

            case R.id.query_before_supplies_query_btn:
                query();
                break;
        }
    }

    private void selectCaliber() {
        final int flag = index;
        new AlertDialog.Builder(this).setTitle("选择排水口径：").setSingleChoiceItems(
                item, index, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = which;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            tv_caliber.setText(item[index]);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = flag;
                    }
                }).show();
    }

    private void query() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在查询数据...");
        pDialog.show();
        String url = null;
        try {
            url = "?cmd=getnearmaterialneed&userno="+ URLEncoder.encode(userNo, "UTF-8")
                    +"&constypename="+URLEncoder.encode(type, "UTF-8")
                    +"&consclass=&drainsize="+tv_caliber.getText().toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<OrderTypeSupplies> list = JsonParseUtils.getOrderTypeSupplies(response);
                        groupList.clear();
                        groupList.addAll(list);
                        adapter.notifyDataSetChanged();
                        pDialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(QueryBeforeSuppliesActivity.this, "与服务器断开", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_query_before_supplies, menu);
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
            sure();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sure() {
        ArrayList<Supplies> suppliesList = new ArrayList<>();
        for(OrderTypeSupplies group : groupList){
            if(group.isCheck()){
                suppliesList.addAll(group.getSuppliesList());
            }
        }
        Intent intent = getIntent();
        intent.putParcelableArrayListExtra("suppliesList", suppliesList);
        setResult(10, intent);
        finish();
    }
}
