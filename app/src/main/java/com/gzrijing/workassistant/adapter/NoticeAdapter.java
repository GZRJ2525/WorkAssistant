package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.Notice;

import java.util.List;

public class NoticeAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private List<Notice> list;

    public NoticeAdapter(Context context, List<Notice> list) {
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
                    R.layout.listview_item_notice, parent, false);
            v.title = (TextView) convertView.findViewById(R.id.listview_item_notice_title_tv);
            v.promulgator = (TextView) convertView.findViewById(R.id.listview_item_notice_promulgator_tv);
            v.date = (TextView) convertView.findViewById(R.id.listview_item_notice_date_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }
        v.title.setText(list.get(position).getTitle());
        v.promulgator.setText(list.get(position).getDepartment()+"："+list.get(position).getPromulgator());
        v.date.setText(list.get(position).getDate());
        return convertView;
    }

    class ViewHolder {
        private TextView title;
        private TextView promulgator;
        private TextView date;
    }
}
