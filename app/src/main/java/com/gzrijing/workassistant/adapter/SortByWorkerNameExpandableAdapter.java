package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.BusinessHaveSendByAll;
import com.gzrijing.workassistant.entity.Subordinate;

import java.util.ArrayList;

public class SortByWorkerNameExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private ArrayList<Subordinate> groupList;

    public SortByWorkerNameExpandableAdapter(Context context, ArrayList<Subordinate> groupList) {
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
        return groupList.get(groupPosition).getBusinessList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupList.get(groupPosition).getBusinessList().get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder v = null;
        if (convertView == null) {
            v = new GroupViewHolder();
            convertView = listContainer.inflate(
                    R.layout.elv_group_item_sort_by_worker_name, parent, false);
            v.image = (ImageView) convertView.findViewById(R.id.elv_group_item_sort_by_worker_name_iv);
            v.name = (TextView) convertView.findViewById(R.id.elv_group_item_sort_by_worker_name_name_tv);
            v.num = (TextView) convertView.findViewById(R.id.elv_group_item_sort_by_worker_name_num_tv);
            convertView.setTag(v);
        } else {
            v = (GroupViewHolder) convertView.getTag();
        }

        v.name.setText(groupList.get(groupPosition).getName());
        v.num.setText(groupList.get(groupPosition).getBusinessList().size()+"个任务");

        if(isExpanded){
            v.image.setImageResource(R.drawable.icon_arrow_down);
        }else {
            v.image.setImageResource(R.drawable.icon_red_dot);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder v = null;
        if (convertView == null) {
            v = new ChildViewHolder();
            convertView = listContainer.inflate(
                    R.layout.elv_child_item_sort_by_worker_name, parent, false);
            v.orderId = (TextView) convertView.findViewById(R.id.elv_child_item_sort_by_worker_name_order_id_tv);
            v.content = (TextView) convertView.findViewById(R.id.elv_child_item_sort_by_worker_name_content_tv);
            v.deadline = (TextView) convertView.findViewById(R.id.elv_child_item_sort_by_worker_name_deadline_tv);
            convertView.setTag(v);
        } else {
            v = (ChildViewHolder) convertView.getTag();
        }

        BusinessHaveSendByAll child = groupList.get(groupPosition).getBusinessList().get(childPosition);
        v.orderId.setText(child.getOrderId()+"————"+child.getBranchId());
        v.content.setText(child.getWorkContent());
        v.deadline.setText(child.getDeadline());

        return convertView;
    }

    class GroupViewHolder {
        private ImageView image;
        private TextView name;
        private TextView num;
    }

    class ChildViewHolder {
        private TextView orderId;
        private TextView content;
        private TextView deadline;
    }
}
