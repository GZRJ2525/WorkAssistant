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
import com.gzrijing.workassistant.view.CancelBusinessActivity;
import com.gzrijing.workassistant.view.DetailedInfoActivity;
import com.gzrijing.workassistant.view.DistributeByDirectorActivity;
import com.gzrijing.workassistant.view.DistributeByInspectionStationActivity;
import com.gzrijing.workassistant.view.InspectionStationDetailedInfoActivity;
import com.gzrijing.workassistant.view.ProgressActivity;
import com.gzrijing.workassistant.view.ReportInfoCompleteActivity;
import com.gzrijing.workassistant.view.TemInfoActivity;
import com.gzrijing.workassistant.widget.SlideView;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.litepal.crud.DataSupport;

import java.util.List;

public class InspectionStationAdapter extends BaseAdapter implements SlideView.OnSlideListener{
    private Context context;
    private LayoutInflater listContainer;
    private List<BusinessByLeader> orderList;
    private String userNo;
    private Handler handler = new Handler();
    private SlideView mLastSlideViewWithStatusOn;

    public InspectionStationAdapter(Context context, List<BusinessByLeader> orderList, String userNo) {
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
        SlideView slideView = (SlideView) convertView;
        if (slideView == null) {
            View itemView = listContainer.inflate(
                    R.layout.listview_item_inspection_station, parent, false);

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
        v.temInfo.setText(orderList.get(position).getTemInfoNum()+"条新临时信息");

        if (orderList.get(position).isUrgent()) {
            v.urgent.setVisibility(View.VISIBLE);
        } else {
            v.urgent.setVisibility(View.GONE);
        }

        v.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InspectionStationDetailedInfoActivity.class);
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                intent.putParcelableArrayListExtra("detailedInfos", orderList.get(position).getDetailedInfos());
                intent.putParcelableArrayListExtra("picUrls", orderList.get(position).getPicUrls());
                context.startActivity(intent);
            }
        });

        v.callOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CancelBusinessActivity.class);
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
                    Intent intent = new Intent(context, DistributeByInspectionStationActivity.class);
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
        private TextView temInfo;
        private Button info;
        private Button callOff;
        private TextView flag;
        private LinearLayout bg_ll;
        private RelativeLayout btn_rl;
        public ViewGroup deleteHolder;

        ViewHolder(View view) {
            orderId = (TextView) view.findViewById(
                    R.id.listview_item_inspection_station_order_id_tv);
            info = (Button) view.findViewById(
                    R.id.listview_item_inspection_station_info_btn);
            callOff = (Button) view.findViewById(
                    R.id.listview_item_inspection_station_call_off_btn);
            urgent = (ImageView) view.findViewById(
                    R.id.listview_item_inspection_station_urgent_iv);
            type = (TextView) view.findViewById(
                    R.id.listview_item_inspection_station_type_tv);
            state = (TextView) view.findViewById(
                    R.id.listview_item_inspection_station_state_tv);
            receivedTime = (TextView) view.findViewById(
                    R.id.listview_item_inspection_station_received_time_tv);
            temInfo = (TextView) view.findViewById(
                    R.id.listview_item_inspection_station_tem_info_tv);
            flag = (TextView) view.findViewById(
                    R.id.listview_item_inspection_station_flag_tv);
            bg_ll = (LinearLayout) view.findViewById(
                    R.id.listview_item_inspection_station_bg_ll);
            btn_rl = (RelativeLayout) view.findViewById(R.id.listview_item_inspection_station_rl);
            deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
        }
    }
}
