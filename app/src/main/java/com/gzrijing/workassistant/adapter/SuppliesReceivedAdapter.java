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

public class SuppliesReceivedAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private ArrayList<Supplies> applyingList;

    public SuppliesReceivedAdapter(Context context, ArrayList<Supplies> applyingList) {
        listContainer = LayoutInflater.from(context);
        this.applyingList = applyingList;
    }

    @Override
    public int getCount() {
        return applyingList.size();
    }

    @Override
    public Object getItem(int position) {
        return applyingList.get(position);
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
                    R.layout.listview_item_supplies_received, parent, false);
            v.name = (TextView) convertView.findViewById(R.id.listview_item_supplies_received_name_tv);
            v.spec = (TextView) convertView.findViewById(R.id.listview_item_supplies_received_spec_tv);
            v.unit = (TextView) convertView.findViewById(R.id.listview_item_supplies_received_unit_tv);
            v.sendNum = (TextView) convertView.findViewById(R.id.listview_item_supplies_received_send_num_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.name.setText(applyingList.get(position).getName());
        v.spec.setText(applyingList.get(position).getSpec());
        v.unit.setText(applyingList.get(position).getUnit());
        v.sendNum.setText(applyingList.get(position).getSendNum());
        return convertView;
    }

    class ViewHolder {
        private TextView name;
        private TextView spec;
        private TextView unit;
        private TextView sendNum;
    }
}
