package com.gzrijing.workassistant.view;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.BusinessDirectorAdapter;
import com.gzrijing.workassistant.base.MyApplication;
import com.gzrijing.workassistant.db.BusinessData;
import com.gzrijing.workassistant.entity.BusinessByLeader;
import com.gzrijing.workassistant.util.DateUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DirectorFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private String userNo;
    private View layoutView;
    private Spinner sp_business;
    private Spinner sp_state;
    private ListView lv_order;
    private List<BusinessByLeader> orderList = new ArrayList<BusinessByLeader>();
    private BusinessDirectorAdapter adapter;
    private List<BusinessByLeader> orderListByLeader = new ArrayList<BusinessByLeader>();

    public DirectorFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_leader, container, false);

        initViews();
        setListeners();
        return layoutView;
    }

    private void initData() {
        SharedPreferences app = getActivity().getSharedPreferences(
                "saveUser", getActivity().MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        getDBData();

        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("action.com.gzrijing.workassistant.LeaderFragment");
        mIntentFilter.addAction("action.com.gzrijing.workassistant.LeaderFragment.Distribute");
        mIntentFilter.addAction("action.com.gzrijing.workassistant.temInfoNum");
        MyApplication.getContext().registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    private void getDBData() {
        orderListByLeader.clear();
        List<BusinessData> list = DataSupport.where("user = ?", userNo).find(BusinessData.class);
        for (BusinessData data : list) {
            BusinessByLeader order = new BusinessByLeader();
            order.setId(data.getId());
            order.setOrderId(data.getOrderId());
            order.setUrgent(data.isUrgent());
            order.setType(data.getType());
            order.setState(data.getState());
            order.setReceivedTime(data.getReceivedTime());
            order.setDeadline(data.getDeadline());
            order.setTemInfoNum(data.getTemInfoNum());
            order.setFlag(data.getFlag());
            orderListByLeader.add(order);
        }
    }


    private void initViews() {
        sp_business = (Spinner) layoutView.findViewById(R.id.fragment_leader_business_sp);
        String[] businessItem = getResources().getStringArray(R.array.business_item);
        ArrayAdapter<String> businessAdater = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, businessItem);
        businessAdater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_business.setAdapter(businessAdater);

        sp_state = (Spinner) layoutView.findViewById(R.id.fragment_leader_state_sp);
        String[] stateItem = getResources().getStringArray(R.array.leader_fragment_state_item);
        ArrayAdapter<String> stateAdater = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, stateItem);
        stateAdater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_state.setAdapter(stateAdater);

        lv_order = (ListView) layoutView.findViewById(R.id.fragment_leader_order_lv);
        adapter = new BusinessDirectorAdapter(getActivity(), orderList, userNo);
        lv_order.setAdapter(adapter);
    }

    private void setListeners() {
        sp_business.setOnItemSelectedListener(this);
        sp_state.setOnItemSelectedListener(this);
    }

    private void select(View view){
        if(view != null){
            TextView tv = (TextView) view;
            tv.setTextColor(getResources().getColor(R.color.black));
        }
        String type = sp_business.getSelectedItem().toString();
        String state = sp_state.getSelectedItem().toString();

        orderList.clear();
        if (type.equals("全部") && state.equals("全部")) {
            orderList.addAll(orderListByLeader);
        } else if (type.equals("全部") && !state.equals("全部")) {
            for(BusinessByLeader order : orderListByLeader){
                if(order.getState().equals(state)){
                    orderList.add(order);
                }
            }
        } else if (!type.equals("全部") && state.equals("全部")) {
            for(BusinessByLeader order : orderListByLeader){
                if(order.getType().equals(type)){
                    orderList.add(order);
                }
            }
        } else {
            for(BusinessByLeader order : orderListByLeader){
                if(order.getType().equals(type) && order.getState().equals(state)){
                    orderList.add(order);
                }
            }
        }
        if(!orderList.toString().equals("[]")){
            sequence(orderList);
        }
        adapter.notifyDataSetChanged();
    }

    private void sequence (List<BusinessByLeader> orders){
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        select(view);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("action", action);
            if(action.equals("action.com.gzrijing.workassistant.LeaderFragment") ||
                    action.equals("action.com.gzrijing.workassistant.temInfoNum")){
                getDBData();
                orderList.clear();
                orderList.addAll(orderListByLeader);
                if(!orderList.toString().equals("[]")){
                    sequence(orderList);
                }
                adapter.notifyDataSetChanged();
            }

            if(action.equals("action.com.gzrijing.workassistant.LeaderFragment.Distribute")){
                String orderId = intent.getStringExtra("orderId");
                for(BusinessByLeader order : orderListByLeader){
                    if(order.getOrderId().equals(orderId)){
                        order.setState("已派工");
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
