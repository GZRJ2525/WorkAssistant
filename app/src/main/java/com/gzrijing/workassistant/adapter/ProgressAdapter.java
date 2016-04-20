package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.Progress;
import com.gzrijing.workassistant.entity.Progress;

import java.util.List;

public class ProgressAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater listContainer;
    private List<Progress> proInfos;

    public ProgressAdapter(
            Context context, List<Progress> proInfos) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.proInfos = proInfos;
    }

    @Override
    public int getCount() {
        return proInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return proInfos.get(position);
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
                    R.layout.listview_item_progress, parent, false);
            v.time = (TextView) convertView.findViewById(
                    R.id.listview_item_progress_time_tv);
            v.progress = (ImageView) convertView.findViewById(
                    R.id.listview_item_progress_line_iv);
            v.content = (TextView) convertView.findViewById(
                    R.id.listview_item_progress_content_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }
        v.time.setText(proInfos.get(position).getTime());
        v.content.setText(proInfos.get(position).getContent());
        if(position == 0){
            v.progress.setImageResource(R.drawable.bg_progress_top);
        }else if((position+1) == proInfos.size()){
            v.progress.setImageResource(R.drawable.bg_progress_bottom);
        }else{
            v.progress.setImageResource(R.drawable.bg_progress_centre);
        }
        return convertView;
    }

    class ViewHolder {
        private TextView time;
        private ImageView progress;
        private TextView content;
    }
}
