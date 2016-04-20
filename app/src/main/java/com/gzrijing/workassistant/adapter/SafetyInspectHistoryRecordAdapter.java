package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.SafetyInspectHistoryRecordFailItem;

import java.util.ArrayList;

public class SafetyInspectHistoryRecordAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private ArrayList<SafetyInspectHistoryRecordFailItem> list;

    public SafetyInspectHistoryRecordAdapter(Context context, ArrayList<SafetyInspectHistoryRecordFailItem> list) {
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
                    R.layout.listview_item_safety_inspect_history_record, parent, false);
            v.failContent = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_history_record_fail_content_tv);
            v.handleName = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_history_record_fail_handle_name_tv);
            v.handleTime = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_history_record_fail_handle_time_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.failContent.setText(list.get(position).getFailContent());
        v.handleName.setText(list.get(position).getHandleName());
        v.handleTime.setText(list.get(position).getHandleTime());
        return convertView;
    }

    class ViewHolder {
        private TextView failContent;
        private TextView handleName;
        private TextView handleTime;
    }
}
