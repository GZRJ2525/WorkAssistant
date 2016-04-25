package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.ProjectAmountAddSuppliesAdapter;
import com.gzrijing.workassistant.adapter.SuppliesQueryAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.Supplies;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ProjectAmountAddSuppliesActivity extends BaseActivity implements View.OnClickListener {
    private ImageView iv_delAll;
    private EditText et_keyword;
    private Button btn_keyword;
    private Button btn_before;
    private ListView lv_supplies;
    private ListView lv_query;
    private List<Supplies> suppliesQueries;
    private ArrayList<Supplies> suppliesList;
    private ProjectAmountAddSuppliesAdapter adapter;
    private SuppliesQueryAdapter queryAdapter;
    private String type;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    suppliesQueries.clear();
                    List<Supplies> SQList = (List<Supplies>) msg.obj;
                    suppliesQueries.addAll(SQList);
                    queryAdapter.notifyDataSetChanged();
                    break;

                case 1:
                    ToastUtil.showToast(ProjectAmountAddSuppliesActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_amount_add_supplies);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        suppliesList = intent.getParcelableArrayListExtra("suppliesList");
        suppliesQueries = new ArrayList<Supplies>();

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iv_delAll = (ImageView) findViewById(R.id.project_amount_add_supplies_del_all_iv);
        et_keyword = (EditText) findViewById(R.id.project_amount_add_supplies_query_keyword_et);
        btn_keyword = (Button) findViewById(R.id.project_amount_add_supplies_query_keyword_btn);
        btn_before = (Button) findViewById(R.id.project_amount_add_supplies_query_before_supplies_btn);

        lv_supplies = (ListView) findViewById(R.id.project_amount_add_supplies_supplies_lv);
        adapter = new ProjectAmountAddSuppliesAdapter(this, suppliesList);
        lv_supplies.setAdapter(adapter);

        lv_query = (ListView) findViewById(R.id.project_amount_add_supplies_query_lv);
        queryAdapter = new SuppliesQueryAdapter(this, suppliesQueries);
        lv_query.setAdapter(queryAdapter);
    }

    private void setListeners() {
        iv_delAll.setOnClickListener(this);
        btn_keyword.setOnClickListener(this);
        btn_before.setOnClickListener(this);

        lv_query.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Supplies query = suppliesQueries.get(position);
                for (Supplies sup : suppliesList) {
                    if (sup.getId().equals(query.getId())) {
                        sup.setNum(String.valueOf(Float.valueOf(sup.getNum()) + 1));
                        adapter.notifyDataSetChanged();
                        return;
                    }
                }
                Supplies supplies = new Supplies();
                supplies.setId(query.getId());
                supplies.setName(query.getName());
                supplies.setSpec(query.getSpec());
                supplies.setUnit(query.getUnit());
                supplies.setNum("1.0");
                suppliesList.add(supplies);
                adapter.notifyDataSetChanged();
            }
        });

        adapter.setmOnclickCallBack(new ProjectAmountAddSuppliesAdapter.OnClickCallBack() {
            @Override
            public void click(String name) {
                queryKeyword(name);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.project_amount_add_supplies_del_all_iv:
                suppliesList.clear();
                adapter.notifyDataSetChanged();
                break;

            case R.id.project_amount_add_supplies_query_keyword_btn:
                String keyword = et_keyword.getText().toString().trim();
                queryKeyword(keyword);
                break;

            case R.id.project_amount_add_supplies_query_before_supplies_btn:
                Intent intent = new Intent(this, QueryBeforeSuppliesActivity.class);
                intent.putExtra("type", type);
                startActivityForResult(intent, 10);
                break;
        }
    }

    private void queryKeyword(String keyword) {
        if (keyword.equals("")) {
            ToastUtil.showToast(this, "请填上关键字", Toast.LENGTH_SHORT);
            return;
        }
        String url = null;
        try {
            url = "?cmd=getmaking&makingno=&makingname=" + URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                List<Supplies> SQList = JsonParseUtils.getSuppliesQueries(response);

                Message msg = handler.obtainMessage(0);
                msg.obj = SQList;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(Exception e) {
                Message msg = handler.obtainMessage(1);
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == 10) {
                ArrayList<Supplies> list = data.getParcelableArrayListExtra("suppliesList");
                suppliesList.clear();
                if (list.size() > 0) {
                    for (Supplies supplies : list) {
                        supplies.setNum(supplies.getApplyNum());
                        supplies.setApplyNum("");
                        suppliesList.add(supplies);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_supplies_apply_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_save) {
            Intent intent = getIntent();
            intent.putParcelableArrayListExtra("suppliesList", suppliesList);
            setResult(10, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
