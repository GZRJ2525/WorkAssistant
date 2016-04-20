package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.MachineVerifyInfo;

import java.util.List;

public class MachineVerifyOkAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private List<MachineVerifyInfo> mvInfoList;

    public MachineVerifyOkAdapter(Context context, List<MachineVerifyInfo> mvInfoList) {
        listContainer = LayoutInflater.from(context);
        this.mvInfoList = mvInfoList;
    }

    @Override
    public int getCount() {
        return mvInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mvInfoList.get(position);
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
                    R.layout.listview_item_machine_verify_ok_info, parent, false);
            v.name = (TextView) convertView.findViewById(R.id.listview_item_machine_verify_ok_info_name_tv);
            v.applyNum = (TextView) convertView.findViewById(R.id.listview_item_machine_verify_ok_info_apply_num_tv);
            v.sendNum = (TextView) convertView.findViewById(R.id.listview_item_machine_verify_ok_info_send_num_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.name.setText(mvInfoList.get(position).getName());
        v.applyNum.setText(mvInfoList.get(position).getApplyNum());
        v.sendNum.setText(mvInfoList.get(position).getSendNum());

        return convertView;
    }

    class ViewHolder {
        private TextView name;
        private TextView applyNum;
        private TextView sendNum;
    }
}
