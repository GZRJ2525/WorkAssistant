package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.MachineNo;

import java.util.List;

public class MachineApplyReturnAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private List<MachineNo> machineNoList;

    public MachineApplyReturnAdapter(Context context, List<MachineNo> machineNoList) {
        listContainer = LayoutInflater.from(context);
        this.machineNoList = machineNoList;
    }

    @Override
    public int getCount() {
        return machineNoList.size();
    }

    @Override
    public Object getItem(int position) {
        return machineNoList.get(position);
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
                    R.layout.listview_item_machine_apply_return, parent, false);
            v.id = (TextView) convertView.findViewById(R.id.listview_item_machine_apply_return_id_tv);
            v.time = (TextView) convertView.findViewById(R.id.listview_item_machine_apply_return_apply_time_tv);
            v.type = (TextView) convertView.findViewById(R.id.listview_item_machine_apply_return_type_tv);
            v.state = (TextView) convertView.findViewById(R.id.listview_item_machine_apply_return_state_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.id.setText(machineNoList.get(position).getReturnId());
        v.time.setText(machineNoList.get(position).getReturnApplyTime());
        v.type.setText(machineNoList.get(position).getReturnType());
        v.state.setText(machineNoList.get(position).getReturnState());

        return convertView;
    }

    class ViewHolder {
        private TextView id;
        private TextView time;
        private TextView type;
        private TextView state;
    }
}
