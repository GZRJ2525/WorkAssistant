package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.SubordinateLocation;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.gzrijing.workassistant.widget.TrajectoryQueryDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TrajectoryQueryActivity extends BaseActivity implements View.OnClickListener {

    private String userNo;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Button btn_replay;
    private Button btn_exit;
    private List<SubordinateLocation> locInfos = new ArrayList<SubordinateLocation>();
    private List<LatLng> pts = new ArrayList<LatLng>();
    private ProgressDialog pDialog;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    btn_exit.setVisibility(View.VISIBLE);
                    btn_replay.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_trajectory_query);

        initViews();
        initData();
        setListeners();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences("saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        initMap();
        getSubordinateLocationInfo();
    }

    private void getSubordinateLocationInfo() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在加载数据...");
        pDialog.show();
        String url = null;
        try {
            url = "?cmd=GetSitUsergl&userno=" + URLEncoder.encode(userNo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<SubordinateLocation> list = JsonParseUtils.getSubordinateLocationInfo(response);
                        locInfos.addAll(list);
                        if (locInfos.size() > 0) {
                            initMarker();
                        }
                    }
                });
                pDialog.cancel();
            }

            @Override
            public void onError(Exception e) {
                pDialog.cancel();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(TrajectoryQueryActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private void initMarker() {
        for (int i = 0; i < locInfos.size(); i++) {
            LatLng point = new LatLng(locInfos.get(i).getLatitude(), locInfos
                    .get(i).getLongitude());
            // 构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.map_flag_blue);
            // 构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions().position(point).icon(
                    bitmap);
            // 在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);
        }
    }

    private void initMap() {
        mBaiduMap = mMapView.getMap();
        // 普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 定义Maker坐标点
        //小榄水务（22.67414, 113.255899）
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

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMapView = (MapView) findViewById(R.id.bmapView);
        btn_replay = (Button) findViewById(R.id.trajectory_query_replay_btn);
        btn_exit = (Button) findViewById(R.id.trajectory_query_exit_btn);
    }

    private void setListeners() {
        btn_replay.setOnClickListener(this);
        btn_exit.setOnClickListener(this);

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker markerInfo) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.listview_item_trajectory_query, null);
                TextView tv_name = (TextView) view
                        .findViewById(R.id.listview_item_trajectory_query_name_tv);
                TextView tv_tel = (TextView) view
                        .findViewById(R.id.listview_item_trajectory_query_tel_tv);
                TextView tv_lastTime = (TextView) view
                        .findViewById(R.id.listview_item_trajectory_query_last_time_tv);
                TextView tv_doingTask = (TextView) view
                        .findViewById(R.id.listview_item_trajectory_query_doing_task_tv);
                TextView tv_trajectory = (TextView) view
                        .findViewById(R.id.listview_item_trajectory_query_trajectory_tv);
                LatLng point = markerInfo.getPosition();
                for (final SubordinateLocation locInfo : locInfos) {
                    if (point.latitude == locInfo.getLatitude()
                            && point.longitude == locInfo.getLongitude()) {
                        tv_name.setText("姓名：" + locInfo.getName());
                        tv_tel.setText("电话：" + locInfo.getTel());
                        tv_lastTime.setText("最后记录时间：" + locInfo.getLastTime());
                        int index = locInfo.getDoingTask().indexOf(",");
                        if (index >= 0) {
                            String str = locInfo.getDoingTask().replace(",", "\n");
                            tv_doingTask.setText(str);
                        }else{
                            tv_doingTask.setText(locInfo.getDoingTask());
                        }

                        tv_trajectory.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                btn_exit.setVisibility(View.GONE);
                                btn_replay.setVisibility(View.GONE);
                                pts.clear();
                                getDate(locInfo.getUserNo());
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trajectory_query_replay_btn:
                btn_exit.setVisibility(View.GONE);
                btn_replay.setVisibility(View.GONE);
                mBaiduMap.clear();
                ReplayTrack();
                break;

            case R.id.trajectory_query_exit_btn:
                btn_exit.setVisibility(View.GONE);
                btn_replay.setVisibility(View.GONE);
                mBaiduMap.clear();
                initMarker();
                break;
        }
    }

    /**
     * 回放历史轨迹
     */
    private void ReplayTrack() {
        new Thread() {
            public void run() {
                List<LatLng> points = new ArrayList<LatLng>();
                for (int i = 0; i < pts.size(); i++) {
                    mBaiduMap.clear();
                    if (i == 0) {
                        points.add(pts.get(0));
                    } else {
                        points.add(pts.get(i));
                        OverlayOptions polygonOption = null;
                        polygonOption = new PolylineOptions().points(points)
                                .color(0xAA000000).width(12);
                        mBaiduMap.addOverlay(polygonOption);
                    }
                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.map_flag_blue);
                    OverlayOptions option = new MarkerOptions().position(
                            pts.get(i)).icon(bitmap);
                    mBaiduMap.addOverlay(option);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendEmptyMessage(1);
            }
        }.start();

    }

    private void getLatLng(String userNo, String beginTime, String endTime) {
        String url = null;
        try {
            url = "?cmd=GetUserRecord&UserNo=" + URLEncoder.encode(userNo, "UTF-8")
                    + "&DateTime1=" + URLEncoder.encode(beginTime, "UTF-8")
                    + "&DateTime2=" + URLEncoder.encode(endTime, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!response.equals("[]") && !response.substring(0, 1).equals("E")) {
                            String[] data = response.split(",");
                            for (int i = 0; i < data.length; i += 2) {
                                double latitude = Double.valueOf(data[i]);
                                double longitude = Double.valueOf(data[i + 1]);
                                LatLng pt = new LatLng(latitude, longitude);
                                pts.add(pt);
                            }
                            mBaiduMap.clear();
                            // 回放历史轨迹
                            ReplayTrack();
                        }
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(TrajectoryQueryActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private void getDate(final String userNo) {
        final TrajectoryQueryDialog dialog = new TrajectoryQueryDialog(TrajectoryQueryActivity.this);
        dialog.setTitle("请选择时间段");
        dialog.show();

        dialog.setClickListenerCallBack(new TrajectoryQueryDialog.ClickListenerCallBack() {
            @Override
            public void doConfirm(String beginTime, String endTime) {
                if (!beginTime.equals("") && !endTime.equals("")) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        c.setTime(sdf.parse(beginTime));
                        long bTime = c.getTimeInMillis();
                        Log.e("bTime", bTime + "");
                        c.setTime(sdf.parse(endTime));
                        long eTime = c.getTimeInMillis();
                        Log.e("eTime", eTime + "");
                        Log.e("eTime-bTime", (eTime - bTime) + "");

                        if (bTime < eTime) {
                            dialog.dismiss();
                            getLatLng(userNo, beginTime, endTime);
                        } else {
                            dialog.dismiss();
                            ToastUtil.showToast(TrajectoryQueryActivity.this, "时间选择范围有误", Toast.LENGTH_SHORT);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    dialog.dismiss();
                    ToastUtil.showToast(TrajectoryQueryActivity.this, "请选择时间段", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void doCancel() {
                dialog.dismiss();
            }
        });
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
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
