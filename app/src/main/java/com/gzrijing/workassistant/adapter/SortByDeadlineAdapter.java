package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.BusinessHaveSendByAll;
import com.gzrijing.workassistant.view.ReportInfoByProblemActivity;
import com.gzrijing.workassistant.view.ReportInfoByProgressActivity;
import com.gzrijing.workassistant.view.ReportInfoByProjectAmountActivity;

import java.util.ArrayList;

public class SortByDeadlineAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater listContainer;
    private ArrayList<BusinessHaveSendByAll> BHSList;

    public SortByDeadlineAdapter(Context context, ArrayList<BusinessHaveSendByAll> BHSList) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.BHSList = BHSList;
    }

    @Override
    public int getCount() {
        return BHSList.size();
    }

    @Override
    public Object getItem(int position) {
        return BHSList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder v = null;
        if (convertView == null) {
            v = new ViewHolder();
            convertView = listContainer.inflate(
                    R.layout.listview_item_sort_by_deadline, parent, false);
            v.orderId = (TextView) convertView.findViewById(
                    R.id.listview_item_sort_by_deadline_order_id_tv);
            v.content = (TextView) convertView.findViewById(
                    R.id.listview_item_sort_by_deadline_content_tv);
            v.executors = (TextView) convertView.findViewById(
                    R.id.listview_item_sort_by_deadline_executors_tv);
            v.deadline = (TextView) convertView.findViewById(
                    R.id.listview_item_sort_by_deadline_deadline_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.orderId.setText(BHSList.get(position).getOrderId() + "——" + BHSList.get(position).getBranchId());
        v.content.setText(BHSList.get(position).getWorkContent());
        v.executors.setText(BHSList.get(position).getWorkerName());
        v.deadline.setText(BHSList.get(position).getDeadline());

        return convertView;
    }

    class ViewHolder {
        private TextView orderId;
        private TextView content;
        private TextView executors;
        private TextView deadline;
    }
}
