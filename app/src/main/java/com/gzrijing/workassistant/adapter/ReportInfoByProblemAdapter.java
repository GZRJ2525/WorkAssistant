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
import com.gzrijing.workassistant.entity.BusinessHaveSend;
import com.gzrijing.workassistant.entity.ReportInfo;
import com.gzrijing.workassistant.view.ReportInfoProblemActivity;
import com.gzrijing.workassistant.view.ReportInfoProblemByProcessActivity;

import java.util.ArrayList;
import java.util.List;

public class ReportInfoByProblemAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private List<ReportInfo> list;
    private ArrayList<BusinessHaveSend> BHSList;

    public ReportInfoByProblemAdapter(Context context, List<ReportInfo> list, ArrayList<BusinessHaveSend> BHSList) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.list = list;
        this.BHSList = BHSList;
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
                    R.layout.listview_item_report_info_by_problem, parent, false);
            v.content = (TextView) convertView.findViewById(R.id.listview_item_report_info_by_problem_content_tv);
            v.info = (Button) convertView.findViewById(R.id.listview_item_report_info_by_problem_info_btn);
            v.process = (Button) convertView.findViewById(R.id.listview_item_report_info_by_problem_process_btn);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.content.setText(list.get(position).getContent());

        v.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReportInfoProblemActivity.class);
                intent.putExtra("problemInfo", list.get(position));
                context.startActivity(intent);
            }
        });

        v.process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReportInfoProblemByProcessActivity.class);
                intent.putParcelableArrayListExtra("BHSList", BHSList);
                intent.putExtra("fileNo", list.get(position).getFileNo());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView content;
        private Button info;
        private Button process;
    }
}
