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

public class SuppliesApplyApprovalAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private List<SuppliesNo> approvalList;

    public SuppliesApplyApprovalAdapter(Context context, List<SuppliesNo> approvalList) {
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
                    R.layout.listview_item_supplies_apply_approval, parent, false);
            v.id = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_approval_id_tv);
            v.time = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_approval_time_tv);
            v.state = (TextView) convertView.findViewById(R.id.listview_item_supplies_apply_approval_state_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.id.setText(approvalList.get(position).getApplyId());
        v.time.setText(approvalList.get(position).getApprovalTime());
        v.state.setText(approvalList.get(position).getApplyState());

        return convertView;
    }

    class ViewHolder {
        private TextView id;
        private TextView time;
        private TextView state;
    }
}
