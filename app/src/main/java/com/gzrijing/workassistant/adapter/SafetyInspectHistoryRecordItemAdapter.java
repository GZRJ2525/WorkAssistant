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
import com.gzrijing.workassistant.entity.SafetyInspectHistoryRecord;
import com.gzrijing.workassistant.entity.SafetyInspectHistoryRecordFailItem;
import com.gzrijing.workassistant.view.SafetyInspectHistoryRecordActivity;

import java.util.ArrayList;

public class SafetyInspectHistoryRecordItemAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private ArrayList<SafetyInspectHistoryRecord> list;

    public SafetyInspectHistoryRecordItemAdapter(Context context, ArrayList<SafetyInspectHistoryRecord> list) {
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
                    R.layout.listview_item_safety_inspect_history_record_item, parent, false);
            v.submitTIme = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_history_record_item_time_tv);
            v.query = (Button) convertView.findViewById(R.id.listview_item_safety_inspect_history_record_item_query_btn);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.submitTIme.setText(position + 1 + "." + list.get(position).getSubmitDate());
        v.query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SafetyInspectHistoryRecordActivity.class);
                intent.putExtra("SafetyInspectHistoryRecord", list.get(position));
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder {
        private TextView submitTIme;
        private Button query;
    }
}
