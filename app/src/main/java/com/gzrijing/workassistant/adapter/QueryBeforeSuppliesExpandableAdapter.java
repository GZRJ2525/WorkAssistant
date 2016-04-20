package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.OrderTypeSupplies;
import com.gzrijing.workassistant.entity.SafetyInspectFirstItem;
import com.gzrijing.workassistant.entity.SafetyInspectSecondItem;
import com.gzrijing.workassistant.entity.Supplies;

import java.util.ArrayList;

public class QueryBeforeSuppliesExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private ArrayList<OrderTypeSupplies> groupList;

    public QueryBeforeSuppliesExpandableAdapter(Context context,
                                                ArrayList<OrderTypeSupplies> groupList) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.groupList = groupList;
    }


    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groupList.get(groupPosition).getSuppliesList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupList.get(groupPosition).getSuppliesList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder v = null;
        if (convertView == null) {
            v = new GroupViewHolder();
            convertView = listContainer.inflate(
                    R.layout.elv_group_item_query_before_supplies, parent, false);
            v.check = (ImageView) convertView.findViewById(R.id.elv_group_item_query_before_supplies_check_iv);
            v.orderId = (TextView) convertView.findViewById(R.id.elv_group_item_query_before_supplies_order_id_tv);
            convertView.setTag(v);
        } else {
            v = (GroupViewHolder) convertView.getTag();
        }

        v.orderId.setText(groupList.get(groupPosition).getOrderId());
        if(groupList.get(groupPosition).isCheck()){
            v.check.setImageResource(R.drawable.login_checkbox_on);
        }else{
            v.check.setImageResource(R.drawable.login_checkbox_off);
        }

        v.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupList.get(groupPosition).isCheck()){
                   groupList.get(groupPosition).setCheck(false);
                }else{
                    for(OrderTypeSupplies group : groupList){
                        group.setCheck(false);
                    }
                    groupList.get(groupPosition).setCheck(true);
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder v = null;
        if (convertView == null) {
            v = new ChildViewHolder();
            convertView = listContainer.inflate(
                    R.layout.elv_child_item_query_before_supplies, parent, false);
            v.name = (TextView) convertView.findViewById(R.id.elv_child_item_query_before_supplies_name_tv);
            v.spec = (TextView) convertView.findViewById(R.id.elv_child_item_query_before_supplies_spec_tv);
            v.unit = (TextView) convertView.findViewById(R.id.elv_child_item_query_before_supplies_unit_tv);
            v.applyNum = (TextView) convertView.findViewById(R.id.elv_child_item_query_before_supplies_num_tv);
            convertView.setTag(v);
        } else {
            v = (ChildViewHolder) convertView.getTag();
        }

        Supplies child = groupList.get(groupPosition).getSuppliesList().get(childPosition);
        v.name.setText(child.getName());
        v.spec.setText(child.getSpec());
        v.unit.setText(child.getUnit());
        v.applyNum.setText(child.getApplyNum());

        return convertView;
    }

    class GroupViewHolder {
        private ImageView check;
        private TextView orderId;
    }

    class ChildViewHolder {
        private TextView name;
        private TextView spec;
        private TextView unit;
        private TextView applyNum;
    }
}
