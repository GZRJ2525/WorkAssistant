package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.Inspection;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

public class PipeInspectionUpdateByValveActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_item1;
    private EditText et_item2;
    private EditText et_item3;
    private EditText et_item4;
    private EditText et_item5;
    private EditText et_item6;
    private TextView tv_item7;
    private Button btn_item7;
    private Inspection valve;
    private String areaName;
    private Handler handler = new Handler();
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pipe_inspection_update_by_valve);

        initData();
        initViews();
        initListeners();
    }

    private void initData() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        valve = intent.getParcelableExtra("valve");
        areaName = intent.getStringExtra("orderId").split("/")[1];
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_item1 = (TextView) findViewById(R.id.pipe_inspection_update_by_valve_item1_tv);
        tv_item1.setText(valve.getNo());
        et_item2 = (EditText) findViewById(R.id.pipe_inspection_update_by_valve_item2_et);
        et_item2.setText(valve.getName());
        et_item3 = (EditText) findViewById(R.id.pipe_inspection_update_by_valve_item3_et);
        et_item3.setText(areaName);
        et_item4 = (EditText) findViewById(R.id.pipe_inspection_update_by_valve_item4_et);
        et_item4.setText(valve.getValveNo());
        et_item5 = (EditText) findViewById(R.id.pipe_inspection_update_by_valve_item5_et);
        et_item5.setText(valve.getValveGNo());
        et_item6 = (EditText) findViewById(R.id.pipe_inspection_update_by_valve_item6_et);
        et_item6.setText(valve.getAddress());
        tv_item7 = (TextView) findViewById(R.id.pipe_inspection_update_by_valve_item7_tv);
        tv_item7.setText(valve.getLongitude() + "，" + valve.getLatitude());
        btn_item7 = (Button) findViewById(R.id.pipe_inspection_update_by_valve_item7_btn);
    }

    private void initListeners() {
        btn_item7.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pipe_inspection_update_by_valve_item7_btn:
                Intent intent = new Intent(this, MapPointActivity.class);
                intent.putExtra("longitude", valve.getLongitude());
                intent.putExtra("latitude", valve.getLatitude());
                startActivityForResult(intent, 10);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10){
            if(resultCode == 10){
                String location = data.getStringExtra("location");
                tv_item7.setText(location);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pipe_inspection_update_by_valve, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_update) {
            upData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void upData() {
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "upvalveinf")
                .add("fileno", tv_item1.getText().toString().trim())
                .add("FileName", et_item2.getText().toString().trim())
                .add("AreaName", et_item3.getText().toString().trim())
                .add("ValveClass", et_item4.getText().toString().trim())
                .add("WellClass", et_item5.getText().toString().trim())
                .add("Address", et_item6.getText().toString().trim())
                .add("ValveGps", tv_item7.getText().toString())
                .build();

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.equals("ok")) {
                            Inspection marker = new Inspection();
                            marker.setNo(tv_item1.getText().toString());
                            marker.setName(et_item2.getText().toString().trim());
                            marker.setValveNo(et_item4.getText().toString().trim());
                            marker.setModel(et_item4.getText().toString().trim());
                            marker.setValveGNo(et_item5.getText().toString().trim());
                            marker.setAddress(et_item6.getText().toString().trim());
                            marker.setLongitude(Double.valueOf(tv_item7.getText().toString().split("，")[0]));
                            marker.setLatitude(Double.valueOf(tv_item7.getText().toString().split("，")[1]));
                            marker.setType("1");
                            marker.setCheckFlag("0");
                            Intent intent = new Intent("action.com.gzrijing.workassistant.PipeInspectMap.update");
                            intent.putExtra("marker", marker);
                            intent.putExtra("position", position);
                            sendBroadcast(intent);
                            ToastUtil.showToast(PipeInspectionUpdateByValveActivity.this, "更新成功", Toast.LENGTH_SHORT);
                            finish();
                        } else {
                            ToastUtil.showToast(PipeInspectionUpdateByValveActivity.this, "更新失败", Toast.LENGTH_SHORT);
                        }
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(PipeInspectionUpdateByValveActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }
}
