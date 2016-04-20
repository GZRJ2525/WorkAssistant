package com.gzrijing.workassistant.view;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.SuppliesApplyApplyingAdapter;
import com.gzrijing.workassistant.adapter.SuppliesApplyApprovalAdapter;
import com.gzrijing.workassistant.adapter.SuppliesApplyCreatedAdapter;
import com.gzrijing.workassistant.adapter.SuppliesApplyReceivedAdapter;
import com.gzrijing.workassistant.adapter.SuppliesApplyReturnAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.Supplies;
import com.gzrijing.workassistant.entity.SuppliesNo;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.JudgeDate;
import com.gzrijing.workassistant.util.ToastUtil;
import com.gzrijing.workassistant.widget.MyListView;
import com.gzrijing.workassistant.widget.selectdate.ScreenInfo;
import com.gzrijing.workassistant.widget.selectdate.WheelMain;
import com.gzrijing.workassistant.zxing.view.MipcaActivityCapture;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BusinessLeaderByMyOrderSuppliesApplyActivity extends BaseActivity implements View.OnClickListener {

    private final static int SUPPLIES_RECEIVED = 3001;
    private final static int SUPPLIES_RETURN = 3002;
    private String userNo;
    private String orderId;
    private String type;
    private Button btn_edit;
    private Button btn_apply;
    private TextView tv_useTime;
    private EditText et_remarks;
    private Button btn_received;
    private Button btn_returnEdit;
    private Button btn_return;
    private MyListView lv_created;
    private MyListView lv_applying;
    private MyListView lv_approval;
    private MyListView lv_received;
    private MyListView lv_return;
    private ArrayList<Supplies> createdList = new ArrayList<Supplies>();
    private List<SuppliesNo> applyingList = new ArrayList<SuppliesNo>();
    private List<SuppliesNo> approvalList = new ArrayList<SuppliesNo>();
    private List<SuppliesNo> receivedList = new ArrayList<SuppliesNo>();
    private List<SuppliesNo> returnList = new ArrayList<SuppliesNo>();
    private SuppliesApplyCreatedAdapter createdAdapter;
    private SuppliesApplyApplyingAdapter applyingAdapter;
    private SuppliesApplyApprovalAdapter approvalAdapter;
    private SuppliesApplyReceivedAdapter receivedAdapter;
    private SuppliesApplyReturnAdapter returnAdapter;
    private WheelMain wheelMain;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_leader_by_my_order_supplies_apply);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        type = intent.getStringExtra("type");

        getSuppliesApply();
        getSuppliesReceived();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.com.gzrijing.workassistant.SuppliesApply.refresh");
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void getSuppliesApply() {
        String url = null;
        try {
            url = "?cmd=getmymaterialneedmain&userno=" + URLEncoder.encode(userNo, "UTF-8") +
                    "&checkdate=&fileno=" + URLEncoder.encode(orderId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<SuppliesNo> list = JsonParseUtils.getMyOrderSuppliesNoByApply(response);
                        Log.e("size", list.size() + "");
                        applyingList.clear();
                        approvalList.clear();
                        returnList.clear();
                        for (SuppliesNo suppliesNo : list) {
                            if (!suppliesNo.getApplyId().equals("")) {
                                if (suppliesNo.getApplyState().equals("申请中") || suppliesNo.getApplyState().equals("不批准")) {
                                    applyingList.add(suppliesNo);
                                    applyingAdapter.notifyDataSetChanged();
                                }
                                if (suppliesNo.getApplyState().equals("已审批")) {
                                    approvalList.add(suppliesNo);
                                    approvalAdapter.notifyDataSetChanged();
                                }
                            } else {
                                if (!suppliesNo.getReturnId().equals("")) {
                                    returnList.add(suppliesNo);
                                    returnAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(BusinessLeaderByMyOrderSuppliesApplyActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private void getSuppliesReceived() {
        String url = null;
        try {
            url = "?cmd=getmaterialsend&userno=" + URLEncoder.encode(userNo, "UTF-8") +
                    "&checkdate=&fileno=" + URLEncoder.encode(orderId, "UTF-8") +
                    "&billno=";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<SuppliesNo> list = JsonParseUtils.getMyOrderSuppliesNoBySend(response);
                        receivedList.clear();
                        receivedList.addAll(list);
                        receivedAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(BusinessLeaderByMyOrderSuppliesApplyActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }


    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_edit = (Button) findViewById(R.id.supplies_apply_edit_btn);
        btn_apply = (Button) findViewById(R.id.supplies_apply_apply_btn);
        tv_useTime = (TextView) findViewById(R.id.supplies_apply_use_time_tv);
        et_remarks = (EditText) findViewById(R.id.supplies_apply_remarks_et);

        btn_received = (Button) findViewById(R.id.supplies_apply_received_btn);

        btn_returnEdit = (Button) findViewById(R.id.supplies_apply_return_edit_btn);
        btn_return = (Button) findViewById(R.id.supplies_apply_return_btn);

        lv_created = (MyListView) findViewById(R.id.supplies_apply_created_lv);
        createdAdapter = new SuppliesApplyCreatedAdapter(this, createdList);
        lv_created.setAdapter(createdAdapter);

        lv_applying = (MyListView) findViewById(R.id.supplies_apply_applying_lv);
        applyingAdapter = new SuppliesApplyApplyingAdapter(this, applyingList);
        lv_applying.setAdapter(applyingAdapter);

        lv_approval = (MyListView) findViewById(R.id.supplies_apply_approval_lv);
        approvalAdapter = new SuppliesApplyApprovalAdapter(this, approvalList);
        lv_approval.setAdapter(approvalAdapter);

        lv_received = (MyListView) findViewById(R.id.supplies_apply_received_lv);
        receivedAdapter = new SuppliesApplyReceivedAdapter(this, receivedList);
        lv_received.setAdapter(receivedAdapter);

        lv_return = (MyListView) findViewById(R.id.supplies_apply_return_lv);
        returnAdapter = new SuppliesApplyReturnAdapter(this, returnList);
        lv_return.setAdapter(returnAdapter);
    }

    private void setListeners() {
        btn_edit.setOnClickListener(this);
        btn_apply.setOnClickListener(this);
        tv_useTime.setOnClickListener(this);
        btn_received.setOnClickListener(this);
        btn_returnEdit.setOnClickListener(this);
        btn_return.setOnClickListener(this);

        lv_applying.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String state = applyingList.get(position).getApplyState();
                if (state.equals("不批准")) {
                    Intent intent = new Intent(BusinessLeaderByMyOrderSuppliesApplyActivity.this, BusinessLeaderByMyOrderSuppliesApplyingActivity.class);
                    intent.putExtra("suppliesNo", applyingList.get(position));
                    intent.putExtra("position", position);
                    intent.putExtra("userNo", userNo);
                    intent.putExtra("orderId", orderId);
                    startActivityForResult(intent, 20);
                } else {
                    Intent intent = new Intent(BusinessLeaderByMyOrderSuppliesApplyActivity.this, BusinessLeaderByMyOrderSuppliesApplyingScanActivity.class);
                    intent.putExtra("suppliesNo", applyingList.get(position));
                    startActivity(intent);
                }
            }
        });

        lv_approval.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BusinessLeaderByMyOrderSuppliesApplyActivity.this, BusinessLeaderByMyOrderSuppliesApprovalActivity.class);
                intent.putExtra("suppliesNo", approvalList.get(position));
                startActivity(intent);
            }
        });

        lv_received.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BusinessLeaderByMyOrderSuppliesApplyActivity.this, BusinessLeaderByMyOrderSuppliesReceivedActivity.class);
                intent.putExtra("suppliesNo", receivedList.get(position));
                startActivity(intent);
            }
        });

        lv_return.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BusinessLeaderByMyOrderSuppliesApplyActivity.this, BusinessLeaderByMyOrderSuppliesReturnActivity.class);
                intent.putExtra("suppliesNo", returnList.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.supplies_apply_edit_btn:
                Intent intent1 = new Intent(this, SuppliesApplyEditActivity.class);
                intent1.putParcelableArrayListExtra("suppliesList", createdList);
                intent1.putExtra("type", type);
                startActivityForResult(intent1, 10);
                break;

            case R.id.supplies_apply_apply_btn:
                apply();
                break;

            case R.id.supplies_apply_use_time_tv:
                getUseTime();
                break;

            case R.id.supplies_apply_received_btn:
                Intent intent2 = new Intent();
                intent2.setClass(this, MipcaActivityCapture.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent2, SUPPLIES_RECEIVED);
                break;

            case R.id.supplies_apply_return_edit_btn:
                ArrayList<SuppliesNo> suppliesNoList = new ArrayList<SuppliesNo>();
                for (SuppliesNo suppliesNo : receivedList) {
                    if (suppliesNo.getReceivedState().equals("已领出")) {
                        suppliesNoList.add(suppliesNo);
                    }
                }
                Intent intent3 = new Intent(this, BusinessLeaderByMyOrderSuppliesReturnEditActivity.class);
                intent3.putExtra("userNo", userNo);
                intent3.putExtra("orderId", orderId);
                intent3.putParcelableArrayListExtra("suppliesNoList", suppliesNoList);
                startActivityForResult(intent3, 30);
                break;

            case R.id.supplies_apply_return_btn:
                Intent intent4 = new Intent();
                intent4.setClass(this, MipcaActivityCapture.class);
                intent4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent4, SUPPLIES_RETURN);
                break;
        }
    }

    private void apply() {
        String useTime = tv_useTime.getText().toString();
        if (useTime.equals("")) {
            ToastUtil.showToast(this, "请选择领用时间", Toast.LENGTH_SHORT);
            return;
        }
        String remarks = et_remarks.getText().toString().trim();

        JSONArray jsonArray = new JSONArray();
        for (Supplies supplies : createdList) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("MakingNo", supplies.getId());
                jsonObject.put("MakingName", supplies.getName());
                jsonObject.put("MakingSpace", supplies.getSpec());
                jsonObject.put("MakingUnit", supplies.getUnit());
                jsonObject.put("Qty", supplies.getApplyNum());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.e("json", jsonArray.toString());
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "dosavematerialneedmain")
                .add("mainid", "")
                .add("fileno", orderId)
                .add("billtype", "工程材料")
                .add("usedatetime", useTime)
                .add("remark", remarks)
                .add("userno", userNo)
                .add("makingjson", jsonArray.toString())
                .build();

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                if (response.substring(0, 1).equals("E")) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(BusinessLeaderByMyOrderSuppliesApplyActivity.this, "申请失败", Toast.LENGTH_SHORT);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            savaSuppliesNo(response);
                        }
                    });

                }
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(BusinessLeaderByMyOrderSuppliesApplyActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });

    }

    private void savaSuppliesNo(String applyId) {
        Calendar rightNow = Calendar.getInstance();
        String applyTime = dateFormat.format(rightNow.getTime());

        ArrayList<Supplies> suppliesList = new ArrayList<Supplies>();
        for (int i = 0; i < createdList.size(); i++) {
            Supplies supplies = new Supplies();
            supplies.setId(createdList.get(i).getId());
            supplies.setName(createdList.get(i).getName());
            supplies.setSpec(createdList.get(i).getSpec());
            supplies.setUnit(createdList.get(i).getUnit());
            supplies.setApplyNum(createdList.get(i).getApplyNum());
            supplies.setSendNum("0");
            suppliesList.add(supplies);
        }

        SuppliesNo suppliesNo = new SuppliesNo();
        suppliesNo.setApplyId(applyId);
        suppliesNo.setApplyTime(applyTime);
        suppliesNo.setUseTime(tv_useTime.getText().toString());
        suppliesNo.setRemarks(et_remarks.getText().toString().trim());
        suppliesNo.setApplyState("申请中");
        suppliesNo.setSuppliesList(suppliesList);
        applyingList.add(suppliesNo);

        btn_apply.setVisibility(View.GONE);
        tv_useTime.setText("");
        tv_useTime.setHint("未选择");
        et_remarks.setText("");
        createdList.clear();
        createdAdapter.notifyDataSetChanged();
        applyingAdapter.notifyDataSetChanged();

    }

    private void getUseTime() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View timepickerview = inflater.inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(this);
        wheelMain = new WheelMain(timepickerview, true);
        wheelMain.screenheight = screenInfo.getHeight();
        String time = tv_useTime.getText().toString();
        Calendar calendar = Calendar.getInstance();
        if (JudgeDate.isDate(time, "yyyy-MM-dd HH:mm")) {
            try {
                calendar.setTime(dateFormat.parse(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        int h = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        wheelMain.initDateTimePicker(y, m - 1, d, h, min);
        new AlertDialog.Builder(this)
                .setTitle("选择时间")
                .setView(timepickerview)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_useTime.setText(wheelMain.getTime());
                        tv_useTime.setTextColor(getResources().getColor(
                                R.color.black));
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == 10) {
                List<Supplies> supplies = data.getParcelableArrayListExtra("suppliesList");
                if (supplies.size() > 0) {
                    btn_apply.setVisibility(View.VISIBLE);
                } else {
                    btn_apply.setVisibility(View.GONE);
                }
                createdList.clear();
                createdList.addAll(supplies);
                createdAdapter.notifyDataSetChanged();
            }
        }

        if (requestCode == 20) {
            if (resultCode == 20) {
                SuppliesNo suppliesNo = data.getParcelableExtra("suppliesNo");
                int position = data.getIntExtra("position", -1);
                applyingList.remove(position);
                applyingList.add(suppliesNo);
                applyingAdapter.notifyDataSetChanged();
            }
        }

        if (requestCode == 30) {
            if (resultCode == 30) {
                SuppliesNo suppliesNo = data.getParcelableExtra("suppliesNo");
                returnList.add(suppliesNo);
                returnAdapter.notifyDataSetChanged();
            }
        }

        if (requestCode == SUPPLIES_RECEIVED) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                // 显示扫描到的内容
                String id = bundle.getString("result");
                receivedConfirm(id);
            }
        }

        if (requestCode == SUPPLIES_RETURN) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                // 显示扫描到的内容
                String id = bundle.getString("result");
                returnConfirm(id);
            }
        }
    }

    private void receivedConfirm(final String id) {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PicType", "WnW_MaterialSendMain");
            jsonObject.put("FileNo", orderId);
            jsonObject.put("BillNo", id);
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "doreceivematerialandmachine")
                .add("userno", userNo)
                .add("detail", jsonArray.toString())
                .build();

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.equals("ok")) {
                            Calendar rightNow = Calendar.getInstance();
                            String receivedTime = dateFormat.format(rightNow.getTime());
                            for (SuppliesNo suppliesNo : receivedList) {
                                if (suppliesNo.getReceivedId().equals(id)) {
                                    suppliesNo.setReceivedState("已领出");
                                    suppliesNo.setReceivedTime(receivedTime);

                                    for (SuppliesNo approval : approvalList) {
                                        if (suppliesNo.getApplyId().equals(approval.getApplyId())) {
                                            for (Supplies receivedSupplies : suppliesNo.getSuppliesList()) {
                                                for (Supplies approvalSupplies : approval.getSuppliesList()) {
                                                    if (receivedSupplies.getName().equals(approvalSupplies.getName())
                                                            && receivedSupplies.getSpec().equals(approvalSupplies.getSpec())) {
                                                        approvalSupplies.setSendNum(String.valueOf(
                                                                Float.valueOf(approvalSupplies.getSendNum()) + Float.valueOf(receivedSupplies.getSendNum())));
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    receivedAdapter.notifyDataSetChanged();
                                    approvalAdapter.notifyDataSetChanged();
                                    ToastUtil.showToast(BusinessLeaderByMyOrderSuppliesApplyActivity.this, "发放单ID:" + id + ",领出成功", Toast.LENGTH_SHORT);
                                }
                            }
                        } else {
                            ToastUtil.showToast(BusinessLeaderByMyOrderSuppliesApplyActivity.this, "领出失败", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(BusinessLeaderByMyOrderSuppliesApplyActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }


    private void returnConfirm(final String id) {
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "domaterialback")
                .add("userno", userNo)
                .add("billno", id)
                .build();
        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.equals("ok")) {
                            for (SuppliesNo suppliesNo : returnList) {
                                if (suppliesNo.getReturnId().equals(id)) {
                                    suppliesNo.setReturnState("已退回");
                                    returnAdapter.notifyDataSetChanged();
                                    ToastUtil.showToast(BusinessLeaderByMyOrderSuppliesApplyActivity.this, "退回单ID:" + id + ",退回成功", Toast.LENGTH_SHORT);
                                }
                            }
                        } else {
                            ToastUtil.showToast(BusinessLeaderByMyOrderSuppliesApplyActivity.this, "退回失败", Toast.LENGTH_SHORT);
                        }
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(BusinessLeaderByMyOrderSuppliesApplyActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.com.gzrijing.workassistant.SuppliesApply.refresh")) {
                getSuppliesApply();
                getSuppliesReceived();
            }
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
