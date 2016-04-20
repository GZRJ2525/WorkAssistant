package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.Supplies;

import java.util.List;

public class SuppliesApplyCreatedAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private List<Supplies> suppliesList;

    public SuppliesApplyCreatedAdapter(Context context, List<Supplies> suppliesList) {
        listContainer = LayoutInflater.from(context);
        this.suppliesList = suppliesList;
    }

    @Override
    public int getCount() {
        return suppliesList.size();
    }

    @Override
    public Object getItem(int position) {
        return suppliesList.get(position);
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
                    R.layout.listview_item_supplies_apply_created, parent, false);
            v.name = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_created_name_tv);
            v.spec = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_created_spec_tv);
            v.unit = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_created_unit_tv);
            v.num = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_created_num_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.name.setText(suppliesList.get(position).getName());
        v.spec.setText(suppliesList.get(position).getSpec());
        v.unit.setText(suppliesList.get(position).getUnit());
        v.num.setText(suppliesList.get(position).getApplyNum());

        return convertView;
    }

    class ViewHolder {
        private TextView name;
        private TextView spec;
        private TextView unit;
        private TextView num;
    }
}
