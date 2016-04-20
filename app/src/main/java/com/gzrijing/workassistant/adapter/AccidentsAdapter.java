package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.Accident;
import com.gzrijing.workassistant.entity.QueryProjectAmount;
import com.gzrijing.workassistant.view.AccidentsProcessActivity;
import com.gzrijing.workassistant.view.QueryProjectAmountByInfoActivity;

import java.util.List;

public class AccidentsAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private List<Accident> list;

    public AccidentsAdapter(Context context, List<Accident> list) {
        this.context = context;
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
                    R.layout.listview_item_accidents, parent, false);
            v.content = (TextView) convertView.findViewById(
                    R.id.listview_item_accidents_content_tv);
            v.process = (Button) convertView.findViewById(
                    R.id.listview_item_accidents_process_btn);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.content.setText(list.get(position).getReason());
        v.process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AccidentsProcessActivity.class);
                intent.putExtra("accident", list.get(position));
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView content;
        private Button process;
    }
}
