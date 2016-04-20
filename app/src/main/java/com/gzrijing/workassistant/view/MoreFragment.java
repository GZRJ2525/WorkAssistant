package com.gzrijing.workassistant.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.MoreGridViewAdapter;

public class MoreFragment extends Fragment {

    private View layoutView;
    private int[] iconIds;
    private String[] texts;
    private GridView gv_menu;

    public MoreFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_more, container, false);
        initViews();
        setListeners();
        return layoutView;
    }

    private void initData() {
        iconIds = new int[]{R.drawable.icon_notice, R.drawable.icon_send_machine, R.drawable.icon_back_machine,
                R.drawable.icon_setting};
        texts = new String[]{"通知公告", "送机列表", "退机列表", "　设置　"};
    }

    private void initViews() {
        gv_menu = (GridView) layoutView.findViewById(R.id.more_gv);
        MoreGridViewAdapter adapter = new MoreGridViewAdapter(getActivity(), iconIds, texts);
        gv_menu.setAdapter(adapter);
    }

    private void setListeners() {
        gv_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Intent intent = new Intent(getActivity(), NoticeActivity.class);
                    getActivity().startActivity(intent);
                }

                if(position == 1){
                    Intent intent = new Intent(getActivity(), SendMachineActivity.class);
                    getActivity().startActivity(intent);
                }

                if(position == 2){
                    Intent intent = new Intent(getActivity(), ReturnMachineActivity.class);
                    getActivity().startActivity(intent);
                }

                if(position == 3){
                    Intent intent = new Intent(getActivity(), SettingActivity.class);
                    getActivity().startActivity(intent);
                }

            }
        });
    }

}
