package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.LeaderMachineApplyBill;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.gzrijing.workassistant.view.LeaderMachineApplyBillByInfoActivity;
import com.gzrijing.workassistant.view.LeaderMachineApplyBillByPlanActivity;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LeaderMachineApplyBillListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private ArrayList<LeaderMachineApplyBill> list;
    private int index = 0;
    private String userNo;
    private Handler handler = new Handler();

    public LeaderMachineApplyBillListAdapter(Context context, ArrayList<LeaderMachineApplyBill> list, String userNo) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.list = list;
        this.userNo = userNo;
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
                    R.layout.listview_item_leader_machine_apply_bill_list, parent, false);
            v.billNo = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_apply_bill_list_id_tv);
            v.orderId = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_apply_bill_list_order_id_tv);
            v.applyDate = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_apply_bill_list_apply_date_tv);
            v.plan = (Button) convertView.findViewById(R.id.listview_item_leader_machine_apply_bill_list_plan_btn);
            v.info = (Button) convertView.findViewById(R.id.listview_item_leader_machine_apply_bill_list_info_btn);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.billNo.setText(list.get(position).getBillNo());
        v.orderId.setText(list.get(position).getOrderId());
        v.applyDate.setText(list.get(position).getApplyDate());
        String state = list.get(position).getState();
        if(state.equals("未审核")){
           v.plan.setText("审核");
        }
        if(state.equals("已审核")){
            v.plan.setText("安排送机");
        }
        if(state.equals("不通过")){
            v.plan.setText("不通过");
        }

        v.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LeaderMachineApplyBillByInfoActivity.class);
                intent.putExtra("bill", list.get(position));
                context.startActivity(intent);
            }
        });

        v.plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(position).getState().equals("未审核")){
                    newDialog(position);
                }
                if(list.get(position).getState().equals("已审核")){
                    Intent intent = new Intent(context, LeaderMachineApplyBillByPlanActivity.class);
                    intent.putExtra("bill", list.get(position));
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    private void newDialog(final int position) {
        final EditText et = new EditText(context);
        et.setVisibility(View.GONE);
        new AlertDialog.Builder(context).setTitle("选择类型").setView(et).setSingleChoiceItems(
                new String[]{"通过", "不通过"}, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            et.setVisibility(View.GONE);
                        }
                        if(which == 1){
                            et.setVisibility(View.VISIBLE);
                            et.setTextColor(context.getResources().getColor(R.color.black));
                            et.setTextSize(context.getResources().getDimension(R.dimen.sw_15dp));
                            et.setHint("请填写不通过原因");
                            et.setHintTextColor(context.getResources().getColor(R.color.grey_hint));
                        }
                        index = which;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (index == 0) {
                            submit(position, "1", et);
                        }
                        if (index == 1) {
                            submit(position, "0", et);
                        }
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private void submit(final int position, String isPass, EditText et) {
        String reason = "";
        if(isPass.equals("0")){
            reason = et.getText().toString().trim();
        }
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("BillNo", list.get(position).getBillNo());
            jsonObject.put("IsPass", isPass);
            jsonObject.put("UnPassReason", reason);
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "docheckmachineneed")
                .add("userno", userNo)
                .add("machineneedjson", jsonArray.toString())
                .build();

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(response.equals("ok")){
                            list.get(position).setState("已审核");
                            notifyDataSetChanged();
                            ToastUtil.showToast(context, "审核成功", Toast.LENGTH_SHORT);
                        }else{
                            ToastUtil.showToast(context, "审核失败", Toast.LENGTH_SHORT);
                        }
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(context, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    class ViewHolder {
        private TextView billNo;
        private TextView orderId;
        private TextView applyDate;
        private Button plan;
        private Button info;

    }
}
