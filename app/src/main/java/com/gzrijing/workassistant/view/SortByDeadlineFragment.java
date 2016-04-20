package com.gzrijing.workassistant.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.SortByDeadlineAdapter;
import com.gzrijing.workassistant.entity.BusinessHaveSendByAll;
import com.gzrijing.workassistant.util.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class SortByDeadlineFragment extends Fragment {

    private View layoutView;
    private ListView lv_business;
    private ArrayList<BusinessHaveSendByAll> businessList;

    public SortByDeadlineFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    private void initData() {
        Bundle bundle = getArguments();
        businessList = bundle.getParcelableArrayList("businessList");
        sequence();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_sort_by_deadline, container, false);

        initViews();

        return layoutView;
    }

    private void initViews() {
        lv_business = (ListView) layoutView.findViewById(R.id.sort_by_deadline_fragment_lv);
        SortByDeadlineAdapter adapter = new SortByDeadlineAdapter(getActivity(), businessList);
        lv_business.setAdapter(adapter);
    }

    private void sequence() {
        if (businessList != null && businessList.size() > 1) {
            Collections.sort(businessList, new Comparator<BusinessHaveSendByAll>() {
                @Override
                public int compare(BusinessHaveSendByAll lhs, BusinessHaveSendByAll rhs) {
                    Date date1 = DateUtil.stringToDate2(lhs.getDeadline());
                    Date date2 = DateUtil.stringToDate2(rhs.getDeadline());
                    // 对日期字段进行升序，如果欲降序可采用after方法
                    if (date1.after(date2)) {
                        return 1;
                    }
                    return -1;
                }
            });
        }
    }

}
