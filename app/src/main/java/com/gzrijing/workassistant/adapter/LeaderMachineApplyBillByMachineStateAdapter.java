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
import com.gzrijing.workassistant.entity.LeaderMachineState;
import com.gzrijing.workassistant.view.LeaderMachineApplyBillBySendMachineActivity;

import java.util.ArrayList;

public class LeaderMachineApplyBillByMachineStateAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private ArrayList<LeaderMachineState> list;
    private LeaderMachineApplyBill bill;

    public LeaderMachineApplyBillByMachineStateAdapter(Context context, ArrayList<LeaderMachineState> list,
                                                       LeaderMachineApplyBill bill) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.list = list;
        this.bill = bill;
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
                    R.layout.listview_item_leader_machine_apply_bill_by_machine_state, parent, false);
            v.businessNo = (TextView)convertView.findViewById(R.id.listview_item_leader_machine_apply_bill_by_machine_state_business_no_tv);//yycq 初始化使用该机械的对应工程编号
            v.machineNo = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_apply_bill_by_machine_state_no_tv);
            v.machineName = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_apply_bill_by_machine_state_name_tv);
            v.state = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_apply_bill_by_machine_state_state_tv);
            v.plan = (Button) convertView.findViewById(R.id.listview_item_leader_machine_apply_bill_by_machine_state_plan_btn);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }
        v.businessNo.setText(list.get(position).getBusinessNo());//yycq 添加
        v.machineNo.setText(list.get(position).getMachineNo());
        v.machineName.setText(list.get(position).getMachineName());
        v.state.setText(list.get(position).getState());

        if(list.get(position).getState().equals("正常")){
            v.state.setTextColor(convertView.getResources().getColor(R.color.blue));
            v.plan.setText("安排");
        }else if(list.get(position).getState().equals("损坏")){
            v.state.setTextColor(convertView.getResources().getColor(R.color.red));
            v.plan.setText("");
        }else {
            v.state.setTextColor(convertView.getResources().getColor(R.color.blue));
            v.plan.setText("调派");
        }

        final String flag = v.plan.getText().toString();
        v.plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag.equals("安排")){
                    Intent intent = new Intent(context, LeaderMachineApplyBillBySendMachineActivity.class);
                    intent.putExtra("machineNo", list.get(position).getMachineNo());
                    intent.putExtra("machineName", list.get(position).getMachineName());
                    intent.putExtra("getAddress", list.get(position).getAddress());
                    intent.putExtra("bill", bill);
                    intent.putExtra("flag", flag);
                    context.startActivity(intent);
                }

                if(flag.equals("调派")){
                    Intent intent = new Intent(context, LeaderMachineApplyBillBySendMachineActivity.class);
                    intent.putExtra("machineNo", list.get(position).getMachineNo());
                    intent.putExtra("machineName", list.get(position).getMachineName());
                    intent.putExtra("getAddress", list.get(position).getAddress());
                    intent.putExtra("bill", bill);
                    intent.putExtra("flag", flag);
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView businessNo;//yycq 定义工程编号tv
        private TextView machineNo;
        private TextView machineName;
        private TextView state;
        private Button plan;

    }
}
