package com.gzrijing.workassistant.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.util.JudgeDate;
import com.gzrijing.workassistant.widget.selectdate.ScreenInfo;
import com.gzrijing.workassistant.widget.selectdate.WheelMain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TrajectoryQueryDialog extends Dialog implements View.OnClickListener{

    private Context context;
    private TextView tv_beginTime;
    private TextView tv_endTime;
    private Button btn_confirm;
    private Button btn_cancel;
    private WheelMain wheelMain;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private ClickListenerCallBack clickListenerCallBack;

    public TrajectoryQueryDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        setListeners();
    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_trajectory_query, null);
        setContentView(view);

        tv_beginTime = (TextView) findViewById(R.id.dialog_trajectory_query_begin_time_tv);
        tv_endTime = (TextView) findViewById(R.id.dialog_trajectory_query_end_time_tv);
        btn_confirm = (Button) findViewById(R.id.dialog_trajectory_query_confirm_btn);
        btn_cancel = (Button) findViewById(R.id.dialog_trajectory_query_cancel_btn);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8);     // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(lp);

    }

    private void setListeners() {
        tv_beginTime.setOnClickListener(this);
        tv_endTime.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_trajectory_query_begin_time_tv:
                getTime(tv_beginTime);
                break;

            case R.id.dialog_trajectory_query_end_time_tv:
                getTime(tv_endTime);
                break;

            case R.id.dialog_trajectory_query_confirm_btn:
                clickListenerCallBack.doConfirm(tv_beginTime.getText().toString(), tv_endTime.getText().toString());
                break;

            case R.id.dialog_trajectory_query_cancel_btn:
                clickListenerCallBack.doCancel();
                break;
        }
    }

    private void getTime(final TextView tv_time) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View timepickerview = inflater.inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo((Activity) context);
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
        new AlertDialog.Builder(context)
                .setTitle("选择时间")
                .setView(timepickerview)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_time.setText(wheelMain.getTime());
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    public void setClickListenerCallBack(ClickListenerCallBack clickListenerCallBack) {
        this.clickListenerCallBack = clickListenerCallBack;
    }

    public interface ClickListenerCallBack{
        void doConfirm(String beginTime, String endTime);
        void doCancel();
    }

}
