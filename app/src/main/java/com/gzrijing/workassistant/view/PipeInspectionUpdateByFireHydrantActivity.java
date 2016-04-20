package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
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

public class PipeInspectionUpdateByFireHydrantActivity extends BaseActivity implements View.OnClickListener {

    private Inspection fireHydrant;
    private String areaName;
    private TextView tv_item1;
    private EditText et_item2;
    private EditText et_item3;
    private EditText et_item4;
    private EditText et_item5;
    private EditText et_item6;
    private EditText et_item7;
    private TextView tv_item8;
    private Button btn_item8;
    private Handler handler = new Handler();
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pipe_inspection_update_by_fire_hydrant);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        fireHydrant = intent.getParcelableExtra("fireHydrant");
        areaName = intent.getStringExtra("orderId").split("/")[1];
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_item1 = (TextView) findViewById(R.id.pipe_inspection_update_by_fire_hydrant_item1_tv);
        tv_item1.setText(fireHydrant.getNo());
        et_item2 = (EditText) findViewById(R.id.pipe_inspection_update_by_fire_hydrant_item2_et);
        et_item2.setText(fireHydrant.getName());
        et_item3 = (EditText) findViewById(R.id.pipe_inspection_update_by_fire_hydrant_item3_et);
        et_item3.setText(areaName);
        et_item4 = (EditText) findViewById(R.id.pipe_inspection_update_by_fire_hydrant_item4_et);
        et_item4.setText(fireHydrant.getValveNo());
        et_item5 = (EditText) findViewById(R.id.pipe_inspection_update_by_fire_hydrant_item5_et);
        et_item5.setText(fireHydrant.getValveGNo());
        et_item6 = (EditText) findViewById(R.id.pipe_inspection_update_by_fire_hydrant_item6_et);
        et_item6.setText(fireHydrant.getAddress());
        et_item7 = (EditText) findViewById(R.id.pipe_inspection_update_by_fire_hydrant_item7_et);
        et_item7.setText(fireHydrant.getModel());
        tv_item8 = (TextView) findViewById(R.id.pipe_inspection_update_by_fire_hydrant_item8_tv);
        tv_item8.setText(fireHydrant.getLongitude() + "，" + fireHydrant.getLatitude());
        btn_item8 = (Button) findViewById(R.id.pipe_inspection_update_by_fire_hydrant_item8_btn);
    }

    private void setListeners() {
        btn_item8.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pipe_inspection_update_by_fire_hydrant_item8_btn:
                Intent intent = new Intent(this, MapPointActivity.class);
                intent.putExtra("longitude", fireHydrant.getLongitude());
                intent.putExtra("latitude", fireHydrant.getLatitude());
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
                tv_item8.setText(location);
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
                .add("cmd", "upfireinf")
                .add("fileno", tv_item1.getText().toString().trim())
                .add("FileName", et_item2.getText().toString().trim())
                .add("AreaName", et_item3.getText().toString().trim())
                .add("ValveClass", et_item4.getText().toString().trim())
                .add("ValveGClass", et_item5.getText().toString().trim())
                .add("FireAddress", et_item6.getText().toString().trim())
                .add("FireClass", et_item7.getText().toString().trim())
                .add("FireGps", tv_item8.getText().toString())
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
                            marker.setValveGNo(et_item5.getText().toString().trim());
                            marker.setAddress(et_item6.getText().toString().trim());
                            marker.setModel(et_item7.getText().toString().trim());
                            marker.setLongitude(Double.valueOf(tv_item8.getText().toString().split("，")[0]));
                            marker.setLatitude(Double.valueOf(tv_item8.getText().toString().split("，")[1]));
                            marker.setType("0");
                            marker.setCheckFlag("0");
                            Intent intent = new Intent("action.com.gzrijing.workassistant.PipeInspectMap.update");
                            intent.putExtra("position", position);
                            intent.putExtra("marker", marker);
                            sendBroadcast(intent);
                            ToastUtil.showToast(PipeInspectionUpdateByFireHydrantActivity.this, "更新成功", Toast.LENGTH_SHORT);
                            finish();
                        } else {
                            ToastUtil.showToast(PipeInspectionUpdateByFireHydrantActivity.this, "更新失败", Toast.LENGTH_SHORT);
                        }
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(PipeInspectionUpdateByFireHydrantActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }
}
