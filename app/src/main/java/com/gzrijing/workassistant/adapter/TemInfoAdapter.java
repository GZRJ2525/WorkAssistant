package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.TemInfo;

import java.util.List;

public class TemInfoAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private List<TemInfo> list;

    public TemInfoAdapter(Context context, List<TemInfo> list) {
        listContainer = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
                    R.layout.listview_item_tem_info, parent, false);
            v.info = (TextView) convertView.findViewById(R.id.listview_item_tem_info_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.info.setText(position + 1 + "." + list.get(position).getMessage());
        return convertView;
    }

    class ViewHolder {
        private TextView info;
    }
}
