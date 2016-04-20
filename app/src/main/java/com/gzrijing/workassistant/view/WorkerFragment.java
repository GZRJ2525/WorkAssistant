package com.gzrijing.workassistant.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.BusinessWorkerAdapter;
import com.gzrijing.workassistant.db.BusinessData;
import com.gzrijing.workassistant.entity.BusinessByWorker;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.DateUtil;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import org.litepal.crud.DataSupport;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class WorkerFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private View layoutView;
    private Spinner sp_business;
    private Spinner sp_state;
    private String userNo;
    private ListView lv_order;
    private List<BusinessByWorker> orderList = new ArrayList<BusinessByWorker>();
    private BusinessWorkerAdapter adapter;
    private List<BusinessByWorker> orderListByWorker = new ArrayList<BusinessByWorker>();
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    adapter.notifyDataSetChanged();
                    Message message = handler.obtainMessage(0);
                    handler.sendMessageDelayed(message, 60 * 1000);
            }
            return false;
        }
    });

    public WorkerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_worker, container, false);

        initViews();
        setListeners();
        return layoutView;
    }

    private void initData() {
        SharedPreferences app = getActivity().getSharedPreferences(
                "saveUser", getActivity().MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        orderListByWorker.clear();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.com.gzrijing.workassistant.WorkerFragment");
        intentFilter.addAction("action.com.gzrijing.workassistant.WorkerFragment.state");
        intentFilter.addAction("action.com.gzrijing.workassistant.WorkerFragment.Inspection");
        intentFilter.addAction("action.com.gzrijing.workassistant.PipeInspectMap.add");
        intentFilter.addAction("action.com.gzrijing.workassistant.PipeInspectMap.update");
        intentFilter.addAction("action.com.gzrijing.workassistant.PipeInspectMap.inspection");
        intentFilter.addAction("action.com.gzrijing.workassistant.temInfoNum");
        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);

        getWorkerOreder();

        getInspectionOrder();

        getSewageWellsOrder();

    }

    private void getWorkerOreder(){
        List<BusinessData> list = DataSupport.where("user = ?", userNo).find(BusinessData.class);
        for (BusinessData data : list) {
            BusinessByWorker order = new BusinessByWorker();
            order.setId(data.getId());
            order.setOrderId(data.getOrderId());
            order.setUrgent(data.isUrgent());
            order.setType(data.getType());
            order.setState(data.getState());
            order.setReceivedTime(data.getReceivedTime());
            order.setDeadline(data.getDeadline());
            order.setFlag(data.getFlag());
            order.setTemInfoNum(data.getTemInfoNum());
            order.setRecordFileName(data.getRecordFileName());
            orderListByWorker.add(order);
        }
    }

    private void getInspectionOrder() {
        String url = null;
        try {
            url = "?cmd=GetPlanValve&userno=" + URLEncoder.encode(userNo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("阀门，消防栓计划", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<BusinessByWorker> list = JsonParseUtils.getInspection(response);
                        for(BusinessByWorker businessByWorker: list){
                            orderListByWorker.add(businessByWorker);
                            orderList.add(businessByWorker);
                        }
                        if(!orderList.toString().equals("[]")){
                            sequence(orderList);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getActivity(), "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });

    }

    private void getSewageWellsOrder() {
        String url = null;
        try {
            url = "?cmd=GetPlanSlop&userno=" + URLEncoder.encode(userNo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("污水井计划", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<BusinessByWorker> list = JsonParseUtils.getInspection(response);
                        for(BusinessByWorker businessByWorker: list){
                            orderListByWorker.add(businessByWorker);
                            orderList.add(businessByWorker);
                        }
                        if(!orderList.toString().equals("[]")){
                            sequence(orderList);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getActivity(), "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });

    }

    private void initViews() {
        sp_business = (Spinner) layoutView.findViewById(R.id.fragment_worker_business_sp);
        String[] businessItem = getResources().getStringArray(R.array.business_item);
        ArrayAdapter<String> businessAdater = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, businessItem);
        businessAdater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_business.setAdapter(businessAdater);

        sp_state = (Spinner) layoutView.findViewById(R.id.fragment_worker_state_sp);
        String[] stateItem = getResources().getStringArray(R.array.worker_fragment_state_item);
        ArrayAdapter<String> stateAdater = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, stateItem);
        stateAdater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_state.setAdapter(stateAdater);

        lv_order = (ListView) layoutView.findViewById(R.id.fragment_worker_order_lv);
        adapter = new BusinessWorkerAdapter(getActivity(), orderList, userNo);
        lv_order.setAdapter(adapter);

        Message message = handler.obtainMessage(0);
        handler.sendMessageDelayed(message, 60 * 1000);

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
            orderList.addAll(orderListByWorker);
        } else if (type.equals("全部") && !state.equals("全部")) {
            for(BusinessByWorker order : orderListByWorker){
                if(order.getState().equals(state)){
                    orderList.add(order);
                }
            }
        } else if (!type.equals("全部") && state.equals("全部")) {
            for(BusinessByWorker order : orderListByWorker){
                if(order.getType().equals(type)){
                    orderList.add(order);
                }
            }
        } else {
            for(BusinessByWorker order : orderListByWorker){
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

    private void sequence (List<BusinessByWorker> orders){
        Collections.sort(orders, new Comparator<BusinessByWorker>() {
            @Override
            public int compare(BusinessByWorker lhs, BusinessByWorker rhs) {
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
            if(action.equals("action.com.gzrijing.workassistant.WorkerFragment.Inspection")){
                String jsonData = intent.getStringExtra("jsonData");
                List<BusinessByWorker> list = JsonParseUtils.getInspection(jsonData);
                for(BusinessByWorker businessByWorker: list){
                    orderListByWorker.add(businessByWorker);
                    orderList.add(businessByWorker);
                }
                if(!orderList.toString().equals("[]")){
                    sequence(orderList);
                }
                adapter.notifyDataSetChanged();
            }

            if(action.equals("action.com.gzrijing.workassistant.WorkerFragment")){
                String jsonData = intent.getStringExtra("jsonData");
                List<BusinessByWorker> list = JsonParseUtils.getWorkerBusiness(jsonData);
                orderList.addAll(list);
                orderListByWorker.addAll(list);
                if(!orderList.toString().equals("[]")){
                    sequence(orderList);
                }
                adapter.notifyDataSetChanged();
            }
            if(action.equals("action.com.gzrijing.workassistant.PipeInspectMap.add")
                    || action.equals("action.com.gzrijing.workassistant.PipeInspectMap.update")
                    || action.equals("action.com.gzrijing.workassistant.PipeInspectMap.inspection")){
                orderListByWorker.clear();
                orderList.clear();
                getWorkerOreder();
                getInspectionOrder();
                getSewageWellsOrder();
            }

            if(action.equals("action.com.gzrijing.workassistant.WorkerFragment.state")){
                String orderId = intent.getStringExtra("orderId");
                String state = intent.getStringExtra("state");
                for(BusinessByWorker order : orderListByWorker){
                    if(orderId.equals(order.getOrderId())){
                        order.setState(state);
                    }
                }
                for(BusinessByWorker order : orderList){
                    if(orderId.equals(order.getOrderId())){
                        order.setState(state);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            if(action.equals("action.com.gzrijing.workassistant.temInfoNum")){
                String orderId = intent.getStringExtra("orderId");
                int num = intent.getIntExtra("temInfoNum", -1);
                for(BusinessByWorker order : orderListByWorker){
                    if(orderId.equals(order.getOrderId())){
                        order.setTemInfoNum(num);
                    }
                }
                for(BusinessByWorker order : orderList){
                    if(orderId.equals(order.getOrderId())){
                        order.setTemInfoNum(num);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

        }
    };

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
