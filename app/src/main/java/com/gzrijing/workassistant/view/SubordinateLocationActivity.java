package com.gzrijing.workassistant.view;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.LocationMarker;

import java.util.ArrayList;
import java.util.List;

public class SubordinateLocationActivity extends BaseActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private List<LocationMarker> lMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_subordinate_location);

        initViews();
        initData();
        setListeners();

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMapView = (MapView) findViewById(R.id.bmapView);
    }

    private void initData() {
        initMap();
        initMarker();
    }

    private void initMap() {
        mBaiduMap = mMapView.getMap();
        // 普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 定义Maker坐标点
        LatLng point = new LatLng(23.043248, 113.315723);
        // 定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(18)
                .build();
        // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                .newMapStatus(mMapStatus);
        // 改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

    private void initMarker() {
        lMarkers = getMarker();
        for (int i = 0; i < lMarkers.size(); i++) {
            LatLng point = new LatLng(lMarkers.get(i).getLatitude(), lMarkers
                    .get(i).getLongitude());
            BitmapDescriptor bitmap = null;
            if(i == 0){
                // 构建Marker图标
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_red_dot);
            }else{
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.map_flag_blue);
            }
            // 构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
            // 在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);
        }
    }

    private List<LocationMarker> getMarker() {
        double[] latitudes = {23.044807, 23.043248, 23.045206, 23.044973};
        double[] longitudes = {113.314668, 113.315723, 113.31403, 113.316671};
        String[] names = {"XXXXXXX", "下属1", "下属2", "下属3"};
        String[] times = {"2015-8-19  11:20:00", "2015-8-19  11:30:30", "2015-8-19  11:30:30", "2015-8-19  11:30:10"};
        String[] tels = {"135XXXXXXXX", "134XXXXXXXX", "159XXXXXXXX", "136XXXXXXXX"};
        List<LocationMarker> lMarkers = new ArrayList<LocationMarker>();
        for (int i = 0; i < names.length; i++) {
            LocationMarker lMarker = new LocationMarker();
            lMarker.setLatitude(latitudes[i]);
            lMarker.setLongitude(longitudes[i]);
            lMarker.setName(names[i]);
            lMarker.setTime(times[i]);
            lMarker.setTel(tels[i]);
            lMarkers.add(lMarker);
        }
        return lMarkers;
    }

    private void setListeners() {
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final com.baidu.mapapi.map.Marker markerInfo) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.listview_item_location_marker_info, mMapView, false);
                TextView tv_name = (TextView) view.findViewById(R.id.listview_item_location_marker_info_name_tv);
                TextView tv_time = (TextView) view.findViewById(R.id.listview_item_location_marker_info_time_tv);
                TextView tv_tel = (TextView) view.findViewById(R.id.listview_item_location_marker_info_tel_tv);
                LatLng point = markerInfo.getPosition();
                for (int i = 0; i < lMarkers.size(); i++) {
                    if (point.latitude == lMarkers.get(i).getLatitude()
                            && point.longitude == lMarkers.get(i).getLongitude()) {
                        if(i == 0){
                            tv_name.setText("地址：" + lMarkers.get(i).getName());
                            tv_time.setText(lMarkers.get(i).getTime());
                            tv_tel.setText("电话：" + lMarkers.get(i).getTel());
                        }else{
                            tv_name.setText("姓名：" + lMarkers.get(i).getName());
                            tv_time.setText(lMarkers.get(i).getTime());
                            tv_tel.setText("电话：" + lMarkers.get(i).getTel());
                        }
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
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                mBaiduMap.hideInfoWindow();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subordinate_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
