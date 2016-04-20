package com.gzrijing.workassistant.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;

public class MapPointActivity extends BaseActivity implements View.OnClickListener{

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Button btn_back;
    private Button btn_getPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map_point);

        initViews();
        initData();
        setListeners();
    }

    private void initViews() {
        mMapView = (MapView) findViewById(R.id.map_point_bmapView);
        btn_back = (Button) findViewById(R.id.map_point_back_btn);
        btn_getPoint = (Button) findViewById(R.id.map_point_get_point_btn);
    }

    private void initData() {
        Intent intent = getIntent();
        double longitude = intent.getDoubleExtra("longitude", 0);
        double latitude = intent.getDoubleExtra("latitude", 0);
        Log.e("longitude", longitude + "");
        Log.e("latitude", latitude + "");
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(20.0f);
        mBaiduMap.setMapStatus(msu);
        LatLng ll = new LatLng(latitude, longitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);

        MyIconCanves icm = new MyIconCanves(this);
        getWindow().addContentView(icm,new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

    }

    private void setListeners() {
        btn_back.setOnClickListener(this);
        btn_getPoint.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.map_point_back_btn:
                finish();
                break;

            case R.id.map_point_get_point_btn:
                getPoint();
                break;
        }
    }

    private void getPoint() {
        MapStatus mapStatus = mBaiduMap.getMapStatus();
        LatLng center = mapStatus.target;
        double longitude = center.longitude;
        double latitude = center.latitude;
        String location = longitude + "，"+ latitude;
        Log.e("location", location);
        Intent intent = getIntent();
        intent.putExtra("location", location);
        setResult(10, intent);
        finish();
    }


    // 在屏幕中心点绘制图标
    public class MyIconCanves extends View {
        private Bitmap mBitmap;

        public MyIconCanves(Context context) {
            super(context);
            mBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.map_flag_blue);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(mBitmap,
                    this.getWidth() / 2 - mBitmap.getWidth() / 2, this.getHeight() / 2 - mBitmap.getHeight(), null);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }


}
