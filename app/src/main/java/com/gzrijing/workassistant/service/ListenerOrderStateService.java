package com.gzrijing.workassistant.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.gzrijing.workassistant.base.MyApplication;
import com.gzrijing.workassistant.db.BusinessData;
import com.gzrijing.workassistant.receiver.NotificationReceiver;

import org.litepal.crud.DataSupport;

public class ListenerOrderStateService extends IntentService {


    public ListenerOrderStateService() {
        super("ListenerOrderStateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userNo = intent.getStringExtra("userNo");
        String orderId = intent.getStringExtra("orderId");
        String state = intent.getStringExtra("state");

        Log.e("orderId", orderId);

        BusinessData businessData = DataSupport.where("user = ? and orderId = ?", userNo, orderId)
                .find(BusinessData.class, true).get(0);

        ContentValues values = new ContentValues();
        values.put("state", state);
        DataSupport.update(BusinessData.class, values, businessData.getId());


        Intent intent1 = new Intent("action.com.gzrijing.workassistant.WorkerFragment.state");
        intent1.putExtra("orderId", orderId);
        intent1.putExtra("state", state);
        sendBroadcast(intent1);

        sendNotification(orderId);

    }

    private void sendNotification(String orderId) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(orderId)
                .setContentText("工程状态更新了")
                .setTicker("工程状态更新了")
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .build();
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(MyApplication.notificationId++, notification);
    }

}
