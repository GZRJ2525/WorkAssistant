package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.LeaderMachineReturnBill;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.gzrijing.workassistant.view.LeaderMachineReturnBillByInfoActivity;
import com.gzrijing.workassistant.view.LeaderMachineReturnBillByPlanActivity;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LeaderMachineReturnBillListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private ArrayList<LeaderMachineReturnBill> list;
    private int index = 0;
    private String userNo;
    private Handler handler = new Handler();

    public LeaderMachineReturnBillListAdapter(Context context, ArrayList<LeaderMachineReturnBill> list, String userNo) {
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
                    R.layout.listview_item_leader_machine_return_bill_list, parent, false);
            v.billNo = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_return_bill_list_id_tv);
            v.orderId = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_return_bill_list_order_id_tv);
            v.applyDate = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_return_bill_list_apply_date_tv);
            v.type = (TextView) convertView.findViewById(R.id.listview_item_leader_machine_return_bill_list_type_tv);
            v.plan = (Button) convertView.findViewById(R.id.listview_item_leader_machine_return_bill_list_plan_btn);
            v.info = (Button) convertView.findViewById(R.id.listview_item_leader_machine_return_bill_list_info_btn);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.billNo.setText(list.get(position).getBillNo());
        v.orderId.setText(list.get(position).getOrderId());
        v.applyDate.setText(list.get(position).getApplyDate());
        v.type.setText(list.get(position).getBillType());

        String state = list.get(position).getState();
        if (state.equals("未审核")) {//看json解析工具
            v.plan.setText("审核");
        }
        if (state.equals("已审核")) {//看json解析工具
            v.plan.setText("安排退机");
        }
        v.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LeaderMachineReturnBillByInfoActivity.class);
                intent.putExtra("bill", list.get(position));
                context.startActivity(intent);
            }
        });

        v.plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getState().equals("未审核")) {
                    submit(position, "1");//提交审核
                }
                if (list.get(position).getState().equals("已审核")) {
                    Intent intent = new Intent(context, LeaderMachineReturnBillByPlanActivity.class);
                    intent.putExtra("bill", list.get(position));
                    context.startActivity(intent);
                }
            }
        });
        return convertView;
    }


    private void submit(final int position, String isPass) {
        String reason = "";
        JSONArray jsonArray = new JSONArray();//卢工，接口21。因为客户要求没有申请“不通过”情况。所以调用时直接将isPass = "1",reason = ""
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
                        if (response.equals("ok")) {
                            list.get(position).setState("已审核");
                            notifyDataSetChanged();
                            ToastUtil.showToast(context, "审核成功", Toast.LENGTH_SHORT);
                        } else {
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
        private TextView type;
        private Button plan;
        private Button info;

    }
}
