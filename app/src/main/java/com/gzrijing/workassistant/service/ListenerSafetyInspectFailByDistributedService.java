package com.gzrijing.workassistant.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.gzrijing.workassistant.base.MyApplication;
import com.gzrijing.workassistant.receiver.NotificationReceiver;

public class ListenerSafetyInspectFailByDistributedService extends IntentService {

    public ListenerSafetyInspectFailByDistributedService() {
        super("ListenerSafetyInspectFailByDistributedService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String orderId = intent.getStringExtra("orderId");

        sendNotification(orderId);

    }

    private void sendNotification(String orderId) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(orderId)
                .setContentText("有安全检查不合格项信息更新")
                .setTicker("有安全检查不合格项信息更新")
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .build();
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(MyApplication.notificationId++, notification);
    }

}
