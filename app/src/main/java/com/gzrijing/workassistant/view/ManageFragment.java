package com.gzrijing.workassistant.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.ManageGridViewAdapter;

public class ManageFragment extends Fragment {


    private View layoutView;
    private GridView gv_menu;
    private int[] iconIds;
    private String[] texts;

    public ManageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_manage, container, false);
        initViews();
        setListeners();
        return layoutView;
    }

    private void initData() {
        iconIds = new int[]{R.drawable.icon_trajectory_query, R.drawable.icon_entrust, R.drawable.icon_accidents,
                R.drawable.icon_apply_machine, R.drawable.icon_return_machine, R.drawable.icon_business_have_send,
                R.drawable.icon_safety_inspect, R.drawable.icon_safety_inspect_fail, R.drawable.icon_safety_inspect};
        texts = new String[]{"轨迹查询", "代班功能", "意外情况", "机械申请单", "机械退机单", "已派任务", "安全检查\n", "安全检查不合格项", "安全检查历史查询"};
    }

    private void initViews() {
        gv_menu = (GridView) layoutView.findViewById(R.id.manage_gv);
        ManageGridViewAdapter adapter = new ManageGridViewAdapter(getActivity(), iconIds, texts);
        gv_menu.setAdapter(adapter);
    }

    private void setListeners() {

    }


}
