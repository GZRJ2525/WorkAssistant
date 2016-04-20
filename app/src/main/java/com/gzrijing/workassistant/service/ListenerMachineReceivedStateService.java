package com.gzrijing.workassistant.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.gzrijing.workassistant.base.MyApplication;
import com.gzrijing.workassistant.db.BusinessData;
import com.gzrijing.workassistant.db.MachineData;
import com.gzrijing.workassistant.entity.Machine;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.receiver.NotificationReceiver;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import org.litepal.crud.DataSupport;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class ListenerMachineReceivedStateService extends IntentService {

    private Handler handler = new Handler();
    private String userNo;
    private String orderId;
    private String billNo;

    public ListenerMachineReceivedStateService() {
        super("ListenerMachineReceivedStateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        userNo = intent.getStringExtra("userNo");
        orderId = intent.getStringExtra("orderId");
        billNo = intent.getStringExtra("billNo");

        String url = null;
        try {
            url = "?cmd=getmymachineneed&userno=" + URLEncoder.encode(userNo, "UTF-8") +
                    "&billno=" + URLEncoder.encode(billNo, "UTF-8") +
                    "&fileno=" + URLEncoder.encode(orderId, "UTF-8")+"&checkdate=";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.e("url", url);
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response", response);
                saveData(response);
                sendNotification();
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(ListenerMachineReceivedStateService.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private void saveData(String jsonData) {
        BusinessData businessData = DataSupport.where("user = ? and orderId = ?", userNo, orderId)
                .find(BusinessData.class, true).get(0);

        List<Machine> machines = JsonParseUtils.getLitenerMachineSendNum(jsonData);
        List<MachineData> machineDatas = DataSupport.where("applyId = ? and receivedState=?", billNo, "").find(MachineData.class);
        for(Machine machine : machines){
            for (MachineData data : machineDatas) {
                if(data.getName().equals(machine.getName())){
                    ContentValues values = new ContentValues();
                    values.put("sendNum", machine.getSendNum());
                    DataSupport.updateAll(MachineData.class, values, "applyId = ? and name = ? and receivedState = ?", billNo, machine.getName(), "");
                }
            }
        }

        List<MachineData> machineDataList = DataSupport.where("applyId = ?", billNo).find(MachineData.class);
        List<Machine> machineList = JsonParseUtils.getLitenerMachineReceivedState(jsonData);
        int count = 0;
        for(Machine machine : machineList){
            for(MachineData data : machineDataList){
                if(machine.getId().equals(data.getNo())){
                    break;
                }else{
                    count++;
                }
            }

            if(count == machineDataList.size()){
                MachineData machineData = new MachineData();
                machineData.setApplyId(machine.getApplyId());
                machineData.setNo(machine.getId());
                machineData.setName(machine.getName());
                machineData.setUnit(machine.getUnit());
                machineData.setSendNum(machine.getSendNum());
                machineData.setReceivedState(machine.getState());
                machineData.save();
                businessData.getMachineDataList().add(machineData);
            }
            count = 0;
        }
        businessData.save();

        Intent intent = new Intent("action.com.gzrijing.workassistant.MachineApply.refresh");
        sendBroadcast(intent);

    }

    private void sendNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(orderId)
                .setContentText("有一些机械已安排")
                .setTicker("有一些机械已安排")
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .build();
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(MyApplication.notificationId++, notification);
    }

}
