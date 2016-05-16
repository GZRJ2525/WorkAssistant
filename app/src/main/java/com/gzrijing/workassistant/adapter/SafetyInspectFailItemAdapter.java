package com.gzrijing.workassistant.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.SafetyInspectFailItem;
import com.gzrijing.workassistant.entity.SubordinateLocation;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;

public class SafetyInspectFailItemAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private ArrayList<SafetyInspectFailItem> list;
    private String[] workerItem;
    private String[] workerNoItem;
    private HashMap<Integer, boolean[]> mapList = new HashMap<Integer, boolean[]>();
    private Handler handler = new Handler();
    private HashMap<Integer, String> etMapList = new HashMap<Integer, String>();
    private int index = -1;
    private ProgressDialog pDialog;

    public SafetyInspectFailItemAdapter(Context context, ArrayList<SafetyInspectFailItem> list,
                                        ArrayList<SubordinateLocation> workerList) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.list = list;
        workerItem = new String[workerList.size()];
        workerNoItem = new String[workerList.size()];
        for (int i = 0; i < workerList.size(); i++) {
            workerItem[i] = workerList.get(i).getName();
            workerNoItem[i] = workerList.get(i).getUserNo();
        }

        for (int i = 0; i < list.size(); i++) {
            boolean[] checkedItems = new boolean[workerItem.length];
            mapList.put(i, checkedItems);
        }
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
                    R.layout.listview_item_safety_inspect_fail_item, parent, false);
            v.content = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_fail_item_content_tv);
            v.isHandle = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_fail_item_is_handle_tv);
            v.isDistributed = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_fail_item_is_distributed_tv);
            v.worker = (TextView) convertView.findViewById(R.id.listview_item_safety_inspect_fail_item_worker_tv);
            v.remark = (EditText) convertView.findViewById(R.id.listview_item_safety_inspect_fail_item_remark_et);
            v.submit = (Button) convertView.findViewById(R.id.listview_item_safety_inspect_fail_item_submit_btn);

            v.remark.setTag(position);
            v.remark.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        index = (Integer) v.getTag();
                    }

                    return false;
                }
            });
            class MyTextWatcher implements TextWatcher {
                public MyTextWatcher(ViewHolder holder) {
                    mHolder = holder;
                }

                private ViewHolder mHolder;

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s != null && !"".equals(s.toString())) {
                        int position = (Integer) mHolder.remark.getTag();
                        list.get(position).setRemark(s.toString());
                    }
                }
            }
            v.remark.addTextChangedListener(new MyTextWatcher(v));

            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
            v.remark.setTag(position);
        }

        v.content.setText(list.get(position).getContent());
        v.worker.setText(list.get(position).getWorker());//【
        v.remark.setText(list.get(position).getRemark());
        v.remark.clearFocus();
        if (index != -1 && index == position) {
            v.remark.requestFocus();
        }


        String isDistributed = list.get(position).getIsDistributed();
        if (isDistributed.equals("1")) {
            v.isDistributed.setText("已派发");
            v.submit.setVisibility(View.GONE);
        } else {
            v.isDistributed.setText("未派发");
            v.submit.setVisibility(View.VISIBLE);
            v.worker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final boolean[] checkedItems = mapList.get(position);
                    new AlertDialog.Builder(context).setTitle("选择整改人：").setMultiChoiceItems(
                            workerItem, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    checkedItems[which] = isChecked;
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    StringBuilder sb = new StringBuilder();
                                    StringBuilder sb1 = new StringBuilder();
                                    for (int i = 0; i < checkedItems.length; i++) {
                                        if (checkedItems[i]) {
                                            sb.append(workerItem[i] + ",");
                                            sb1.append(workerNoItem[i] + ",");
                                        }
                                    }
                                    if (sb.length() > 0) {
                                        String str = sb.toString().substring(0, sb.length() - 1);
                                        String str1 = sb1.toString().substring(0, sb1.length() - 1);
                                        list.get(position).setWorker(str);
                                        list.get(position).setWorkerNo(str1);
                                        notifyDataSetChanged();
                                    }
                                }
                            }).show();
                }
            });
        }

        String flag = list.get(position).getIsHandle();
        if (flag.equals("1")) {
            v.isHandle.setText("已处理");
        } else {
            v.isHandle.setText("未处理");
        }

        v.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getWorkerNo() == null || list.get(position).getWorkerNo().equals("")) {
                    ToastUtil.showToast(context, "请选择整改人后再提交", Toast.LENGTH_SHORT);
                } else {
                    if (list.get(position).getRemark() == null) {
                        list.get(position).setRemark("");
                    }
                    Log.e("RecordId", list.get(position).getRecordId());
                    Log.e("UserNo", list.get(position).getWorkerNo());
                    Log.e("UserInf", list.get(position).getRemark());
                    distributed(position);
                }

            }
        });

        return convertView;
    }

    private void distributed(final int position) {
        pDialog = new ProgressDialog(context);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在派发...");
        Log.e("userno",list.get(position).getWorkerNo());
        Log.e("UserName",list.get(position).getWorker());
        pDialog.show();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "safeinf_handle")
                .add("RecordId", list.get(position).getRecordId())
                .add("UserNo", list.get(position).getWorkerNo())
                .add("UserInf", list.get(position).getRemark())
                .add("UserName", list.get(position).getWorker())
                .build();

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.equals("Ok")) {
                            ToastUtil.showToast(context, "提交成功", Toast.LENGTH_SHORT);
                            list.get(position).setIsDistributed("1");
                            notifyDataSetChanged();
                        } else {
                            ToastUtil.showToast(context, response, Toast.LENGTH_SHORT);
                        }
                        pDialog.dismiss();
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
        private TextView isDistributed;
        private TextView isHandle;
        private TextView worker;
        private EditText remark;
        private Button submit;
    }
}
