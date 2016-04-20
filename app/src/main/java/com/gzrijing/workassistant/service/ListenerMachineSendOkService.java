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

public class ListenerMachineSendOkService extends IntentService {

    public ListenerMachineSendOkService() {
        super("ListenerMachineSendOkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userNo = intent.getStringExtra("userNo");
        String orderId = intent.getStringExtra("orderId");
        String billNo = intent.getStringExtra("billNo");
        String machineNo = intent.getStringExtra("machineNo");

        saveData(machineNo);
        sendNotification(orderId);

    }

    private void saveData(String machineNo) {
        ContentValues values = new ContentValues();
        values.put("receivedState", "已送达");
        DataSupport.updateAll(MachineData.class, values, "No = ?", machineNo);

        Intent intent = new Intent("action.com.gzrijing.workassistant.MachineApply.refresh");
        sendBroadcast(intent);

    }

    private void sendNotification(String orderId) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(orderId)
                .setContentText("有一台机械已送达现场")
                .setTicker("有一台机械已送达现场")
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .build();
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(MyApplication.notificationId++, notification);
    }

}
