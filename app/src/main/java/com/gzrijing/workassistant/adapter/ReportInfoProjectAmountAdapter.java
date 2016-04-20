package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.ReportInfoProjectAmount;
import com.gzrijing.workassistant.view.ReportInfoProjectAmountByOkActivity;
import com.gzrijing.workassistant.view.ReportInfoProjectAmountByWaitActivity;

import java.util.List;

public class ReportInfoProjectAmountAdapter extends BaseAdapter {

    private String orderId;
    private String togetherid;
    private Context context;
    private LayoutInflater listContainer;
    private List<ReportInfoProjectAmount> list;

    public ReportInfoProjectAmountAdapter(Context context, List<ReportInfoProjectAmount> list, String togetherid, String orderId) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.list = list;
        this.togetherid = togetherid;
        this.orderId = orderId;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
                    R.layout.listview_item_report_info_by_project_amount, parent, false);
            v.content = (TextView) convertView.findViewById(R.id.listview_item_report_info_by_project_amount_content_tv);
            v.approval = (TextView) convertView.findViewById(R.id.listview_item_report_info_by_project_amount_approval_btn);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.content.setText("    " + list.get(position).getFeeType() + "的工程量单");

        if (list.get(position).getState().equals("未审核")) {
            v.approval.setText("审核");
        } else {
            v.approval.setText("查看");
        }

        v.approval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getState().equals("未审核")) {
                    Intent intent = new Intent(context, ReportInfoProjectAmountByWaitActivity.class);
                    intent.putExtra("id", togetherid);
                    intent.putExtra("projectAmount", list.get(position));
                    intent.putExtra("orderId", orderId);
                    context.startActivity(intent);
                }

                if (list.get(position).getState().equals("已审核")) {
                    Intent intent = new Intent(context, ReportInfoProjectAmountByOkActivity.class);
                    intent.putExtra("id", togetherid);
                    intent.putExtra("projectAmount", list.get(position));
                    context.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        private TextView content;
        private TextView approval;
    }
}
