package com.gzrijing.workassistant.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.SortByWorkerNameExpandableAdapter;
import com.gzrijing.workassistant.entity.BusinessHaveSendByAll;
import com.gzrijing.workassistant.entity.Subordinate;
import com.gzrijing.workassistant.util.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class SortByWorkerNameFragment extends Fragment {

    private View layoutView;
    private ArrayList<BusinessHaveSendByAll> businessList;
    private ArrayList<Subordinate> subordinateList;
    private ExpandableListView elv_business;

    public SortByWorkerNameFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    private void initData() {
        Bundle bundle = getArguments();
        businessList = bundle.getParcelableArrayList("businessList");
        subordinateList = bundle.getParcelableArrayList("subordinateList");

        for (Subordinate subordinate : subordinateList) {
            for (BusinessHaveSendByAll business : businessList) {
                if (business.getWorkerName().indexOf(",") == -1
                        && subordinate.getName().equals(business.getWorkerName())) {
                    subordinate.getBusinessList().add(business);
                } else {
                    String[] str = business.getWorkerName().split(",");
                    for (int i = 0; i < str.length; i++) {
                        if (str[i].equals(subordinate.getName())) {
                            subordinate.getBusinessList().add(business);
                        }
                    }
                }
            }
        }
        sequence();
    }

    private void sequence() {
        if (subordinateList != null && subordinateList.size() > 1) {
            Collections.sort(subordinateList, new Comparator<Subordinate>() {
                @Override
                public int compare(Subordinate lhs, Subordinate rhs) {
                    int size1 = lhs.getBusinessList().size();
                    int size2 = rhs.getBusinessList().size();
                    if (size1 > size2) {
                        return 1;
                    }
                    return -1;
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_sort_by_worker_name, container, false);

        initViews();

        return layoutView;
    }

    private void initViews() {
        elv_business = (ExpandableListView) layoutView.findViewById(R.id.sort_by_worker_name_fragment_elv);
        SortByWorkerNameExpandableAdapter adapter = new SortByWorkerNameExpandableAdapter(getActivity(), subordinateList);
        elv_business.setAdapter(adapter);
    }

}
