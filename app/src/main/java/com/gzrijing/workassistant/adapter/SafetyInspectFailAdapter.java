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
import com.gzrijing.workassistant.entity.SafetyInspectFail;
import com.gzrijing.workassistant.entity.SafetyInspectTask;
import com.gzrijing.workassistant.view.SafetyInspectFailItemActivity;
import com.gzrijing.workassistant.view.SafetyInspectFormActivity;
import com.gzrijing.workassistant.view.SafetyInspectMapActivity;
import com.gzrijing.workassistant.view.SafetyInspectRecordActivity;
import com.gzrijing.workassistant.view.SafetyInspectUploadImageActivity;

import java.util.ArrayList;

public class SafetyInspectFailAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private ArrayList<SafetyInspectFail> list;

    public SafetyInspectFailAdapter(Context context, ArrayList<SafetyInspectFail> list) {
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
                    R.layout.listview_item_safety_inspect_fail, parent, false);
            v.id = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_fail_order_id_tv);
            v.name = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_fail_name_tv);
            v.type = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_fail_type_tv);
            v.fail = (Button) convertView.findViewById(R.id.listview_item_safety_inspect_fail_fail_btn);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.id.setText(list.get(position).getOrderId());
        v.name.setText(list.get(position).getName());
        v.type.setText(list.get(position).getType());
        v.fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SafetyInspectFailItemActivity.class);
                intent.putExtra("orderId", list.get(position).getOrderId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView id;
        private TextView name;
        private TextView type;
        private Button fail;
    }
}
