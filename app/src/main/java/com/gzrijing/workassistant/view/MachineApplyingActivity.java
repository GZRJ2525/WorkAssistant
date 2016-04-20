package com.gzrijing.workassistant.view;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.MachineApplyingAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.db.BusinessData;
import com.gzrijing.workassistant.db.MachineData;
import com.gzrijing.workassistant.db.MachineNoData;
import com.gzrijing.workassistant.db.SuppliesNoData;
import com.gzrijing.workassistant.entity.Machine;
import com.gzrijing.workassistant.entity.MachineNo;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JudgeDate;
import com.gzrijing.workassistant.util.ToastUtil;
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
import java.util.List;

public class MachineApplyingActivity extends BaseActivity implements View.OnClickListener{

    private int position;
    private String userNo;
    private String orderId;
    private MachineNo machineNo;
    private ArrayList<Machine> machineList = new ArrayList<Machine>();
    private TextView tv_reason;
    private TextView tv_useTime;
    private TextView tv_returnTime;
    private EditText et_useAddress;
    private EditText et_remarks;
    private Button btn_edit;
    private ListView lv_list;
    private WheelMain wheelMain;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private MachineApplyingAdapter adapter;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_applying);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        Intent intent = getIntent();
        userNo = intent.getStringExtra("userNo");
        orderId = intent.getStringExtra("orderId");
        position = intent.getIntExtra("position", -1);
        machineNo = (MachineNo)intent.getParcelableExtra("machineNo");
        List<MachineData> machineDataList  = DataSupport.where("applyId = ?", machineNo.getApplyId()).find(MachineData.class);
        for(MachineData data : machineDataList){
            Machine machine = new Machine();
            machine.setId(data.getNo());
            machine.setName(data.getName());
            machine.setUnit(data.getUnit());
            machine.setApplyNum(data.getApplyNum());
            machineList.add(machine);
        }
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_reason = (TextView) findViewById(R.id.machine_applying_reason_tv);
        tv_reason.setText(machineNo.getReason());
        tv_useTime = (TextView) findViewById(R.id.machine_applying_use_time_tv);
        tv_useTime.setText(machineNo.getUseTime());
        et_useAddress = (EditText) findViewById(R.id.machine_applying_use_address_et);
        et_useAddress.setText(machineNo.getUseAddress());
        et_remarks = (EditText) findViewById(R.id.machine_applying_remarks_et);
        et_remarks.setText(machineNo.getRemarks());
        tv_returnTime = (TextView) findViewById(R.id.machine_applying_return_time_tv);
        tv_returnTime.setText(machineNo.getReturnTime());
        btn_edit = (Button) findViewById(R.id.machine_applying_edit_btn);

        lv_list = (ListView) findViewById(R.id.machine_applying_lv);
        adapter = new MachineApplyingAdapter(this, machineList);
        lv_list.setAdapter(adapter);
    }

    private void setListeners() {
        tv_useTime.setOnClickListener(this);
        tv_returnTime.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.machine_applying_use_time_tv:
                getUseTime(tv_useTime);
                break;

            case R.id.machine_applying_return_time_tv:
                getUseTime(tv_returnTime);
                break;

            case R.id.machine_applying_edit_btn:
                Intent intent = new Intent(this, MachineApplyEditActivity.class);
                intent.putParcelableArrayListExtra("machineList", machineList);
                startActivityForResult(intent, 10);
                break;
        }
    }

    private void getUseTime(final TextView tv_time) {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View timepickerview = inflater.inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(this);
        wheelMain = new WheelMain(timepickerview, true);
        wheelMain.screenheight = screenInfo.getHeight();
        String time = tv_time.getText().toString();
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
                        tv_time.setText(wheelMain.getTime());
                        tv_time.setTextColor(getResources().getColor(
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
                ArrayList<Machine> machines = data.getParcelableArrayListExtra("machineList");
                machineList.clear();
                machineList.addAll(machines);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_machine_applying, menu);
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
        String useTime = tv_useTime.getText().toString();
        String returnTime = tv_returnTime.getText().toString();
        String useAddress = et_useAddress.getText().toString().trim();
        String remarks = et_remarks.getText().toString().trim();
        if (useAddress.equals("")) {
            ToastUtil.showToast(MachineApplyingActivity.this, "请填写使用地点", Toast.LENGTH_SHORT);
            return;
        }

        JSONArray jsonArray = new JSONArray();
        for (Machine machine : machineList) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", "");
                jsonObject.put("MachineName", machine.getName());
                jsonObject.put("Qty", machine.getApplyNum());
                jsonObject.put("BeginDate", useTime);
                jsonObject.put("EstimateFinishDate", returnTime);
                jsonObject.put("Remark", remarks);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.e("json", jsonArray.toString());
        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "dosavemachineneed")
                .add("billno", "")
                .add("billtype", "工程机械申请")
                .add("fileno", orderId)
                .add("proaddress", useAddress)
                .add("userno", userNo)
                .add("machinedetailjson", jsonArray.toString())
                .build();

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                if (response.substring(0, 1).equals("E")) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(MachineApplyingActivity.this, "申请失败", Toast.LENGTH_SHORT);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MachineNo machineNo = savaMachineNo(response);
                            Intent intent = getIntent();
                            intent.putExtra("position", position);
                            intent.putExtra("machineNo", (Parcelable) machineNo);
                            setResult(20, intent);
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
                        ToastUtil.showToast(MachineApplyingActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private MachineNo savaMachineNo(String applyId) {
        Calendar rightNow = Calendar.getInstance();
        String applyTime = dateFormat.format(rightNow.getTime());

        DataSupport.deleteAll(MachineData.class, "applyId = ?", machineNo.getApplyId());
        DataSupport.deleteAll(SuppliesNoData.class, "applyId = ?", machineNo.getApplyId());

        BusinessData businessData = DataSupport.where("user = ? and orderId = ?", userNo, orderId).find(BusinessData.class, true).get(0);
        for (int i = 0; i < machineList.size(); i++) {
            MachineData data = new MachineData();
            data.setApplyId(applyId);
            data.setNo(machineList.get(i).getId());
            data.setName(machineList.get(i).getName());
            data.setUnit(machineList.get(i).getUnit());
            data.setApplyNum(machineList.get(i).getApplyNum());
            data.save();
            businessData.getMachineDataList().add(data);
        }

        MachineNoData data1 = new MachineNoData();
        data1.setApplyId(applyId);
        data1.setApplyTime(applyTime);
        data1.setUseTime(tv_useTime.getText().toString());
        data1.setReturnTime(tv_returnTime.getText().toString());
        data1.setUseAddress(et_useAddress.getText().toString().trim());
        data1.setRemarks(et_remarks.getText().toString().trim());
        data1.setApplyState("申请中");
        data1.save();
        businessData.getMachineNoList().add(data1);
        businessData.save();

        MachineNo machineNo = new MachineNo();
        machineNo.setApplyId(applyId);
        machineNo.setApplyTime(applyTime);
        machineNo.setUseTime(tv_useTime.getText().toString());
        machineNo.setReturnTime(tv_returnTime.getText().toString());
        machineNo.setUseAddress(et_useAddress.getText().toString().trim());
        machineNo.setRemarks(et_remarks.getText().toString().trim());
        machineNo.setApplyState("申请中");

        return machineNo;
    }

}
