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

public class MachineApprovalAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private List<Machine> approvalList;

    public MachineApprovalAdapter(Context context, List<Machine> approvalList) {
        listContainer = LayoutInflater.from(context);
        this.approvalList = approvalList;
    }

    @Override
    public int getCount() {
        return approvalList.size();
    }

    @Override
    public Object getItem(int position) {
        return approvalList.get(position);
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
                    R.layout.listview_item_machine_approval, parent, false);
            v.name = (TextView) convertView.findViewById(R.id.listview_item_machine_approval_name_tv);
            v.unit = (TextView) convertView.findViewById(R.id.listview_item_machine_approval_unit_tv);
            v.applyNum = (TextView) convertView.findViewById(R.id.listview_item_machine_approval_apply_num_tv);
            v.sendNum = (TextView) convertView.findViewById(R.id.listview_item_machine_approval_send_num_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.name.setText(approvalList.get(position).getName());
        v.unit.setText(approvalList.get(position).getUnit());
        v.applyNum.setText(approvalList.get(position).getApplyNum() + "");
        v.sendNum.setText(approvalList.get(position).getSendNum() + "");
        return convertView;
    }

    class ViewHolder {
        private TextView name;
        private TextView unit;
        private TextView applyNum;
        private TextView sendNum;
    }
}
