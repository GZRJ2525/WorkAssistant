package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.SuppliesVerifyInfo;

import java.util.List;

public class SuppliesVerifyOkAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private List<SuppliesVerifyInfo> svInfoList;

    public SuppliesVerifyOkAdapter(Context context, List<SuppliesVerifyInfo> svInfoList) {
        listContainer = LayoutInflater.from(context);
        this.svInfoList = svInfoList;
    }

    @Override
    public int getCount() {
        return svInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return svInfoList.get(position);
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
                    R.layout.listview_item_supplies_verify_ok_info, parent, false);
            v.name = (TextView) convertView.findViewById(R.id.listview_item_supplies_verify_ok_info_name_tv);
            v.spec = (TextView) convertView.findViewById(R.id.listview_item_supplies_verify_ok_info_spec_tv);
            v.unit = (TextView) convertView.findViewById(R.id.listview_item_supplies_verify_ok_info_unit_tv);
            v.applyNum = (TextView) convertView.findViewById(R.id.listview_item_supplies_verify_ok_info_apply_num_tv);
            v.sendNum = (TextView) convertView.findViewById(R.id.listview_item_supplies_verify_ok_info_send_num_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.name.setText(svInfoList.get(position).getName());
        v.spec.setText(svInfoList.get(position).getSpec());
        v.unit.setText(svInfoList.get(position).getUnit());
        v.applyNum.setText(svInfoList.get(position).getApplyNum());
        v.sendNum.setText(svInfoList.get(position).getSendNum());

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
