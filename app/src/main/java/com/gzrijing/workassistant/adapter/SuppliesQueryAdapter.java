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

public class SuppliesQueryAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private List<Supplies> suppliesQueries;

    public SuppliesQueryAdapter(Context context, List<Supplies> suppliesQueries) {
        listContainer = LayoutInflater.from(context);
        this.suppliesQueries = suppliesQueries;
    }

    @Override
    public int getCount() {
        return suppliesQueries.size();
    }

    @Override
    public Object getItem(int position) {
        return suppliesQueries.get(position);
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
                    R.layout.listview_item_supplies_query, parent, false);
            v.name = (TextView) convertView.findViewById(R.id.listview_item_supplies_query_name_tv);
            v.spec = (TextView) convertView.findViewById(R.id.listview_item_supplies_query_spec_tv);
            v.unit = (TextView) convertView.findViewById(R.id.listview_item_supplies_query_unit_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.name.setText(suppliesQueries.get(position).getName());
        v.spec.setText(suppliesQueries.get(position).getSpec());
        v.unit.setText(suppliesQueries.get(position).getUnit());

        return convertView;
    }

    class ViewHolder {
        private TextView name;
        private TextView spec;
        private TextView unit;
    }
}
