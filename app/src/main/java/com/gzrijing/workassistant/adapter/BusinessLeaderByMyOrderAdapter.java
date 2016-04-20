package com.gzrijing.workassistant.adapter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.BusinessByWorker;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.receiver.NotificationReceiver;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.gzrijing.workassistant.view.BusinessLeaderByMyOrderDetailedInfoActivity;
import com.gzrijing.workassistant.view.BusinessLeaderByMyOrderSuppliesApplyActivity;
import com.gzrijing.workassistant.view.GetGPSActivity;
import com.gzrijing.workassistant.view.PipeInspectionMapActivity;
import com.gzrijing.workassistant.view.QueryProjectAmountActivity;
import com.gzrijing.workassistant.view.ReportActivity;
import com.gzrijing.workassistant.view.TemInfoActivity;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BusinessLeaderByMyOrderAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater listContainer;
    private List<BusinessByWorker> orderList;
    private String userNo;
    private Handler handler = new Handler();

    public BusinessLeaderByMyOrderAdapter(Context context, List<BusinessByWorker> orderList, String userNo) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.orderList = orderList;
        this.userNo = userNo;
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
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
                    R.layout.listview_item_business_worker, parent, false);
            v.orderId = (TextView) convertView.findViewById(
                    R.id.listview_item_business_worker_order_id_tv);
            v.urgent = (ImageView) convertView.findViewById(
                    R.id.listview_item_business_worker_urgent_iv);
            v.type = (TextView) convertView.findViewById(
                    R.id.listview_item_business_worker_type_tv);
            v.state = (TextView) convertView.findViewById(
                    R.id.listview_item_business_worker_state_tv);
            v.receivedTime = (TextView) convertView.findViewById(
                    R.id.listview_item_business_worker_received_time_tv);
            v.deadline = (TextView) convertView.findViewById(
                    R.id.listview_item_business_worker_deadline_tv);
            v.temInfo = (TextView) convertView.findViewById(
                    R.id.listview_item_business_worker_tem_info_tv);
            v.suppliesApply = (Button) convertView.findViewById(
                    R.id.listview_item_business_worker_supplies_apply_btn);
            v.info = (Button) convertView.findViewById(
                    R.id.listview_item_business_worker_info_btn);
            v.gps = (Button) convertView.findViewById(
                    R.id.listview_item_business_worker_gps_btn);
            v.queryProjectAmount = (Button) convertView.findViewById(
                    R.id.listview_item_business_worker_project_amount_btn);
            v.safetyFail = (Button) convertView.findViewById(
                    R.id.listview_item_business_worker_safety_inspect_fail_btn);
            v.flag = (TextView) convertView.findViewById(
                    R.id.listview_item_business_worker_flag_tv);
            v.head_rl = (RelativeLayout) convertView.findViewById(
                    R.id.listview_item_business_worker_head_rl);
            v.bg_ll = (LinearLayout) convertView.findViewById(
                    R.id.listview_item_business_worker_bg_ll);
            v.btn_rl = (RelativeLayout) convertView.findViewById(
                    R.id.listview_item_business_worker_rl);
            v.timeLeft = (TextView) convertView.findViewById(
                    R.id.listview_item_business_time_left_tv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.orderId.setText(orderList.get(position).getOrderId());
        v.type.setText(orderList.get(position).getType());
        v.state.setText(orderList.get(position).getState());
        v.receivedTime.setText(orderList.get(position).getReceivedTime());
        v.deadline.setText(orderList.get(position).getDeadline());
        v.safetyFail.setVisibility(View.GONE);

        String endTime = orderList.get(position).getDeadline();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = sdf.parse(endTime);
            long time = d.getTime() - System.currentTimeMillis();
            long day = time / (1000 * 60 * 60 * 24);
            long dayNm = 0;
            if (day > 0) {
                dayNm = day;
            }
            long hour = (time - day * 1000 * 60 * 60 * 24) / (1000 * 60 * 60);
            long hourNm = 0;
            if (hour > 0) {
                hourNm = hour;
            }
            long min = (time - day * 1000 * 60 * 60 * 24 - hour * 1000 * 60 * 60) / (1000 * 60);
            long minNm = 0;
            if (min > 0) {
                minNm = min;
            }
            String surpTime = dayNm + "天" + hourNm + "时" + minNm + "分";
            if (dayNm == 0 && hourNm < 4 && hourNm > 1) {
                v.timeLeft.setText(surpTime);
                v.head_rl.setBackgroundColor(convertView.getResources().getColor(R.color.orangeShallow));
            } else if (dayNm == 0 && hourNm < 2) {
                v.timeLeft.setText(surpTime);
                v.head_rl.setBackgroundColor(convertView.getResources().getColor(R.color.redShallow));
            }else{
                v.timeLeft.setText(surpTime);
                v.head_rl.setBackgroundColor(convertView.getResources().getColor(R.color.blue));
            }
            if (dayNm == 0 && hourNm == 1 && minNm == 59) {
                NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                Intent intent = new Intent(context, NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = new NotificationCompat.Builder(context)
                        .setContentText("你有一条快过期的任务")
                        .setTicker("你有一条快过期的任务")
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                        .build();
                notification.defaults = Notification.DEFAULT_SOUND;
                notification.flags = Notification.FLAG_INSISTENT;
                manager.notify(1, notification);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (orderList.get(position).getType().equals("供水管网巡查")) {
            v.info.setVisibility(View.GONE);
            v.suppliesApply.setVisibility(View.GONE);
            v.queryProjectAmount.setVisibility(View.GONE);
            v.gps.setVisibility(View.GONE);
        }
        if (orderList.get(position).isUrgent()) {
            v.urgent.setVisibility(View.VISIBLE);
        } else {
            v.urgent.setVisibility(View.GONE);
        }
        final String flag = orderList.get(position).getFlag();
        v.flag.setText(flag);
        if(flag.equals("确认收到") || flag.equals("处理")){
            v.bg_ll.setBackgroundResource(R.color.pink_bg);
        }else{
            v.bg_ll.setBackgroundResource(R.color.white);
        }

        v.temInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TemInfoActivity.class);
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                context.startActivity(intent);
            }
        });

        v.suppliesApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BusinessLeaderByMyOrderSuppliesApplyActivity.class);
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                intent.putExtra("type", orderList.get(position).getType());
                context.startActivity(intent);
            }
        });

        v.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BusinessLeaderByMyOrderDetailedInfoActivity.class);
                intent.putParcelableArrayListExtra("detailedInfo", orderList.get(position).getDetailedInfos());
                intent.putParcelableArrayListExtra("picUrlList", orderList.get(position).getPicUrls());
                intent.putExtra("recordFileName", orderList.get(position).getRecordFileName());
                context.startActivity(intent);
            }
        });

        v.gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GetGPSActivity.class);
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                context.startActivity(intent);
            }
        });

        v.queryProjectAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QueryProjectAmountActivity.class);
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                context.startActivity(intent);
            }
        });

        v.btn_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag.equals("确认收到")) {
                    sendSure(position);
                }

                if (flag.equals("处理")) {
                    new AlertDialog.Builder(context).setMessage("确定开始处理？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendDone(position);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }

                if (flag.equals("汇报")) {
                    Intent intent = new Intent(context, ReportActivity.class);
                    intent.putExtra("orderId", orderList.get(position).getOrderId());
                    intent.putExtra("type", orderList.get(position).getType());
                    context.startActivity(intent);
                }

                if (flag.equals("巡检")) {
                    Intent intent = new Intent(context, PipeInspectionMapActivity.class);
                    intent.putParcelableArrayListExtra("inspectionList", orderList.get(position).getInspectionInfos());
                    intent.putExtra("orderId", orderList.get(position).getOrderId());
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    private void sendSure(final int position) {
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "doreceive")
                .add("userno", userNo)
                .add("fileno", orderList.get(position).getOrderId())
                .build();
        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if (response.equals("ok")) {
                    orderList.get(position).setFlag("处理");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(context, response, Toast.LENGTH_SHORT);
                        }
                    });
                }
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

    private void sendDone(final int position) {
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "doinstallreceive")
                .add("userno", userNo)
                .add("fileno", orderList.get(position).getOrderId())
                .build();
        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if (response.equals("ok")) {
                    if (orderList.get(position).getType().equals("供水管网巡查")) {
                        orderList.get(position).setFlag("巡检");
                    } else {
                        orderList.get(position).setFlag("汇报");
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(context, response, Toast.LENGTH_SHORT);
                        }
                    });
                }
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
        private TextView orderId;
        private ImageView urgent;
        private TextView type;
        private TextView state;
        private TextView receivedTime;
        private TextView deadline;
        private TextView temInfo;
        private TextView flag;
        private Button suppliesApply;
        private Button info;
        private Button gps;
        private Button queryProjectAmount;
        private Button safetyFail;
        private RelativeLayout head_rl;
        private LinearLayout bg_ll;
        private RelativeLayout btn_rl;
        private TextView timeLeft;
    }
}
