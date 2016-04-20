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

public class SuppliesApplyReceivedAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private List<SuppliesNo> suppliesList;

    public SuppliesApplyReceivedAdapter(Context context, List<SuppliesNo> suppliesList) {
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
                    R.layout.listview_item_supplies_apply_received, parent, false);
            v.applyId = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_received_apply_id_tv);
            v.receivedId = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_received_id_tv);
            v.time = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_received_time_tv);
            v.state = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_received_state_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.applyId.setText(suppliesList.get(position).getApplyId());
        v.receivedId.setText(suppliesList.get(position).getReceivedId());
        v.time.setText(suppliesList.get(position).getReceivedTime());
        v.state.setText(suppliesList.get(position).getReceivedState());

        return convertView;
    }

    class ViewHolder {
        private TextView applyId;
        private TextView receivedId;
        private TextView time;
        private TextView state;
    }
}
