package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.Subordinate;
import com.gzrijing.workassistant.view.PipeInspectionFormActivity;
import com.gzrijing.workassistant.view.SubordinateActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SubordinateAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private List<Subordinate> subordinates;
    private ImageView iv_checkAll;
    private TextView tv_selected;
    private HashMap<Integer, String> names;

    public SubordinateAdapter(
            Context context, List<Subordinate> subordinates, ImageView iv_checkAll,
            TextView tv_selected, HashMap<Integer, String> names) {
        listContainer = LayoutInflater.from(context);
        this.subordinates = subordinates;
        this.iv_checkAll = iv_checkAll;
        this.tv_selected = tv_selected;
        this.names = names;
    }

    @Override
    public int getCount() {
        return subordinates.size();
    }

    @Override
    public Object getItem(int position) {
        return subordinates.get(position);
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
                    R.layout.listview_item_subordinate, parent, false);
            v.ll = (LinearLayout) convertView.findViewById(R.id.listview_item_subordinate_ll);
            v.checkBox = (ImageView) convertView.findViewById(R.id.listview_item_subordinate_checkbox_iv);
            v.name = (TextView) convertView.findViewById(R.id.listview_item_subordinate_name_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        final boolean isCheck = subordinates.get(position).isCheck();
        if (isCheck) {
            v.checkBox.setImageResource(R.drawable.login_checkbox_on);
        } else {
            v.checkBox.setImageResource(R.drawable.login_checkbox_off);
        }

        v.name.setText(subordinates.get(position).getName());
        v.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheck) {
                    subordinates.get(position).setCheck(false);
                    names.remove(position);
                } else {
                    subordinates.get(position).setCheck(true);
                    names.put(position, subordinates.get(position).getName());
                }
                notifyDataSetChanged();
            }
        });

        Iterator iter = names.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String name = (String) entry.getValue();
            sb.append(name + "; ");
        }
        if(!sb.toString().equals("")){
            sb.delete(sb.lastIndexOf(";"), sb.length());
        }
        tv_selected.setText(sb.toString());

        for (Subordinate name : subordinates) {
            if (!name.isCheck()) {
                iv_checkAll.setImageResource(R.drawable.login_checkbox_off);
                SubordinateActivity.isCheck = false;
                return convertView;
            }
        }
        iv_checkAll.setImageResource(R.drawable.login_checkbox_on);
        SubordinateActivity.isCheck = true;
        return convertView;
    }

    class ViewHolder {
        private LinearLayout ll;
        private ImageView checkBox;
        private TextView name;
    }
}
