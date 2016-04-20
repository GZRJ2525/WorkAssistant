package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.Supplies;

import java.util.ArrayList;

public class SuppliesApprovalAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private ArrayList<Supplies> list;

    public SuppliesApprovalAdapter(Context context, ArrayList<Supplies> list) {
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
                    R.layout.listview_item_supplies_approval, parent, false);
            v.name = (TextView) convertView.findViewById(R.id.listview_item_supplies_approval_name_tv);
            v.spec = (TextView) convertView.findViewById(R.id.listview_item_supplies_approval_spec_tv);
            v.unit = (TextView) convertView.findViewById(R.id.listview_item_supplies_approval_unit_tv);
            v.applyNum = (TextView) convertView.findViewById(R.id.listview_item_supplies_approval_apply_num_tv);
            v.sendNum = (TextView) convertView.findViewById(R.id.listview_item_supplies_approval_send_num_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.name.setText(list.get(position).getName());
        v.spec.setText(list.get(position).getSpec());
        v.unit.setText(list.get(position).getUnit());
        v.applyNum.setText(list.get(position).getApplyNum());
        v.sendNum.setText(list.get(position).getSendNum());
        return convertView;
    }

    class ViewHolder {
        private TextView name;
        private TextView spec;
        private TextView unit;
        private TextView applyNum;
        private TextView sendNum;
    }
}
