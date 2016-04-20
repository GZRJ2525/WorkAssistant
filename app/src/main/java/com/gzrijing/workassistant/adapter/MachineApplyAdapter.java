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
import com.gzrijing.workassistant.entity.Machine;

import java.util.List;

public class MachineApplyAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private List<Machine> machineList;

    public MachineApplyAdapter(Context context, List<Machine> machineList) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.machineList = machineList;
    }

    @Override
    public int getCount() {
        return machineList.size();
    }

    @Override
    public Object getItem(int position) {
        return machineList.get(position);
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
                    R.layout.listview_item_machine_apply, parent, false);
            v.del = (ImageView) convertView.findViewById(R.id.listview_item_machine_apply_del_iv);
            v.name = (TextView) convertView.findViewById(R.id.listview_item_machine_apply_name_tv);
            v.unit = (TextView) convertView.findViewById(R.id.listview_item_machine_apply_unit_tv);
            v.num = (TextView) convertView.findViewById(R.id.listview_item_machine_apply_num_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.name.setText(machineList.get(position).getName());
        v.unit.setText(machineList.get(position).getUnit());
        v.num.setText(machineList.get(position).getApplyNum() + "");

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
                                machineList.get(position).setApplyNum(Integer.valueOf(et.getText().toString().trim()));
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
        machineList.remove(position);
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
        private TextView unit;
        private TextView num;
    }
}
