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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.Inspection;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

public class PipeInspectionAddByWaterWellActivity extends BaseActivity implements View.OnClickListener {

    private String areaNo;
    private TextView tv_item1;
    private Button btn_item1;
    private EditText et_item2;
    private EditText et_item3;
    private EditText et_item4;
    private EditText et_item5;
    private EditText et_item6;
    private TextView tv_item7;
    private Button btn_item7;
    private Handler handler = new Handler();
    private LocationClient locationClient;
    private MyLocationListener mMyLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pipe_inspection_add_by_water_well);

        initData();
        initViews();
        initListeners();
    }

    private void initData() {
        Intent intent = getIntent();
        areaNo = intent.getStringExtra("orderId").split("/")[0];
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_item1 = (TextView) findViewById(R.id.pipe_inspection_add_by_water_well_item1_tv);
        btn_item1 = (Button) findViewById(R.id.pipe_inspection_add_by_water_well_item1_btn);
        et_item2 = (EditText) findViewById(R.id.pipe_inspection_add_by_water_well_item2_et);
        et_item3 = (EditText) findViewById(R.id.pipe_inspection_add_by_water_well_item3_et);
        et_item3.setText(areaNo);
        et_item4 = (EditText) findViewById(R.id.pipe_inspection_add_by_water_well_item4_et);
        et_item5 = (EditText) findViewById(R.id.pipe_inspection_add_by_water_well_item5_et);
        et_item6 = (EditText) findViewById(R.id.pipe_inspection_add_by_water_well_item6_et);
        tv_item7 = (TextView) findViewById(R.id.pipe_inspection_add_by_water_well_item7_tv);
        btn_item7 = (Button) findViewById(R.id.pipe_inspection_add_by_water_well_item7_btn);
    }

    private void initListeners() {
        btn_item1.setOnClickListener(this);
        btn_item7.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pipe_inspection_add_by_water_well_item1_btn:
                getNo();
                break;


            case R.id.pipe_inspection_add_by_water_well_item7_btn:
                initMyLocation();
                break;
        }

    }

    private void initMyLocation() {
        locationClient = new LocationClient(this);
        mMyLocationListener = new MyLocationListener();
        locationClient.registerLocationListener(mMyLocationListener);
        // 设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); //设置定位模式
        option.setOpenGps(true); // 是否打开GPS
        option.setCoorType("bd09ll"); // 设置返回值的坐标类型。
        option.setScanSpan(900); // 设置定时定位的时间间隔。单位毫秒
        locationClient.setLocOption(option);
        locationClient.start();
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // map view 销毁后不在处理新接收的位置
            if (bdLocation == null) {
                return;
            }
            Log.e("Longitude", bdLocation.getLongitude() + "");
            Log.e("Latitude", bdLocation.getLatitude() + "");
            double longitude = bdLocation.getLongitude();
            double latitude = bdLocation.getLatitude();
            Intent intent = new Intent(PipeInspectionAddByWaterWellActivity.this, MapPointActivity.class);
            intent.putExtra("longitude", longitude);
            intent.putExtra("latitude", latitude);
            startActivityForResult(intent, 10);
            locationClient.stop();
        }
    }

    private void getNo() {
        btn_item1.setVisibility(View.GONE);
        String url = "?cmd=autoslopno";
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_item1.setText(response);
                        btn_item1.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(PipeInspectionAddByWaterWellActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                        btn_item1.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == 10) {
                String location = data.getStringExtra("location");
                tv_item7.setText(location);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pipe_inspection_add_by_valve, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_add) {
            add();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void add() {
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "addnewslop")
                .add("fileno", tv_item1.getText().toString())
                .add("FileName", et_item2.getText().toString().trim())
                .add("AreaNo", et_item3.getText().toString().trim())
                .add("ValveClass", et_item4.getText().toString().trim())
                .add("WellClass", et_item5.getText().toString().trim())
                .add("SlopAddress", et_item6.getText().toString().trim())
                .add("GpsNo", tv_item7.getText().toString())
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
                            marker.setLongitude(Double.valueOf(tv_item7.getText().toString().split("，")[0]));
                            marker.setLatitude(Double.valueOf(tv_item7.getText().toString().split("，")[1]));
                            marker.setType("2");
                            marker.setCheckFlag("0");
                            Intent intent = new Intent("action.com.gzrijing.workassistant.PipeInspectMap.add");
                            intent.putExtra("marker", marker);
                            sendBroadcast(intent);
                            ToastUtil.showToast(PipeInspectionAddByWaterWellActivity.this, "增加成功", Toast.LENGTH_SHORT);
                            finish();
                        } else {
                            ToastUtil.showToast(PipeInspectionAddByWaterWellActivity.this, "增加失败", Toast.LENGTH_SHORT);
                        }
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(PipeInspectionAddByWaterWellActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }
}
