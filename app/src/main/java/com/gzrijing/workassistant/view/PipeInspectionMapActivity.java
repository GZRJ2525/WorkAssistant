package com.gzrijing.workassistant.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.model.LatLng;
import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.Inspection;
import com.gzrijing.workassistant.listener.MyOrientationListener;

import java.util.ArrayList;


public class PipeInspectionMapActivity extends BaseActivity {

    private String orderId;
    private BaiduMap mBaiduMap;
    private MapView mMapView;
    private ArrayList<Inspection> markers;
    private LocationClient locationClient;
    private MyOrientationListener myOrientationListener;
    private int mXDirection;            //传感器弧度值
    private double mCurrentLatitude;   //最新一次的纬度
    private double mCurrentLongitude;   //最新一次的经度
    private float mCurrentAccracy;      //当前的精度
    private volatile boolean isFristLocation; //是否是第一次定位
    /**
     * 定位的监听器
     */
    private MyLocationListener mMyLocationListener;
    private int index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_pipe_inspection_map);

        initViews();
        initData();
        setListeners();

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 第一次定位
        isFristLocation = true;
        mMapView = (MapView) findViewById(R.id.pipe_inspection_map_bmapView);
    }

    private void initData() {
        Intent intent = getIntent();
        markers = intent.getParcelableArrayListExtra("inspectionList");
        orderId = intent.getStringExtra("orderId");
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
        mBaiduMap.setMapStatus(msu);

        initMyLocation();
        initOritationListener();
        initMarker();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.com.gzrijing.workassistant.PipeInspectMap.add");
        intentFilter.addAction("action.com.gzrijing.workassistant.PipeInspectMap.update");
        intentFilter.addAction("action.com.gzrijing.workassistant.PipeInspectMap.inspection");
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    /**
     * 初始化定位
     */
    private void initMyLocation() {
        locationClient = new LocationClient(this);
        mMyLocationListener = new MyLocationListener();
        locationClient.registerLocationListener(mMyLocationListener);
        // 设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); // 设置定位模式
        option.setOpenGps(true); // 是否打开GPS
        option.setCoorType("bd09ll"); // 设置返回值的坐标类型。
        option.setScanSpan(5000); // 设置定时定位的时间间隔。单位毫秒
        locationClient.setLocOption(option);

    }

    /**
     * 初始化方向传感器
     */
    private void initOritationListener() {
        myOrientationListener = new MyOrientationListener(
                getApplicationContext());
        myOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
                    @Override
                    public void onOrientationChanged(float x) {
                        mXDirection = (int) x;
                        Log.e("x", x + "");
                        // 构造定位数据
                        MyLocationData locData = new MyLocationData.Builder()
                                .accuracy(mCurrentAccracy)
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                                .direction(mXDirection)
                                .latitude(mCurrentLatitude)
                                .longitude(mCurrentLongitude).build();
                        // 设置定位数据
                        mBaiduMap.setMyLocationData(locData);
                        // 设置自定义图标
                        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                                .fromResource(R.drawable.map_gps_locked);
                        MyLocationConfiguration config = new MyLocationConfiguration(
                                MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker);
                        mBaiduMap.setMyLocationConfigeration(config);
                    }
                });
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // map view 销毁后不在处理新接收的位置
            if (bdLocation == null || mMapView == null) {
                return;
            }

            Log.e("Latitude", bdLocation.getLatitude() + "");
            Log.e("Longitude", bdLocation.getLongitude() + "");
            Log.e("mXDirection", mXDirection + "");
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mXDirection).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            mCurrentAccracy = bdLocation.getRadius();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            mCurrentLatitude = bdLocation.getLatitude();
            mCurrentLongitude = bdLocation.getLongitude();
            // 设置自定义图标
            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                    .fromResource(R.drawable.map_gps_locked);
            MyLocationConfiguration config = new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker);
            mBaiduMap.setMyLocationConfigeration(config);
            // 第一次定位时
            if (isFristLocation) {
                isFristLocation = false;
                LatLng ll = new LatLng(bdLocation.getLatitude(),
                        bdLocation.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }
    }

    /**
     * 初始化覆盖物
     */
    private void initMarker() {
        for (Inspection marker : markers) {
            addMarker(marker);
        }
    }

    private void addMarker(Inspection marker) {
        LatLng point = new LatLng(marker.getLatitude(), marker.getLongitude());
        BitmapDescriptor bitmap = null;
        if (marker.getType().equals("0")) {
            if(marker.getCheckFlag().equals("0")){
                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.map_flag_green);
            }
            if(marker.getCheckFlag().equals("1")){
                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.map_flag_gray_one);
            }
        }
        if (marker.getType().equals("1")) {
            if(marker.getCheckFlag().equals("0")){
                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.map_flag_blue);
            }
            if(marker.getCheckFlag().equals("1")){
                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.map_flag_gray_two);
            }

        }
        if (marker.getType().equals("2")) {
            if(marker.getCheckFlag().equals("0")){
                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.map_flag_black);
            }
            if(marker.getCheckFlag().equals("1")){
                bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.map_flag_gray_three);
            }

        }
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    private void setListeners() {
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final com.baidu.mapapi.map.Marker markerInfo) {
                final LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.listview_item_pipe_inspection_map_marker, mMapView, false);
                TextView tv_id = (TextView) view.findViewById(R.id.listview_item_pipe_inspection_map_marker_id_tv);
                TextView tv_name = (TextView) view.findViewById(R.id.listview_item_pipe_inspection_map_marker_name_tv);
                TextView tv_model = (TextView) view.findViewById(R.id.listview_item_pipe_inspection_map_marker_model_tv);
                TextView tv_valveNo = (TextView) view.findViewById(R.id.listview_item_pipe_inspection_map_marker_valveNo_tv);
                TextView tv_valveGNo = (TextView) view.findViewById(R.id.listview_item_pipe_inspection_map_marker_valveGNo_tv);
                TextView tv_address = (TextView) view.findViewById(R.id.listview_item_pipe_inspection_map_marker_address_tv);
                Button btn_inspection = (Button) view.findViewById(R.id.listview_item_pipe_inspection_map_marker_inspection_btn);
                Button btn_update = (Button) view.findViewById(R.id.listview_item_pipe_inspection_map_marker_update_btn);
                LatLng point = markerInfo.getPosition();
                for (int i = 0; i < markers.size(); i++) {
                    final Inspection marker = markers.get(i);
                    if (point.latitude == marker.getLatitude()
                            && point.longitude == marker.getLongitude()) {
                        tv_id.setText(marker.getNo());
                        tv_name.setText(marker.getName());
                        if (!marker.getType().equals("2")) {
                            tv_model.setText(marker.getModel());
                        }
                        tv_valveNo.setText(marker.getValveNo());
                        tv_valveGNo.setText(marker.getValveGNo());
                        tv_address.setText(marker.getAddress());
                        final int position = i;

                        btn_inspection.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PipeInspectionMapActivity.this, PipeInspectionFormActivity.class);
                                intent.putExtra("id", marker.getNo());
                                intent.putExtra("type", marker.getType());
                                intent.putExtra("position", position);
                                startActivity(intent);
                            }
                        });

                        btn_update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (marker.getType().equals("0")) {
                                    Intent intent = new Intent(PipeInspectionMapActivity.this, PipeInspectionUpdateByFireHydrantActivity.class);
                                    intent.putExtra("position", position);
                                    intent.putExtra("fireHydrant", marker);
                                    intent.putExtra("orderId", orderId);
                                    startActivity(intent);
                                }
                                if (marker.getType().equals("1")) {
                                    Intent intent = new Intent(PipeInspectionMapActivity.this, PipeInspectionUpdateByValveActivity.class);
                                    intent.putExtra("position", position);
                                    intent.putExtra("valve", marker);
                                    intent.putExtra("orderId", orderId);
                                    startActivity(intent);
                                }
                                if (marker.getType().equals("2")) {
                                    Intent intent = new Intent(PipeInspectionMapActivity.this, PipeInspectionUpdateByWaterWellActivity.class);
                                    intent.putExtra("position", position);
                                    intent.putExtra("waterWell", marker);
                                    intent.putExtra("orderId", orderId);
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                }
                InfoWindow infoWindow = new InfoWindow(view, point, -48);
                mBaiduMap.showInfoWindow(infoWindow);
                return false;
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                mBaiduMap.hideInfoWindow();
            }
        });
    }

    @Override
    protected void onStart() {
        // 开启图层定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!locationClient.isStarted()) {
            locationClient.start();
        }
        // 开启方向传感器
        myOrientationListener.start();
        super.onStart();
    }

    @Override
    protected void onStop() {
        // 关闭图层定位
        mBaiduMap.setMyLocationEnabled(false);
        locationClient.stop();
        // 关闭方向传感器
        myOrientationListener.stop();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pipe_inspection_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.pipe_inspection_overflow_download:
                Intent intent = new Intent(this, DownloadOfflineMapActivity.class);
                startActivity(intent);
                return true;
            case R.id.pipe_inspection_add:
                add();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void add() {
        new AlertDialog.Builder(this).setTitle("选择类型").setSingleChoiceItems(
                new String[]{"消防栓", "阀门", "污水井"}, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = which;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (index == 0) {
                            Intent intent = new Intent(PipeInspectionMapActivity.this, PipeInspectionAddByFireHydrantActivity.class);
                            intent.putExtra("orderId", orderId);
                            startActivity(intent);
                        }
                        if (index == 1) {
                            Intent intent = new Intent(PipeInspectionMapActivity.this, PipeInspectionAddByValveActivity.class);
                            intent.putExtra("orderId", orderId);
                            startActivity(intent);
                        }
                        if (index == 2) {
                            Intent intent = new Intent(PipeInspectionMapActivity.this, PipeInspectionAddByWaterWellActivity.class);
                            intent.putExtra("orderId", orderId);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.com.gzrijing.workassistant.PipeInspectMap.add")) {
                Inspection marker = intent.getParcelableExtra("marker");
                markers.add(marker);
                addMarker(marker);
            }
            if (action.equals("action.com.gzrijing.workassistant.PipeInspectMap.update")) {
                Inspection marker = intent.getParcelableExtra("marker");
                int position = intent.getIntExtra("position", -1);
                markers.remove(position);
                markers.add(marker);
                mBaiduMap.clear();
                initMarker();
            }

            if(action.equals("action.com.gzrijing.workassistant.PipeInspectMap.inspection")){
                int position = intent.getIntExtra("position", -1);
                markers.get(position).setCheckFlag("1");
                mBaiduMap.clear();
                initMarker();
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
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
