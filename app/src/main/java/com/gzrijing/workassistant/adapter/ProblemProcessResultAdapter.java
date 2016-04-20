package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.ProblemResult;

import java.util.ArrayList;

public class ProblemProcessResultAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private ArrayList<ProblemResult> list;

    public ProblemProcessResultAdapter(Context context, ArrayList<ProblemResult> list) {
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
                    R.layout.listview_item_problem_process_result, parent, false);
            v.reason = (TextView) convertView.findViewById(
                    R.id.listview_item_problem_process_result_reason_tv);
            v.result = (TextView) convertView.findViewById(
                    R.id.listview_item_problem_process_result_result_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.reason.setText(list.get(position).getReason());
        v.result.setText(list.get(position).getResult());

        return convertView;
    }

    class ViewHolder {
        private TextView reason;
        private TextView result;
    }
}
