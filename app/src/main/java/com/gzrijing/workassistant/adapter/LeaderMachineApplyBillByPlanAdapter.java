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
import com.gzrijing.workassistant.view.LeaderMachineApplyBillByMachineStateActivity;

import java.util.ArrayList;

public class LeaderMachineApplyBillByPlanAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private LeaderMachineApplyBill bill;
    private ArrayList<LeaderMachineApplyBillByMachine> list;

    public LeaderMachineApplyBillByPlanAdapter(Context context, LeaderMachineApplyBill bill) {
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
                    R.layout.listview_item_leader_machine_apply_bill_by_plan, parent, false);
            v.name = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_apply_bill_by_plan_name_tv);
            v.applyNum = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_apply_bill_by_plan_apply_num_tv);
            v.sendNum = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_apply_bill_by_plan_send_num_tv);
            v.planOk = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_apply_bill_by_plan_ok_tv);
            v.plan = (Button) convertView.findViewById(R.id.listview_item_leader_machine_apply_bill_by_plan_btn);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.name.setText(list.get(position).getName());
        v.applyNum.setText(list.get(position).getApplyNum());
        v.sendNum.setText(list.get(position).getSendNum());

        int applyNum = Integer.valueOf(list.get(position).getApplyNum());
        int sendNum = Integer.valueOf(list.get(position).getSendNum());
        if(applyNum - sendNum == 0){
            v.planOk.setVisibility(View.VISIBLE);
            v.plan.setVisibility(View.GONE);
        }else{
            v.planOk.setVisibility(View.GONE);
            v.plan.setVisibility(View.VISIBLE);
        }

        v.plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LeaderMachineApplyBillByMachineStateActivity.class);
                intent.putExtra("bill", bill);
                intent.putExtra("machineName", list.get(position).getName());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView name;
        private TextView applyNum;
        private TextView sendNum;
        private TextView planOk;
        private Button plan;

    }
}
