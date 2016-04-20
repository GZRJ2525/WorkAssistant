package com.gzrijing.workassistant.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }

        v.content.setText(list.get(position).getContent());
        v.time.setText(list.get(position).getTime());
        v.worker.setText(list.get(position).getWorker());
        v.remark.setText(list.get(position).getRemark());
        v.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report(position);
            }
        });

        return convertView;
    }

    private void report(final int position) {
        pDialog = new ProgressDialog(context);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在反馈...");
        pDialog.show();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "handlegetinsys")
                .add("RecordId", list.get(position).getRecordId())
                .add("OKInf", "1")
                .build();

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(response.equals("Ok")){
                                    ToastUtil.showToast(context, "反馈成功", Toast.LENGTH_SHORT);
                                    list.remove(position);
                                    notifyDataSetChanged();
                                }else{
                                    ToastUtil.showToast(context, response, Toast.LENGTH_SHORT);
                                }
                                pDialog.dismiss();
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(context, "与服务器断开连接", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
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
    }
}
