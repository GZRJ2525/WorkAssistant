package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.LeaderMachineReturnBillByMachine;

import java.util.ArrayList;

public class LeaderMachineReturnBillByInfoAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private ArrayList<LeaderMachineReturnBillByMachine> list;

    public LeaderMachineReturnBillByInfoAdapter(Context context, ArrayList<LeaderMachineReturnBillByMachine> list) {
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
                    R.layout.listview_item_leader_machine_return_bill_by_info, parent, false);
            v.machineNo = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_return_bill_by_info_machine_no_tv);
            v.name = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_return_bill_by_info_machine_name_tv);
            v.unit = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_return_bill_by_info_machine_unit_tv);
            v.num = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_return_bill_by_info_machine_num_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.machineNo.setText(list.get(position).getMachineNo());
        v.name.setText(list.get(position).getName());
        v.unit.setText(list.get(position).getUnit());
        v.num.setText(list.get(position).getNum());

        return convertView;
    }

    class ViewHolder {
        private TextView machineNo;
        private TextView name;
        private TextView unit;
        private TextView num;
    }
}
