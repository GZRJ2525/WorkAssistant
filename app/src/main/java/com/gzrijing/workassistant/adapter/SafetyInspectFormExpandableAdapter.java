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
import com.gzrijing.workassistant.entity.SafetyInspectFirstItem;
import com.gzrijing.workassistant.entity.SafetyInspectSecondItem;

import java.util.ArrayList;

public class SafetyInspectFormExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private ArrayList<SafetyInspectFirstItem> groupList;

    public SafetyInspectFormExpandableAdapter(Context context,
                                              ArrayList<SafetyInspectFirstItem> groupList) {
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
        return groupList.get(groupPosition).getChildList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupList.get(groupPosition).getChildList().get(childPosition);
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
                    R.layout.elv_group_item_safety_inspect_form, parent, false);
            v.iv = (ImageView) convertView.findViewById(R.id.elv_group_item_safety_inspect_form_iv);
            v.name = (TextView) convertView.findViewById(R.id.elv_group_item_safety_inspect_form_tv);
            convertView.setTag(v);
        } else {
            v = (GroupViewHolder) convertView.getTag();
        }
        v.name.setText(groupList.get(groupPosition).getName());
        if(isExpanded){
            v.iv.setImageResource(R.drawable.icon_arrow_down);
        }else {
            v.iv.setImageResource(R.drawable.icon_red_dot);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder v = null;
        if (convertView == null) {
            v = new ChildViewHolder();
            convertView = listContainer.inflate(
                    R.layout.elv_child_item_safety_inspect_form, parent, false);
            v.ll = (LinearLayout) convertView.findViewById(R.id.elv_child_item_safety_inspect_form_ll);
            v.iv = (ImageView) convertView.findViewById(R.id.elv_child_item_safety_inspect_form_iv);
            v.name = (TextView) convertView.findViewById(R.id.elv_child_item_safety_inspect_form_tv);
            convertView.setTag(v);
        } else {
            v = (ChildViewHolder) convertView.getTag();
        }
        final SafetyInspectSecondItem child = groupList.get(groupPosition).getChildList().get(childPosition);
        v.name.setText(child.getName());
        if(child.isCheck()){
            v.iv.setImageResource(R.drawable.login_checkbox_on);
        }else {
            v.iv.setImageResource(R.drawable.login_checkbox_off);
        }

        if(!child.isSubmit()){
            v.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    child.setCheck(!child.isCheck());
                    notifyDataSetChanged();
                }
            });
        }

        return convertView;
    }

    class GroupViewHolder {
        private ImageView iv;
        private TextView name;
    }

    class ChildViewHolder {
        private LinearLayout ll;
        private ImageView iv;
        private TextView name;
    }
}
