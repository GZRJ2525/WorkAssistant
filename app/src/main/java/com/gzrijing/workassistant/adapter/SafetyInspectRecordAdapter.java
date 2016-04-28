package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.SafetyInspectSecondItem;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import java.util.ArrayList;

public class SafetyInspectRecordAdapter extends BaseAdapter {

    private Context context;
    private String orderId;
    private LayoutInflater listContainer;
    private ArrayList<SafetyInspectSecondItem> list;
    private Handler handler = new Handler();

    public SafetyInspectRecordAdapter(Context context, ArrayList<SafetyInspectSecondItem> list, String orderId) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.list = list;
        this.orderId = orderId;
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
                    R.layout.listview_item_safety_inspect_record, parent, false);
            v.image = (ImageView) convertView.findViewById(R.id.listview_item_safety_inspect_record_image_iv);
            v.name = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_record_name_tv);
            v.isHandle = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_record_is_handle_tv);
            v.feedback = (TextView)convertView.findViewById(R.id.listview_item_safety_inspect_record_feedback_tv);//yycq
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.name.setText(list.get(position).getName());
        String feedback = list.get(position).getFeedback();
        Log.e("feedback",feedback);
//        feedback = feedback.replaceAll("\n","\\n");// 0427 yycq 恢复换行
        v.feedback.setText(feedback);// 0426 yycq
        Log.e("feedbackget",v.feedback.getText().toString());
        if (list.get(position).getIsHandle().equals("1")) {
            v.isHandle.setText("处理完");
        } else {
            v.isHandle.setText("未处理");
        }
        v.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("cmd", "DelSafeItem")
                        .add("TaskId", orderId)
                        .add("FileId", list.get(position).getId())
                        .build();
                Log.e("orderID", orderId);
                Log.e("fileId", list.get(position).getId());
                HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
                    @Override
                    public void onFinish(final String response) {
                        Log.e("response", response);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.equals("ok")) {
                                    list.remove(position);
                                    notifyDataSetChanged();
                                    ToastUtil.showToast(context, "删除成功", Toast.LENGTH_SHORT);
                                } else {
                                    ToastUtil.showToast(context, "删除失败", Toast.LENGTH_SHORT);
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
        });
        return convertView;
    }

    class ViewHolder {
        private ImageView image;
        private TextView name;
        private TextView isHandle;
        private TextView feedback;//yycq
    }
}
