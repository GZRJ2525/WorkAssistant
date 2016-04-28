package com.gzrijing.workassistant.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.SafetyInspectFailReport;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import java.util.ArrayList;

public class SafetyInspectFailReportAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private ArrayList<SafetyInspectFailReport> list;
    private Handler handler = new Handler();
    private ProgressDialog pDialog;

    public SafetyInspectFailReportAdapter(Context context, ArrayList<SafetyInspectFailReport> list) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.list = list;
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
                    R.layout.listview_item_safety_inspect_fail_report, parent, false);
            v.content = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_fail_report_content_tv);
            v.time = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_fail_report_time_tv);
            v.worker = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_fail_report_worker_tv);
            v.remark = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_fail_report_remark_tv);
            v.submit = (Button) convertView.findViewById(R.id.listview_item_safety_inspect_fail_report_submit_btn);
            v.feedback = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_fail_report_remark_et);//yycq 添加反馈
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.content.setText(list.get(position).getContent());
        v.time.setText(list.get(position).getTime());
        v.worker.setText(list.get(position).getWorker());
        v.remark.setText(list.get(position).getRemark());
        v.feedback.setText(list.get(position).getFeedback());
        // 反馈信息textview添加点击事件
        v.feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText feedback = new EditText(SafetyInspectFailReportAdapter.this.context);
                feedback.setTextColor(SafetyInspectFailReportAdapter.this.context.getResources().getColor(R.color.black));
                new AlertDialog.Builder(SafetyInspectFailReportAdapter.this.context)
                        .setMessage("请输入反馈信息")
                        .setView(feedback)
                        .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list.get(position).setFeedback(feedback.getText().toString().trim());// yycq 点击确定赋值给SafetyInspectFailReport的feedback属性
                        notifyDataSetChanged();
                    }
                }).show();

            }
        });
        v.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report(SafetyInspectFailReportAdapter.this, position);//426yycq
            }
        });

        return convertView;
    }

    private static void report(final SafetyInspectFailReportAdapter safetyInspectFailReportAdapter, final int position) {
        safetyInspectFailReportAdapter.pDialog = new ProgressDialog(safetyInspectFailReportAdapter.context);
        safetyInspectFailReportAdapter.pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        safetyInspectFailReportAdapter.pDialog.setMessage("正在反馈...");
        safetyInspectFailReportAdapter.pDialog.show();
        Log.e("feedback", safetyInspectFailReportAdapter.list.get(position).getFeedback());
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "handlegetinsys")
                .add("RecordId", safetyInspectFailReportAdapter.list.get(position).getRecordId())
                .add("OKInf", "1")
                .add("FlagUserInf", safetyInspectFailReportAdapter.list.get(position).getFeedback())
                .build();

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                safetyInspectFailReportAdapter.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        safetyInspectFailReportAdapter.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.equals("Ok")) {
                                    ToastUtil.showToast(safetyInspectFailReportAdapter.context, "反馈成功", Toast.LENGTH_SHORT);
                                    safetyInspectFailReportAdapter.list.remove(position);
                                    safetyInspectFailReportAdapter.notifyDataSetChanged();
                                } else {
                                    ToastUtil.showToast(safetyInspectFailReportAdapter.context, response, Toast.LENGTH_SHORT);
                                }
                                safetyInspectFailReportAdapter.pDialog.dismiss();
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                safetyInspectFailReportAdapter.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(safetyInspectFailReportAdapter.context, "与服务器断开连接", Toast.LENGTH_SHORT);
                        safetyInspectFailReportAdapter.pDialog.dismiss();
                    }
                });
            }
        });
    }

    class ViewHolder {
        private TextView content;
        private TextView time;
        private TextView worker;
        private TextView remark;
        private Button submit;
        private TextView feedback;//yycq 反馈
    }
}
