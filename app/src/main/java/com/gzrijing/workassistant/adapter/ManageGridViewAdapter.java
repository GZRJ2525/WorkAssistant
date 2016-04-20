package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.SafetyInspectHistoryRecord;
import com.gzrijing.workassistant.view.AccidentsActivity;
import com.gzrijing.workassistant.view.BusinessHaveSendByAllActivity;
import com.gzrijing.workassistant.view.EntrustActivity;
import com.gzrijing.workassistant.view.LeaderMachineApplyBillListActivity;
import com.gzrijing.workassistant.view.LeaderMachineReturnBillListActivity;
import com.gzrijing.workassistant.view.SafetyInspectFailActivity;
import com.gzrijing.workassistant.view.SafetyInspectHistoryRecordQueryActivity;
import com.gzrijing.workassistant.view.SafetyInspectTaskActivity;
import com.gzrijing.workassistant.view.TrajectoryQueryActivity;

public class ManageGridViewAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater listContainer;
    private final int[] iconIds;
    private final String[] texts;

    public ManageGridViewAdapter(
            Context context, int[] iconIds, String[] texts) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.iconIds = iconIds;
        this.texts = texts;
    }

    @Override
    public int getCount() {
        return texts.length;
    }

    @Override
    public Object getItem(int position) {
        return texts[position];
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
                    R.layout.listview_item_manage_gridview, parent, false);
            v.icon = (ImageView) convertView.findViewById(R.id.manage_gridview_icon_iv);
            v.text = (TextView) convertView.findViewById(R.id.manage_gridview_text_tv);
            v.ll = (LinearLayout) convertView.findViewById(R.id.manage_gridview_ll);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }
        v.icon.setImageResource(iconIds[position]);
        v.text.setText(texts[position]);
        v.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position == 0){
                    Intent intent = new Intent(context, TrajectoryQueryActivity.class);
                    context.startActivity(intent);
                }

                if(position == 1){
                    Intent intent = new Intent(context, EntrustActivity.class);
                    context.startActivity(intent);
                }

                if(position == 2){
                    Intent intent = new Intent(context, AccidentsActivity.class);
                    context.startActivity(intent);
                }

                if(position == 3){
                    Intent intent = new Intent(context, LeaderMachineApplyBillListActivity.class);
                    context.startActivity(intent);
                }

                if(position == 4){
                    Intent intent = new Intent(context, LeaderMachineReturnBillListActivity.class);
                    context.startActivity(intent);
                }

                if(position == 5){
                    Intent intent = new Intent(context, BusinessHaveSendByAllActivity.class);
                    context.startActivity(intent);
                }

                if(position == 6){
                    Intent intent = new Intent(context, SafetyInspectTaskActivity.class);
                    context.startActivity(intent);
                }

                if(position == 7){
                    Intent intent = new Intent(context, SafetyInspectFailActivity.class);
                    context.startActivity(intent);
                }

                if(position == 8){
                    Intent intent = new Intent(context, SafetyInspectHistoryRecordQueryActivity.class);
                    context.startActivity(intent);
                }

            }
        });
        return convertView;
    }

    class ViewHolder {
        private ImageView icon;
        private TextView text;
        private LinearLayout ll;
    }
}
