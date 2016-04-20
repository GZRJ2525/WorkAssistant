package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.ReportInfo;
import com.gzrijing.workassistant.view.ReportInfoProgressActivity;

import java.util.List;

public class ReportInfoByProgressAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private List<ReportInfo> list;

    public ReportInfoByProgressAdapter(Context context, List<ReportInfo> list) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.list = list;
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
                    R.layout.listview_item_report_info_by_progress, parent, false);
            v.content = (TextView) convertView.findViewById(R.id.listview_item_report_info_by_progress_content_tv);
            v.info = (Button) convertView.findViewById(R.id.listview_item_report_info_by_progress_info_btn);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        if (list.get(position).getContent().length() > 10) {
            v.content.setText((position + 1) + "." + list.get(position).getContent().substring(0, 10) + "...");
        } else {
            v.content.setText((position + 1) + "." + list.get(position).getContent());
        }

        v.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReportInfoProgressActivity.class);
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("fileNo", list.get(position).getFileNo());
                intent.putExtra("reportor", list.get(position).getReportor());
                intent.putExtra("reportTime", list.get(position).getReportTime());
                intent.putExtra("content", list.get(position).getContent());
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder {
        private TextView content;
        private Button info;
    }
}
