package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.DetailedInfo;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.entity.QueryProjectAmount;
import com.gzrijing.workassistant.view.ImageBrowserForHttpActivity;
import com.gzrijing.workassistant.view.QueryProjectAmountByInfoActivity;
import com.gzrijing.workassistant.view.QueryProjectAmountByInfoModifyActivity;

import java.util.ArrayList;
import java.util.List;

public class QueryProjectAmountAdapter extends BaseAdapter {

    private String orderId;
    private Context context;
    private LayoutInflater listContainer;
    private List<QueryProjectAmount> list;

    public QueryProjectAmountAdapter(Context context, List<QueryProjectAmount> list, String orderId) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.list = list;
        this.orderId = orderId;
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
                    R.layout.listview_item_query_project_amount, parent, false);
            v.content = (TextView) convertView.findViewById(
                    R.id.listview_item_query_project_amount_content_tv);
            v.query = (Button) convertView.findViewById(
                    R.id.listview_item_query_project_amount_query_btn);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.content.setText(list.get(position).getFeeType());
        v.query.setText(list.get(position).getState());

        v.query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(position).getState().equals("不通过")){
                    Intent intent = new Intent(context, QueryProjectAmountByInfoModifyActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("projectAmount", list.get(position));
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, QueryProjectAmountByInfoActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("projectAmount", list.get(position));
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView content;
        private Button query;
    }
}
