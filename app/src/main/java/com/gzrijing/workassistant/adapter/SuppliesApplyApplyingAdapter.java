package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.SuppliesNo;

import java.util.List;

public class SuppliesApplyApplyingAdapter extends BaseAdapter {
    private LayoutInflater listContainer;
    private List<SuppliesNo> applyingList;

    public SuppliesApplyApplyingAdapter(Context context, List<SuppliesNo> applyingList) {
        listContainer = LayoutInflater.from(context);
        this.applyingList = applyingList;
    }

    @Override
    public int getCount() {
        return applyingList.size();
    }

    @Override
    public Object getItem(int position) {
        return applyingList.get(position);
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
                    R.layout.listview_item_supplies_apply_applying, parent, false);
            v.applyId = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_applying_id_tv);
            v.applyTime = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_applying_time_tv);
            v.state = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_applying_state_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }
        v.applyId.setText(applyingList.get(position).getApplyId());
        v.applyTime.setText(applyingList.get(position).getApplyTime());
        v.state.setText(applyingList.get(position).getApplyState());

        return convertView;
    }

    class ViewHolder {
        private TextView applyId;
        private TextView applyTime;
        private TextView state;
    }
}
