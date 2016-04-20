package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.Machine;

import java.util.List;

public class MachineApplyReceivedAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private List<Machine> machineList;

    public MachineApplyReceivedAdapter(Context context, List<Machine> machineList) {
        listContainer = LayoutInflater.from(context);
        this.machineList = machineList;
    }

    @Override
    public int getCount() {
        return machineList.size();
    }

    @Override
    public Object getItem(int position) {
        return machineList.get(position);
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
                    R.layout.listview_item_machine_apply_received, parent, false);
            v.id = (TextView) convertView.findViewById(R.id.listview_item_machine_apply_received_id_tv);
            v.name = (TextView) convertView.findViewById(R.id.listview_item_machine_apply_received_name_tv);
            v.state = (TextView) convertView.findViewById(R.id.listview_item_machine_apply_received_state_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.id.setText(machineList.get(position).getId());
        v.name.setText(machineList.get(position).getName());
        v.state.setText(machineList.get(position).getState());

        return convertView;
    }

    class ViewHolder {
        private TextView id;
        private TextView name;
        private TextView state;
    }
}
