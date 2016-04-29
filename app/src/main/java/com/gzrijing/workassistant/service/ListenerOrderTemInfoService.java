package com.gzrijing.workassistant.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;

import com.gzrijing.workassistant.base.MyApplication;
import com.gzrijing.workassistant.db.BusinessData;
import com.gzrijing.workassistant.db.SuppliesNoData;
import com.gzrijing.workassistant.receiver.NotificationReceiver;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ListenerOrderTemInfoService extends IntentService {

    private String userNo;
    private String orderId;

    public ListenerOrderTemInfoService() {
        super("ListenerOrderTemInfoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        userNo = intent.getStringExtra("userNo");
        orderId = intent.getStringExtra("orderId");
        String sitLeader = intent.getStringExtra("sitLeader");
        if(sitLeader.equals("3")){
            Intent intent1 = new Intent("action.com.gzrijing.workassistant.InspectionStationFragment");
            sendBroadcast(intent1);
        }else{
            saveData();
            sendNotification();
        }
    }

    private void saveData() {
        BusinessData businessData = DataSupport.where("user = ? and orderId = ?", userNo, orderId)
                .find(BusinessData.class, true).get(0);
        int num = businessData.getTemInfoNum() + 1;
        ContentValues values = new ContentValues();
        values.put("temInfoNum", num);
        DataSupport.updateAll(BusinessData.class, values, "user = ? and orderId = ?", userNo, orderId);

        Intent intent = new Intent("action.com.gzrijing.workassistant.temInfoNum");
        intent.putExtra("orderId", orderId);
        intent.putExtra("temInfoNum", num);
        sendBroadcast(intent);

    }

    private void sendNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(orderId)
                .setContentText("有一条临时信息")
                .setTicker("有一条临时信息")
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .build();
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(MyApplication.notificationId++, notification);
    }

}
