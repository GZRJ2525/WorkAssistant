package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.InspectionStationAdapter;
import com.gzrijing.workassistant.base.MyApplication;
import com.gzrijing.workassistant.entity.BusinessByLeader;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.DateUtil;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class InspectionStationFragment extends Fragment {

    private ProgressDialog pDialog;
    private String userNo;
    private List<BusinessByLeader> orderList = new ArrayList<BusinessByLeader>();
    private Handler handler = new Handler();
    private View layoutView;
    private ListView lv_order;
    private InspectionStationAdapter adapter;

    public InspectionStationFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        SharedPreferences app = getActivity().getSharedPreferences(
                "saveUser", getActivity().MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("action.com.gzrijing.workassistant.InspectionStationFragment");
        mIntentFilter.addAction("action.com.gzrijing.workassistant.InspectionStationFragment.distribute");
        mIntentFilter.addAction("action.com.gzrijing.workassistant.InspectionStationFragment.cancel");
        MyApplication.getContext().registerReceiver(mBroadcastReceiver, mIntentFilter);

        getBusiness();
    }

    private void getBusiness() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.setMessage("请保持网络连接，正在加载数据...");
        pDialog.show();
        String url = null;
        try {
            url = "?cmd=getconstruction&userno=" + URLEncoder.encode(userNo, "UTF-8") + "&begindate=";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response", response);
                List<BusinessByLeader> list = JsonParseUtils.getLeaderBusiness(response);
                orderList.clear();
                if (list.size() > 0) {
                    orderList.addAll(list);
                    if (orderList.size() > 1) {
                        sequence(orderList);
                        ArrayList<BusinessByLeader> BBLList = new ArrayList<BusinessByLeader>();
                        for (int i = 0; i < orderList.size(); i++) {
                            if (orderList.get(i).getState().equals("已完工")) {
                                BBLList.add(orderList.get(i));
                                orderList.remove(i);
                            }
                        }
                        if (BBLList.size() > 0) {
                            orderList.addAll(BBLList);
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                pDialog.dismiss();
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getActivity(), "与服务器断开连接", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                });
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layoutView = inflater.inflate(R.layout.fragment_inspection_station, container, false);

        initViews();

        return layoutView;

    }

    private void initViews() {
        lv_order = (ListView) layoutView.findViewById(R.id.fragment_inspection_station_lv);
        adapter = new InspectionStationAdapter(getActivity(), orderList, userNo);
        lv_order.setAdapter(adapter);
    }

    private void sequence(List<BusinessByLeader> orders) {
        Collections.sort(orders, new Comparator<BusinessByLeader>() {
            @Override
            public int compare(BusinessByLeader lhs, BusinessByLeader rhs) {
                Date date1 = DateUtil.stringToDate2(lhs.getReceivedTime());
                Date date2 = DateUtil.stringToDate2(rhs.getReceivedTime());
                // 对日期字段进行升序，如果欲降序可采用after方法
                if (date1.before(date2)) {
                    return 1;
                }
                return -1;
            }
        });
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("action.com.gzrijing.workassistant.InspectionStationFragment")){
                getBusiness();
            }

            if(action.equals("action.com.gzrijing.workassistant.InspectionStationFragment.distribute")){
                String orderId = intent.getStringExtra("orderId");
                for(BusinessByLeader order : orderList){
                    if(order.getOrderId().equals(orderId)){
                        order.setState("已派工");
                        order.setFlag("已派工");
                    }
                }
                adapter.notifyDataSetChanged();
            }

            if(action.equals("action.com.gzrijing.workassistant.InspectionStationFragment.cancel")){
                String orderId = intent.getStringExtra("orderId");
                for(BusinessByLeader order : orderList){
                    if(order.getOrderId().equals(orderId)){
                        order.setState("取消");
                        order.setFlag("取消");
                    }
                }
                adapter.notifyDataSetChanged();

            }

        }
    };

    @Override
    public void onDestroy() {
        MyApplication.getContext().unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
