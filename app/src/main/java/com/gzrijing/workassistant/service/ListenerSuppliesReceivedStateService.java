package com.gzrijing.workassistant.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.gzrijing.workassistant.base.MyApplication;
import com.gzrijing.workassistant.db.BusinessData;
import com.gzrijing.workassistant.db.SuppliesData;
import com.gzrijing.workassistant.db.SuppliesNoData;
import com.gzrijing.workassistant.entity.Supplies;
import com.gzrijing.workassistant.entity.SuppliesNo;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.receiver.NotificationReceiver;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import org.litepal.crud.DataSupport;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class ListenerSuppliesReceivedStateService extends IntentService {

    private Handler handler = new Handler();
    private String userNo;
    private String orderId;

    public ListenerSuppliesReceivedStateService() {
        super("ListenerSuppliesReceivedStateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sp = getSharedPreferences("saveUser", MODE_PRIVATE);
        String userRank = sp.getString("userRank", "");
        userNo = intent.getStringExtra("userNo");
        orderId = intent.getStringExtra("orderId");
        String billNo = intent.getStringExtra("billNo");

        if(userRank.equals("0")){
            String url = null;
            try {
                url = "?cmd=getmaterialsend&userno=" + URLEncoder.encode(userNo, "UTF-8") +
                        "&checkdate=&fileno=" + URLEncoder.encode(orderId, "UTF-8") +
                        "&billno=" + URLEncoder.encode(billNo, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

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
                            ToastUtil.showToast(ListenerSuppliesReceivedStateService.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                        }
                    });
                }
            });
        }else{
            Intent intent1 = new Intent("action.com.gzrijing.workassistant.SuppliesApply.refresh");
            sendBroadcast(intent1);
            sendNotification();
        }
    }

    private void saveData(String jsonData) {
        BusinessData businessData = DataSupport.where("user = ? and orderId = ?", userNo, orderId)
                .find(BusinessData.class, true).get(0);

        List<SuppliesNo> suppliesNoList = JsonParseUtils.getLitenerSuppliesNoReceivedState(jsonData);
        for (SuppliesNo suppliesNo : suppliesNoList) {
            SuppliesNoData suppliesNoData = new SuppliesNoData();
            suppliesNoData.setApplyId(suppliesNo.getApplyId());
            suppliesNoData.setReceivedId(suppliesNo.getReceivedId());
            suppliesNoData.setReceivedState(suppliesNo.getReceivedState());
            suppliesNoData.save();
            businessData.getSuppliesNoList().add(suppliesNoData);
        }

        List<Supplies> suppliesList = JsonParseUtils.getLitenerSuppliesReceivedState(jsonData);
        for(Supplies supplies : suppliesList){
            SuppliesData suppliesData = new SuppliesData();
            suppliesData.setReceivedId(suppliesNoList.get(0).getReceivedId());
            suppliesData.setNo(supplies.getId());
            suppliesData.setName(supplies.getName());
            suppliesData.setSpec(supplies.getSpec());
            suppliesData.setUnit(supplies.getUnit());
            suppliesData.setApplyNum(supplies.getApplyNum());
            suppliesData.setSendNum(supplies.getSendNum());
            suppliesData.save();
            businessData.getSuppliesDataList().add(suppliesData);
        }
        businessData.save();

        Intent intent = new Intent("action.com.gzrijing.workassistant.SuppliesApply.refresh");
        sendBroadcast(intent);

    }

    private void sendNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(orderId)
                .setContentText("有一条材料申请单可领用")
                .setTicker("有一条材料申请单可领用")
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .build();
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(MyApplication.notificationId++, notification);
    }

}
