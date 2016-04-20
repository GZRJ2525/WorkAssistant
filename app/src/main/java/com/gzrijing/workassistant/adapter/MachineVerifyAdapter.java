package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.MachineVerify;

import java.util.ArrayList;

public class MachineVerifyAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private ArrayList<MachineVerify> mvList;

    public MachineVerifyAdapter(Context context, ArrayList<MachineVerify> mvList) {
        listContainer = LayoutInflater.from(context);
        this.mvList = mvList;
    }

    @Override
    public int getCount() {
        return mvList.size();
    }

    @Override
    public Object getItem(int position) {
        return mvList.get(position);
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
                    R.layout.listview_item_machine_verify, parent, false);
            v.id = (TextView) convertView.findViewById(R.id.listview_item_machine_verify_id_tv);
            v.applicant = (TextView) convertView.findViewById(R.id.listview_item_machine_verify_applicant_tv);
            v.time = (TextView) convertView.findViewById(R.id.listview_item_machine_verify_time_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.id.setText(mvList.get(position).getId());
        v.applicant.setText(mvList.get(position).getApplicant());
        v.time.setText(mvList.get(position).getUseTime());

        return convertView;
    }

    class ViewHolder {
        private TextView id;
        private TextView applicant;
        private TextView time;
    }
}
