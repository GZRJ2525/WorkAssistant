package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.BusinessHaveSend;
import com.gzrijing.workassistant.view.ReportInfoByProblemActivity;
import com.gzrijing.workassistant.view.ReportInfoByProgressActivity;
import com.gzrijing.workassistant.view.ReportInfoByProjectAmountActivity;

import java.util.ArrayList;

public class BusinessHaveSendAdapter extends BaseAdapter {
    private String orderId;
    private Context context;
    private LayoutInflater listContainer;
    private ArrayList<BusinessHaveSend> BHSList;

    public BusinessHaveSendAdapter(Context context, ArrayList<BusinessHaveSend> BHSList, String orderId) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.BHSList = BHSList;
        this.orderId = orderId;
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
                    R.layout.listview_item_business_have_send, parent, false);
            v.orderId = (TextView) convertView.findViewById(
                    R.id.listview_item_business_have_send_order_id_tv);
            v.content = (TextView) convertView.findViewById(
                    R.id.listview_item_business_have_send_content_tv);
            v.state = (TextView) convertView.findViewById(
                    R.id.listview_item_business_have_send_state_tv);
            v.executors = (TextView) convertView.findViewById(
                    R.id.listview_item_business_have_send_executors_tv);
            v.deadline = (TextView) convertView.findViewById(
                    R.id.listview_item_business_have_send_deadline_tv);
            v.progress = (TextView) convertView.findViewById(
                    R.id.listview_item_business_have_send_progress_report_info_tv);
            v.problem = (TextView) convertView.findViewById(
                    R.id.listview_item_business_have_send_problem_report_info_tv);
            v.projectAmount = (TextView) convertView.findViewById(
                    R.id.listview_item_business_have_send_project_amount_report_info_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.orderId.setText(BHSList.get(position).getId());
        v.content.setText(BHSList.get(position).getContent());
        v.state.setText(BHSList.get(position).getState());
        v.executors.setText(BHSList.get(position).getExecutors());
        v.deadline.setText(BHSList.get(position).getDeadline());
        v.progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReportInfoByProgressActivity.class);
                intent.putExtra("id", BHSList.get(position).getId());
                context.startActivity(intent);
            }
        });

        v.problem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReportInfoByProblemActivity.class);
                intent.putParcelableArrayListExtra("BHSList", BHSList);
                intent.putExtra("id", BHSList.get(position).getId());
                context.startActivity(intent);
            }
        });

        v.projectAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReportInfoByProjectAmountActivity.class);
                intent.putExtra("id", BHSList.get(position).getId());
                intent.putExtra("orderId", orderId);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView orderId;
        private TextView content;
        private TextView state;
        private TextView executors;
        private TextView deadline;
        private TextView progress;
        private TextView problem;
        private TextView projectAmount;
    }
}
