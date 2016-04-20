package com.gzrijing.workassistant.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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
import com.gzrijing.workassistant.db.BusinessData;
import com.gzrijing.workassistant.entity.BusinessByLeader;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.DeleteFolderUtil;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ImageUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.gzrijing.workassistant.util.VoiceUtil;
import com.gzrijing.workassistant.view.BusinessHaveSendActivity;
import com.gzrijing.workassistant.view.BusinessLeaderByMyOrderActivity;
import com.gzrijing.workassistant.view.MachineApplyActivity;
import com.gzrijing.workassistant.view.ProgressActivity;
import com.gzrijing.workassistant.view.DistributeActivity;
import com.gzrijing.workassistant.view.ReportInfoCompleteActivity;
import com.gzrijing.workassistant.view.SafetyInspectFailReportActivity;
import com.gzrijing.workassistant.view.SuppliesVerifyActivity;
import com.gzrijing.workassistant.view.TemInfoActivity;
import com.gzrijing.workassistant.view.DetailedInfoActivity;
import com.gzrijing.workassistant.widget.SlideView;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.litepal.crud.DataSupport;

import java.util.List;

public class BusinessLeaderAdapter extends BaseAdapter implements SlideView.OnSlideListener {
    private Context context;
    private LayoutInflater listContainer;
    private List<BusinessByLeader> orderList;
    private String userNo;
    private Handler handler = new Handler();
    private SlideView mLastSlideViewWithStatusOn;

