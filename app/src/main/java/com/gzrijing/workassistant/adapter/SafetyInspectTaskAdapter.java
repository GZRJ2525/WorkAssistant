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
import com.gzrijing.workassistant.entity.SafetyInspectTask;
import com.gzrijing.workassistant.view.SafetyInspectFormActivity;
import com.gzrijing.workassistant.view.SafetyInspectHistoryRecordActivity;
import com.gzrijing.workassistant.view.SafetyInspectHistoryRecordItemActivity;
import com.gzrijing.workassistant.view.SafetyInspectMapActivity;
import com.gzrijing.workassistant.view.SafetyInspectRecordActivity;
import com.gzrijing.workassistant.view.SafetyInspectUploadImageActivity;

import java.util.ArrayList;

public class SafetyInspectTaskAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private ArrayList<SafetyInspectTask> list;

    public SafetyInspectTaskAdapter(Context context, ArrayList<SafetyInspectTask> list) {
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
                    R.layout.listview_item_safety_inspect_task, parent, false);
            v.id = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_task_id_tv);
            v.name = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_task_name_tv);
            v.type = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_task_type_tv);
            v.inspect = (Button) convertView.findViewById(R.id.listview_item_safety_inspect_task_inspect_btn);
            v.location = (Button) convertView.findViewById(R.id.listview_item_safety_inspect_task_location_btn);
            v.record = (Button) convertView.findViewById(R.id.listview_item_safety_inspect_task_record_btn);
            v.upload = (Button) convertView.findViewById(R.id.listview_item_safety_inspect_task_upload_pic_btn);
            v.historyRecord = (Button) convertView.findViewById(R.id.listview_item_safety_inspect_task_history_record_btn);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.id.setText(list.get(position).getId());
        v.name.setText(list.get(position).getName());
        v.type.setText(list.get(position).getType());
        v.inspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SafetyInspectFormActivity.class);
                intent.putExtra("orderId", list.get(position).getId());
                context.startActivity(intent);
            }
        });

        v.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SafetyInspectMapActivity.class);
                intent.putExtra("longitude", list.get(position).getLongitude());
                intent.putExtra("latitude", list.get(position).getLatitude());
                context.startActivity(intent);
            }
        });

        v.record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SafetyInspectRecordActivity.class);
                intent.putExtra("orderId", list.get(position).getId());
                context.startActivity(intent);
            }
        });

        v.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SafetyInspectUploadImageActivity.class);
                intent.putExtra("orderId", list.get(position).getId());
                context.startActivity(intent);
            }
        });

        v.historyRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SafetyInspectHistoryRecordItemActivity.class);
                intent.putExtra("orderId", list.get(position).getId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView id;
        private TextView name;
        private TextView type;
        private Button inspect;
        private Button location;
        private Button record;
        private Button upload;
        private Button historyRecord;

    }
}
