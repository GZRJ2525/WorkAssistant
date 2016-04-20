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
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AcceptanceFragment extends Fragment implements View.OnClickListener {

    private View layoutView;
    private ListView lv_accList;
    private List<Acceptance> accList = new ArrayList<Acceptance>();
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
        adapter = new AcceptanceAdapter(getActivity(), accList);
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
        String orderId = et_orderId.getText().toString().trim();
        String url = null;
        try {
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
                        ArrayList<Acceptance> list = JsonParseUtils.getAcceptanceInfo(response);
                        accList.clear();
                        accList.addAll(list);
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
}
