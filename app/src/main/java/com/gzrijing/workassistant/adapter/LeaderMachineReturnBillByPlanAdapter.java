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
import com.gzrijing.workassistant.entity.LeaderMachineApplyBill;
import com.gzrijing.workassistant.entity.LeaderMachineApplyBillByMachine;
import com.gzrijing.workassistant.entity.LeaderMachineReturnBill;
import com.gzrijing.workassistant.entity.LeaderMachineReturnBillByMachine;
import com.gzrijing.workassistant.view.LeaderMachineApplyBillByMachineStateActivity;
import com.gzrijing.workassistant.view.LeaderMachineReturnBillByBackMachineActivity;

import java.util.ArrayList;

public class LeaderMachineReturnBillByPlanAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private LeaderMachineReturnBill bill;
    private ArrayList<LeaderMachineReturnBillByMachine> list;

    public LeaderMachineReturnBillByPlanAdapter(Context context, LeaderMachineReturnBill bill) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.bill = bill;
        this.list = bill.getMachineList();
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
                    R.layout.listview_item_leader_machine_return_bill_by_plan, parent, false);
            v.machineNo = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_return_bill_by_plan_machine_no_tv);
            v.machineName = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_return_bill_by_plan_machine_name_tv);
            v.num = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_return_bill_by_plan_num_tv);
            v.planOk = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_return_bill_by_plan_ok_tv);
            v.plan = (Button) convertView.findViewById(R.id.listview_item_leader_machine_return_bill_by_plan_btn);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.machineNo.setText(list.get(position).getMachineNo());
        v.machineName.setText(list.get(position).getName());
        v.num.setText(list.get(position).getNum());
        if(list.get(position).getFlag().equals("0")){
            v.plan.setVisibility(View.VISIBLE);
            v.planOk.setVisibility(View.GONE);
        }else {
            v.plan.setVisibility(View.GONE);
            v.planOk.setVisibility(View.VISIBLE);
        }

        v.plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LeaderMachineReturnBillByBackMachineActivity.class);
                intent.putExtra("bill", bill);
                intent.putExtra("machineNo", list.get(position).getMachineNo());
                intent.putExtra("machineName", list.get(position).getName());
                intent.putExtra("machinePosition", position);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView machineNo;
        private TextView machineName;
        private TextView num;
        private TextView planOk;
        private Button plan;

    }
}
