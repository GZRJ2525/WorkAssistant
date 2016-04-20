package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

public class GetGPSActivity extends BaseActivity {

    private String orderId;
    private TextView tv_gps;
    private Button btn_getGps;
    private LocationClient locationClient;
    private MyLocationListener mMyLocationListener;
    private ProgressDialog pDialog;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_gps);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_gps = (TextView) findViewById(R.id.get_gps_tv);
        btn_getGps = (Button) findViewById(R.id.get_gps_btn);
    }

    private void setListeners() {
        btn_getGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMyLocation();
            }
        });
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
        option.setScanSpan(900); // 设置定时定位的时间间隔。单位毫秒，<1000，只检查一次
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
            Intent intent = new Intent(GetGPSActivity.this, MapPointActivity.class);
            intent.putExtra("longitude", longitude);
            intent.putExtra("latitude", latitude);
            startActivityForResult(intent, 10);
            locationClient.stop();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == 10) {
                String location = data.getStringExtra("location");
                tv_gps.setText(location);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_get_gps, menu);
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
            submint();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void submint() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在提交...");
        pDialog.show();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "GpsCheckFun")
                .add("class", "3")
                .add("FileNo", orderId)
                .add("GpsNo", tv_gps.getText().toString())
                .build();

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                pDialog.cancel();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(response.equals("ok")){
                            ToastUtil.showToast(GetGPSActivity.this, "定位成功", Toast.LENGTH_SHORT);
                        }else{
                            ToastUtil.showToast(GetGPSActivity.this, "定位失败", Toast.LENGTH_SHORT);
                        }
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(GetGPSActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
                pDialog.cancel();
            }
        });
    }
}
