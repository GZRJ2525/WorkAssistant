package com.gzrijing.workassistant.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.gzrijing.workassistant.base.MyApplication;
import com.gzrijing.workassistant.receiver.NotificationReceiver;

public class ListenerReportInfoProjectAmountService extends IntentService {

    private String orderId;

    public ListenerReportInfoProjectAmountService() {
        super("ListenerReportInfoProjectAmountService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        orderId = intent.getStringExtra("orderId");

        Intent intent1 = new Intent("action.com.gzrijing.workassistant.ReportInfo.projectAmount.refresh");
        sendBroadcast(intent1);

        sendNotification();

    }

    private void sendNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(orderId)
                .setContentText("有一条新的工程量汇报信息")
                .setTicker("有一条新的工程量汇报信息")
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .build();
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(MyApplication.notificationId++, notification);
    }

}
