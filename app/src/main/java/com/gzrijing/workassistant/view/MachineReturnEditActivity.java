package com.gzrijing.workassistant.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.MachineReturnEditAdapter;
import com.gzrijing.workassistant.adapter.MachineReturnEditReceivedAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.db.BusinessData;
import com.gzrijing.workassistant.db.MachineData;
import com.gzrijing.workassistant.db.MachineNoData;
import com.gzrijing.workassistant.entity.Machine;
import com.gzrijing.workassistant.entity.MachineNo;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JudgeDate;
import com.gzrijing.workassistant.util.ToastUtil;
import com.gzrijing.workassistant.widget.MyListView;
import com.gzrijing.workassistant.widget.selectdate.ScreenInfo;
import com.gzrijing.workassistant.widget.selectdate.WheelMain;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MachineReturnEditActivity extends BaseActivity implements View.OnClickListener {

    private String userNo;
    private String orderId;
    private LinearLayout ll_normal;
    private ImageView iv_normal;
    private LinearLayout ll_damage;
    private ImageView iv_damage;
    private TextView tv_time;
    private EditText et_address;
    private EditText et_remark;
    private ImageView iv_delAll;
    private MyListView lv_return;
    private MyListView lv_received;
    private ArrayList<Machine> returnList = new ArrayList<Machine>();
    private ArrayList<Machine> receivedList = new ArrayList<Machine>();
    private MachineReturnEditAdapter returnAdapter;
    private MachineReturnEditReceivedAdapter receivedAdapter;
    private WheelMain wheelMain;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Handler handler = new Handler();
    private boolean isCheck;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_return_edit);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        Intent intent = getIntent();
        userNo = intent.getStringExtra("userNo");
        orderId = intent.getStringExtra("orderId");
        receivedList = intent.getParcelableArrayListExtra("machineList");
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ll_normal = (LinearLayout) findViewById(R.id.machine_return_edit_normal_ll);
        iv_normal = (ImageView) findViewById(R.id.machine_return_edit_normal_iv);
        ll_damage = (LinearLayout) findViewById(R.id.machine_return_edit_damage_ll);
        iv_damage = (ImageView) findViewById(R.id.machine_return_edit_damage_iv);

        tv_time = (TextView) findViewById(R.id.machine_return_edit_time_tv);
        et_address = (EditText) findViewById(R.id.machine_return_edit_address_et);
        et_remark = (EditText) findViewById(R.id.machine_return_edit_remarks_et);
        iv_delAll = (ImageView) findViewById(R.id.machine_return_edit_del_all_iv);

        lv_return = (MyListView) findViewById(R.id.machine_return_edit_return_lv);
        returnAdapter = new MachineReturnEditAdapter(this, returnList);
        lv_return.setAdapter(returnAdapter);

        lv_received = (MyListView) findViewById(R.id.machine_return_edit_received_lv);
        receivedAdapter = new MachineReturnEditReceivedAdapter(this, receivedList);
        lv_received.setAdapter(receivedAdapter);

    }

    private void setListeners() {
        ll_normal.setOnClickListener(this);
        ll_damage.setOnClickListener(this);
        tv_time.setOnClickListener(this);
        iv_delAll.setOnClickListener(this);

        lv_received.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Machine received = receivedList.get(position);
                for(Machine machine : returnList){
                    if(machine.getId().equals(received.getId())){
                        return;
                    }
                }
                Machine machine = new Machine();
                machine.setId(received.getId());
                machine.setName(received.getName());
                machine.setUnit(received.getUnit());
                machine.setApplyNum(received.getSendNum());
                returnList.add(machine);
                returnAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.machine_return_edit_normal_ll:
                if (isCheck) {
                    iv_normal.setImageResource(R.drawable.spinner_item_check_on);
                    iv_damage.setImageResource(R.drawable.spinner_item_check_off);
                    isCheck = !isCheck;
                }
                break;

            case R.id.machine_return_edit_damage_ll:
                if (!isCheck) {
                    iv_normal.setImageResource(R.drawable.spinner_item_check_off);
                    iv_damage.setImageResource(R.drawable.spinner_item_check_on);
                    isCheck = !isCheck;
                }
                break;

            case R.id.machine_return_edit_time_tv:
                getTime(tv_time);
                break;

            case R.id.machine_return_edit_del_all_iv:
                returnList.clear();
                returnAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void getTime(final TextView tv) {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View timepickerview = inflater.inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(this);
        wheelMain = new WheelMain(timepickerview, true);
        wheelMain.screenheight = screenInfo.getHeight();
        String time = tv.getText().toString();
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
                        tv.setText(wheelMain.getTime());
                        tv.setTextColor(getResources().getColor(
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_machine_return_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_apply) {
            apply();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void apply() {
        String time = tv_time.getText().toString();
        String address = et_address.getText().toString().trim();
        String remark = et_remark.getText().toString().trim();
        String billtype;
        if(isCheck){
            billtype = "损坏机械退还";
        }else{
            billtype = "正常机械退还";
        }
        if (time.equals("")) {
            ToastUtil.showToast(MachineReturnEditActivity.this, "请选择退回时间", Toast.LENGTH_SHORT);
            return;
        }
        if (address.equals("")) {
            ToastUtil.showToast(MachineReturnEditActivity.this, "请填写退回地点", Toast.LENGTH_SHORT);
            return;
        }

        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在申请...");
        pDialog.show();
        JSONArray jsonArray = new JSONArray();
        for (Machine machine : returnList) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("MachineNo", machine.getId());
                jsonObject.put("MachineName", machine.getName());
                jsonObject.put("MachineAddress", "");
                jsonObject.put("EstimateFinishDate", time);
                jsonObject.put("Remark", remark);
                jsonObject.put("Qty", machine.getApplyNum());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.e("json", jsonArray.toString());
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "dosavemachineback")
                .add("billno", "")
                .add("billtype", billtype)
                .add("fileno", orderId)
                .add("proaddress", address)
                .add("userno", userNo)
                .add("machinedetailjson", jsonArray.toString())
                .build();

        Log.e("billtype", billtype);
        Log.e("fileno", orderId);
        Log.e("proaddress", address);
        Log.e("userno", userNo);

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                if (response.substring(0, 1).equals("E")) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(MachineReturnEditActivity.this, "申请失败", Toast.LENGTH_SHORT);
                            pDialog.dismiss();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MachineNo machineNo = savaMachineNo(response);
                            pDialog.dismiss();
                            Intent  intent = getIntent();
                            setResult(30, intent);
                            finish();
                        }
                    });

                }
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(MachineReturnEditActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                });
            }
        });
    }

    private MachineNo savaMachineNo(String returnId) {
        Calendar rightNow = Calendar.getInstance();
        String applyTime = dateFormat.format(rightNow.getTime());
        String type;
        if(isCheck){
            type = "损坏";
        }else{
            type = "正常";
        }

        BusinessData businessData = DataSupport.where("user = ? and orderId = ?", userNo, orderId).find(BusinessData.class, true).get(0);
        for (int i = 0; i < returnList.size(); i++) {
            MachineData data = new MachineData();
            data.setReturnId(returnId);
            data.setNo(returnList.get(i).getId());
            data.setName(returnList.get(i).getName());
            data.setUnit(returnList.get(i).getUnit());
            data.setApplyNum(returnList.get(i).getApplyNum());
            data.save();
            businessData.getMachineDataList().add(data);
            DataSupport.deleteAll(MachineData.class, "receivedState = ? and No = ?", "已送达", returnList.get(i).getId());
        }



        MachineNoData data1 = new MachineNoData();
        data1.setReturnId(returnId);
        data1.setReturnApplyTime(applyTime);
        data1.setReturnTime(tv_time.getText().toString());
        data1.setReturnAddress(et_address.getText().toString().trim());
        data1.setReturnType(type);
        data1.setRemarks(et_remark.getText().toString().trim());
        data1.setReturnState("申请中");
        data1.save();
        businessData.getMachineNoList().add(data1);
        businessData.save();

        MachineNo machineNo = new MachineNo();
        machineNo.setReturnId(returnId);
        machineNo.setReturnApplyTime(applyTime);
        machineNo.setReturnTime(tv_time.getText().toString());
        machineNo.setReturnAddress(et_address.getText().toString().trim());
        machineNo.setReturnType(type);
        machineNo.setRemarks(et_remark.getText().toString().trim());
        machineNo.setReturnState("申请中");

        return machineNo;
    }

}