    public BusinessLeaderAdapter(Context context, List<BusinessByLeader> orderList, String userNo) {
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
    public View getView(final int position, final View convertView, ViewGroup parent) {
        ViewHolder v = null;
        SlideView slideView = (SlideView) convertView;
        if (slideView == null) {
            View itemView = listContainer.inflate(
                    R.layout.listview_item_business_leader, parent, false);

            slideView = new SlideView(context);
            slideView.setContentView(itemView);
            v = new ViewHolder(slideView);
            slideView.setOnSlideListener(this);
            slideView.setTag(v);
        } else {
            v = (ViewHolder) slideView.getTag();
        }

        BusinessByLeader item = orderList.get(position);
        item.setSlideView(slideView);
        item.getSlideView().shrink();

        v.orderId.setText(orderList.get(position).getOrderId());
        v.type.setText(orderList.get(position).getType());
        v.state.setText(orderList.get(position).getState());
        v.receivedTime.setText(orderList.get(position).getReceivedTime());
        v.deadline.setText(orderList.get(position).getDeadline());
        v.temInfo.setText("有" + orderList.get(position).getTemInfoNum() + "条临时信息");

        if (orderList.get(position).isUrgent()) {
            v.urgent.setVisibility(View.VISIBLE);
        } else {
            v.urgent.setVisibility(View.GONE);
        }

        v.completeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReportInfoCompleteActivity.class);
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                context.startActivity(intent);
            }
        });

        v.suppliesQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SuppliesVerifyActivity.class);
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                context.startActivity(intent);
            }
        });

        v.machineApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MachineApplyActivity.class);
                intent.putExtra("id", orderList.get(position).getId());
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                context.startActivity(intent);
            }
        });

        v.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailedInfoActivity.class);
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                intent.putExtra("id", orderList.get(position).getId());
                context.startActivity(intent);
            }
        });

        v.progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProgressActivity.class);
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                context.startActivity(intent);
            }
        });

        v.haveSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BusinessHaveSendActivity.class);
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                context.startActivity(intent);
            }
        });

        v.myOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BusinessLeaderByMyOrderActivity.class);
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                context.startActivity(intent);
            }
        });

        v.safetyFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SafetyInspectFailReportActivity.class);
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                context.startActivity(intent);
            }
        });

        v.temInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TemInfoActivity.class);
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                context.startActivity(intent);
            }
        });

        final String flag = orderList.get(position).getFlag();
        v.flag.setText(flag);
        if (flag.equals("确认收到")) {
            v.bg_ll.setBackgroundResource(R.color.pink_bg);
        } else {
            v.bg_ll.setBackgroundResource(R.color.white);
        }

        v.btn_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag.equals("确认收到")) {
                    sendSure(position);
                }
                if (flag.equals("派工")) {
                    Intent intent = new Intent(context, DistributeActivity.class);
                    intent.putExtra("orderId", orderList.get(position).getOrderId());
                    context.startActivity(intent);
                }
            }
        });

        v.deleteHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataSupport.delete(BusinessData.class, orderList.get(position).getId());
                String picPath = ImageUtils.getImagePath(context, userNo, orderList.get(position).getOrderId()).getPath();
                DeleteFolderUtil.deleteFolder(picPath);
                String voicePath = VoiceUtil.getVoicePath(context, userNo, orderList.get(position).getOrderId()).getPath();
                DeleteFolderUtil.deleteFolder(voicePath);
                orderList.remove(position);
                notifyDataSetChanged();
            }
        });

        return slideView;
    }

    private void sendSure(final int position) {
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "doreceive")
                .add("userno", userNo)
                .add("fileno", orderList.get(position).getOrderId())
                .build();
        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                orderList.get(position).setFlag("派工");
                ContentValues values = new ContentValues();
                values.put("flag", "派工");
                DataSupport.updateAll(BusinessData.class, values,
                        "user = ? and orderId = ?", userNo, orderList.get(position).getOrderId());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
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

    @Override
    public void onSlide(View view, int status) {
        if (mLastSlideViewWithStatusOn != null
                && mLastSlideViewWithStatusOn != view) {
            mLastSlideViewWithStatusOn.shrink();
        }

        if (status == SLIDE_STATUS_ON) {
            mLastSlideViewWithStatusOn = (SlideView) view;
        }
    }

    class ViewHolder {
        private TextView orderId;
        private ImageView urgent;
        private TextView type;
        private TextView state;
        private TextView receivedTime;
        private TextView deadline;
        private TextView temInfo;
        private Button completeInfo;
        private Button suppliesQuery;
        private Button machineApply;
        private Button progress;
        private Button info;
        private Button haveSend;
        private Button myOrder;
        private Button safetyFail;
        private TextView flag;
        private LinearLayout bg_ll;
        private RelativeLayout btn_rl;
        public ViewGroup deleteHolder;

        ViewHolder(View view) {
            orderId = (TextView) view.findViewById(
                    R.id.listview_item_business_leader_order_id_tv);
            completeInfo = (Button) view.findViewById(
                    R.id.listview_item_business_leader_complete_info_btn);
            suppliesQuery = (Button) view.findViewById(
                    R.id.listview_item_business_leader_query_supplies_btn);
            machineApply = (Button) view.findViewById(
                    R.id.listview_item_business_leader_machine_apply_btn);
            progress = (Button) view.findViewById(
                    R.id.listview_item_business_leader_progress_btn);
            info = (Button) view.findViewById(
                    R.id.listview_item_business_leader_info_btn);
            haveSend = (Button) view.findViewById(
                    R.id.listview_item_business_leader_have_send_btn);
            myOrder = (Button) view.findViewById(
                    R.id.listview_item_business_leader_my_order_btn);
            safetyFail = (Button) view.findViewById(
                    R.id.listview_item_business_leader_safety_inspect_fail_btn);
            urgent = (ImageView) view.findViewById(
                    R.id.listview_item_business_leader_urgent_iv);
            type = (TextView) view.findViewById(
                    R.id.listview_item_business_leader_type_tv);
            state = (TextView) view.findViewById(
                    R.id.listview_item_business_leader_state_tv);
            receivedTime = (TextView) view.findViewById(
                    R.id.listview_item_business_leader_received_time_tv);
            deadline = (TextView) view.findViewById(
                    R.id.listview_item_business_leader_deadline_tv);
            temInfo = (TextView) view.findViewById(
                    R.id.listview_item_business_leader_tem_info_tv);
            flag = (TextView) view.findViewById(
                    R.id.listview_item_business_leader_flag_tv);
            bg_ll = (LinearLayout) view.findViewById(
                    R.id.listview_item_business_leader_bg_ll);
            btn_rl = (RelativeLayout) view.findViewById(
                    R.id.listview_item_business_leader_rl);
            deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
        }
    }
}
