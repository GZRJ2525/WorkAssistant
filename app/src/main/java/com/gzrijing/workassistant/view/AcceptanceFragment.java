package com.gzrijing.workassistant.view;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.AcceptanceAdapter;
import com.gzrijing.workassistant.entity.Acceptance;
import com.gzrijing.workassistant.entity.DetailedInfo;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.MyComparator;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AcceptanceFragment extends Fragment implements View.OnClickListener {

    private View layoutView;
    private ListView lv_accList;
    private List<Acceptance> accList = new ArrayList<Acceptance>();//用来存放服务器返回的完工工程的所有信息，适配器将这些数据显示出来。
    private AcceptanceAdapter adapter;
    private String userNo;
    private Handler handler = new Handler();
    private EditText et_orderId;
    private Button btn_query;

    public AcceptanceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences app = getActivity().getSharedPreferences(
                "saveUser", getActivity().MODE_PRIVATE);
        userNo = app.getString("userNo", "");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_acceptance, container, false);
        initViews();
        setListeners();
        return layoutView;
    }

    private void initViews() {
        et_orderId = (EditText) layoutView.findViewById(R.id.fragment_acceptance_orderId_id_et);
        btn_query = (Button) layoutView.findViewById(R.id.fragment_acceptance_query_btn);

        lv_accList = (ListView) layoutView.findViewById(R.id.fragment_acceptance_lv);
        adapter = new AcceptanceAdapter(getActivity(), accList);//将解析出的完工列表accList交给适配器处理!! 在此处过滤数据，将符合要求的数据传给acclist
        lv_accList.setAdapter(adapter);
    }

    private void setListeners() {
        btn_query.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_acceptance_query_btn:
                query();
                break;
        }
    }

    private void query() {
        String orderId = et_orderId.getText().toString().trim();//输入的工程编号
        String url = null;
        try {//卢工接口57．获取工程的所有信息，是否完工都可以查询
            url = "?cmd=getfinishconstruction&userno=" + URLEncoder.encode(userNo, "UTF-8")
                    + "&fileno=" + URLEncoder.encode(orderId, "UTF-8") + "&enddate=&isfinish=1";
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
                        ArrayList<Acceptance> list = JsonParseUtils.getAcceptanceInfo(response);//解析服务器返回的信息
                        //新创建一个ArrayList<Acceptance> orderList,按完工时间倒序排序
                        ArrayList<Acceptance> orderList = getOderList(list);
                        accList.clear();
                        accList.addAll(orderList);
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

    public ArrayList<Acceptance> getOderList(ArrayList<Acceptance> list) {
        /*将完工工程按完工日期倒序排列*/
        ArrayList<String> finishTimes = new ArrayList<>();//finishTimes 所有的完工日期
        ArrayList<String> NewFormatfinishTimes = new ArrayList<>();// NewFormatfinishTimes 所有的完工日期转化日期格式后
        ArrayList<Acceptance> oderList = new ArrayList<Acceptance>();//oderList 按完工日期倒序排列后
        DateFormat format1 = new SimpleDateFormat("yyyy/M/ddHH:mm:ss");//服务器返回的日期格式
        DateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss");//用于比较排序的日期格式

        //遍历完工工程,获得工程详情
        outer:
        for (int i = 0; i < list.size(); i++) {
            String finishTime = "";//完工日期
            Acceptance acceptance = list.get(i);
            ArrayList<DetailedInfo> detailedInfos = acceptance.getDetailedInfos();
            //遍历完工工程详情，获得完工日期，将没有填写完工日期的工程忽略！
            for (int j = 0; j < detailedInfos.size(); j++) {
                DetailedInfo detailedInfo = detailedInfos.get(j);
                String key = detailedInfo.getKey();
                if ("完工日期".equals(key)) {
//                    //获取完工日期
                    finishTime = detailedInfo.getValue();
                    Log.e("finishTme", finishTime);
                    if (finishTime.length() != 0) {// 有的可能没有填写，做判断。
                        finishTimes.add(finishTime);
                        continue outer;
                    } else {//没有填写“完工日期”
                        continue outer;
                    }
                }
            }

        }
        //将完工日期转化日期格式，准备排序
        for (int k = 0; k < finishTimes.size(); k++) {
            try {
                Date finishTime1 = format1.parse(finishTimes.get(k));
                String NewFormatfinishTime = format2.format(finishTime1);
                Log.e("NewFormatfinishTime", NewFormatfinishTime);
                NewFormatfinishTimes.add(NewFormatfinishTime);
            } catch (Exception e) {

            }

        }
        //排序
        Collections.sort(NewFormatfinishTimes, new MyComparator());

        //遍历排序后的新格式完工日期，依次和从服务器拿到的完工日期比较，完工日期相同就将对应的工程放在对应的位置。
        outer1:
        for (int m = 0; m < NewFormatfinishTimes.size(); m++) {//遍历排位序的所有完工日期
            String newFormatfinishTime = NewFormatfinishTimes.get(m);
            //获取完工日期
            outer2:
            for (int n = 0; n < list.size(); n++) {
                Acceptance acceptance = list.get(n);
                ArrayList<DetailedInfo> detailedInfos = acceptance.getDetailedInfos();
                for (int p = 0; p < detailedInfos.size(); p++) {
                    DetailedInfo detailedInfo = detailedInfos.get(p);
                    String key = detailedInfo.getKey();
                    if ("完工日期".equals(key)) {
                        String finishTime = detailedInfo.getValue();
//                            Log.e("finishTime", finishTime);
                        if (finishTime.length() != 0) {
                            try {
                                Date finishiTimeDate = format1.parse(finishTime);
                                String newFormatFinishTime = format2.format(finishiTimeDate);
                                if (newFormatFinishTime.equals(newFormatfinishTime)) {//字符串比较
                                    oderList.add(list.get(n));//依次放入
                                    Log.e("tenDaysList", oderList.toString());
                                    continue outer1;
                                } else {
                                    continue outer2;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return oderList;
    }
}


