package com.gzrijing.workassistant.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.gzrijing.workassistant.base.MyApplication;
import com.gzrijing.workassistant.db.BusinessData;
import com.gzrijing.workassistant.db.MachineData;
import com.gzrijing.workassistant.db.MachineNoData;
import com.gzrijing.workassistant.receiver.NotificationReceiver;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ListenerMachineBackOkService extends IntentService {

    public ListenerMachineBackOkService() {
        super("ListenerMachineBackOkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userNo = intent.getStringExtra("userNo");
        String orderId = intent.getStringExtra("orderId");
        String billNo = intent.getStringExtra("billNo");

        BusinessData businessData = DataSupport.where("user = ? and orderId = ?", userNo, orderId).find(BusinessData.class, true).get(0);
        List<MachineData> returnList = DataSupport.where("returnId = ?", billNo).find(MachineData.class);
        List<MachineData> machineDataList = businessData.getMachineDataList();
        for (MachineData machineData : machineDataList) {
            if (machineData.getReceivedState() != null) {
                if (machineData.getReceivedState().equals("已送达")) {
                    for(MachineData returnData : returnList){
                        if(returnData.getNo().equals(machineData.getNo())){
                            if(machineData.getSendNum() - returnData.getApplyNum() == 0){
                                DataSupport.delete(MachineData.class, machineData.getId());
                            }else{
                                ContentValues values = new ContentValues();
                                values.put("sendNum", machineData.getSendNum() - returnData.getApplyNum());
                                DataSupport.update(MachineData.class, values, machineData.getId());
                            }
                        }
                    }
                }
            }
        }

        saveData(billNo);
        sendNotification(orderId, billNo);

    }

    private void saveData(String billNo) {
        DataSupport.deleteAll(MachineNoData.class, "returnId = ?", billNo);

        DataSupport.deleteAll(MachineData.class, "returnId = ?", billNo);

        Intent intent = new Intent("action.com.gzrijing.workassistant.MachineApply.refresh");
        sendBroadcast(intent);

    }

    private void sendNotification(String orderId, String billNo) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(orderId)
                .setContentText("退机单：" + billNo + "的机械已退完")
                .setTicker("退机单：" + billNo + "的机械已退完")
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .build();
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(MyApplication.notificationId++, notification);
    }

}
