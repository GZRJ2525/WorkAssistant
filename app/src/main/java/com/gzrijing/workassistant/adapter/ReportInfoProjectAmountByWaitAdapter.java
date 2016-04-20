package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.Supplies;

import java.util.List;

public class ReportInfoProjectAmountByWaitAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private List<Supplies> suppliesList;

    public ReportInfoProjectAmountByWaitAdapter(Context context, List<Supplies> suppliesList) {
        listContainer = LayoutInflater.from(context);
        this.suppliesList = suppliesList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return suppliesList.size();
    }

    @Override
    public Object getItem(int position) {
        return suppliesList.get(position);
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
                    R.layout.listview_item_supplies_supplies, parent, false);
            v.del = (ImageView) convertView.findViewById(R.id.listview_item_supplies_supplies_del_iv);
            v.name = (TextView) convertView.findViewById(R.id.listview_item_supplies_supplies_name_tv);
            v.spec = (TextView) convertView.findViewById(R.id.listview_item_supplies_supplies_spec_tv);
            v.unit = (TextView) convertView.findViewById(R.id.listview_item_supplies_supplies_unit_tv);
            v.num = (TextView) convertView.findViewById(R.id.listview_item_supplies_supplies_num_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.name.setText(suppliesList.get(position).getName());
        v.spec.setText(suppliesList.get(position).getSpec());
        v.unit.setText(suppliesList.get(position).getUnit());
        v.num.setText(suppliesList.get(position).getNum());

        v.num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText et = new EditText(context);
                et.setTextColor(context.getResources().getColor(R.color.black));
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
               final AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("请输入数量")
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                suppliesList.get(position).setNum(et.getText().toString().trim());
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", null).show();
                et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });
            }
        });

        v.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(position);
            }
        });

        return convertView;
    }

    private void delete(int position) {
        suppliesList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }

    class ViewHolder {
        private ImageView del;
        private TextView name;
        private TextView spec;
        private TextView unit;
        private TextView num;
    }
}
