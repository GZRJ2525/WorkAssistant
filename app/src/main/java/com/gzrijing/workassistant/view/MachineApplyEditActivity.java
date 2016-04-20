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
import com.gzrijing.workassistant.adapter.MachineApplyAdapter;
import com.gzrijing.workassistant.adapter.MachineQueryAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.Machine;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MachineApplyEditActivity extends BaseActivity implements View.OnClickListener{

    private ImageView iv_delAll;
    private Button btn_add;
    private EditText et_name;
    private EditText et_spec;
    private EditText et_unit;
    private EditText et_keyword;
    private Button btn_keyword;
    private ListView lv_apply;
    private ListView lv_query;
    private ArrayList<Machine> machineList;
    private List<Machine> machineQueries;
    private MachineApplyAdapter applyAdapter;
    private MachineQueryAdapter queryAdapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    machineQueries.clear();
                    List<Machine> MQList = (List<Machine>) msg.obj;
                    machineQueries.addAll(MQList);
                    queryAdapter.notifyDataSetChanged();
                    break;

                case 1:
                    ToastUtil.showToast(MachineApplyEditActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_apply_edit);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        Intent intent = getIntent();
        machineList = intent.getParcelableArrayListExtra("machineList");
        machineQueries = new ArrayList<Machine>();

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iv_delAll = (ImageView) findViewById(R.id.machine_apply_edit_del_all_iv);
        btn_add = (Button) findViewById(R.id.machine_apply_edit_add_btn);
        et_name = (EditText) findViewById(R.id.machine_apply_edit_name_et);
        et_spec = (EditText) findViewById(R.id.machine_apply_edit_spec_et);
        et_unit = (EditText) findViewById(R.id.machine_apply_edit_unit_et);
        et_keyword = (EditText) findViewById(R.id.machine_apply_edit_query_keyword_et);
        btn_keyword = (Button) findViewById(R.id.machine_apply_edit_query_keyword_btn);

        lv_apply = (ListView) findViewById(R.id.machine_apply_edit_apply_lv);
        applyAdapter = new MachineApplyAdapter(this, machineList);
        lv_apply.setAdapter(applyAdapter);

        lv_query = (ListView) findViewById(R.id.machine_apply_edit_query_lv);
        queryAdapter = new MachineQueryAdapter(this, machineQueries);
        lv_query.setAdapter(queryAdapter);
    }

    private void setListeners() {
        iv_delAll.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_keyword.setOnClickListener(this);

        lv_query.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Machine query = machineQueries.get(position);
                for(Machine machine : machineList){
                    if(machine.getName().equals(query.getName())){
                        machine.setApplyNum(machine.getApplyNum()+1);
                        applyAdapter.notifyDataSetChanged();
                        return;
                    }
                }
                Machine apply = new Machine();
                apply.setId(query.getId());
                apply.setName(query.getName());
                apply.setUnit(query.getUnit());
                apply.setApplyNum(1);
                machineList.add(apply);
                applyAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.machine_apply_edit_del_all_iv:
                machineList.clear();
                applyAdapter.notifyDataSetChanged();
                break;

            case R.id.machine_apply_edit_add_btn:
                addCustomMachine();
                break;

            case R.id.machine_apply_edit_query_keyword_btn:
                queryKeyword();
                break;
        }
    }

    private void addCustomMachine() {
        String name = et_name.getText().toString().trim();
        String spec = et_spec.getText().toString().trim();
        String unit = et_unit.getText().toString().trim();
        if (name.equals("")) {
            ToastUtil.showToast(this, "请填写名称", Toast.LENGTH_SHORT);
            return;
        }
        if (unit.equals("")) {
            ToastUtil.showToast(this, "请填写单位", Toast.LENGTH_SHORT);
            return;
        }
        Machine machine = new Machine();
        if(spec.equals("")){
            machine.setName(name);
        }else{
            machine.setName(name+"_"+spec);
        }
        machine.setUnit(unit);
        machine.setApplyNum(1);
        machineList.add(machine);
        applyAdapter.notifyDataSetChanged();
        et_name.setText("");
        et_spec.setText("");
        et_unit.setText("");
    }

    private void queryKeyword() {
        String keyWork = et_keyword.getText().toString().trim();
        if (keyWork.equals("")) {
            ToastUtil.showToast(this, "请填上关键字", Toast.LENGTH_SHORT);
            return;
        }
        String url = null;
        try {
            url = "?cmd=getmachinelist&machineno=&machinename=" + URLEncoder.encode(keyWork, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                List<Machine> MQList = JsonParseUtils.getMachineQueries(response);
                Message msg = handler.obtainMessage(0);
                msg.obj = MQList;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_machine_apply_edit, menu);
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
            intent.putParcelableArrayListExtra("machineList", machineList);
            setResult(10, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
