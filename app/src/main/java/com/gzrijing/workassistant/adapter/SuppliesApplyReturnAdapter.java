package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.Supplies;
import com.gzrijing.workassistant.entity.SuppliesNo;

import java.util.List;

public class SuppliesApplyReturnAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private List<SuppliesNo> suppliesList;

    public SuppliesApplyReturnAdapter(Context context, List<SuppliesNo> suppliesList) {
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
                    R.layout.listview_item_supplies_apply_return, parent, false);
            v.id = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_return_id_tv);
            v.time = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_return_time_tv);
            v.state = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_return_state_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.id.setText(suppliesList.get(position).getReturnId());
        v.time.setText(suppliesList.get(position).getReturnTime());
        v.state.setText(suppliesList.get(position).getReturnState());

        return convertView;
    }

    class ViewHolder {
        private TextView id;
        private TextView time;
        private TextView state;
    }
}
