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

public class ListenerSuppliesApplyStateService extends IntentService {

    private Handler handler = new Handler();
    private String userNo;
    private String orderId;

    public ListenerSuppliesApplyStateService() {
        super("ListenerSuppliesApplyStateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sp = getSharedPreferences("saveUser", MODE_PRIVATE);
        String userRank = sp.getString("userRank", "");
        userNo = intent.getStringExtra("userNo");
        orderId = intent.getStringExtra("orderId");

        if (userRank.equals("0")) {
            String url = null;
            try {
                url = "?cmd=getmymaterialneedmain&userno=" + URLEncoder.encode(userNo, "UTF-8") +
                        "&checkdate=&fileno=" + URLEncoder.encode(orderId, "UTF-8");
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
                            ToastUtil.showToast(ListenerSuppliesApplyStateService.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                        }
                    });
                }
            });
        } else {
            Intent intent1 = new Intent("action.com.gzrijing.workassistant.SuppliesApply.refresh");
            sendBroadcast(intent1);
            sendNotification();
        }


    }

    private void saveData(String jsonData) {
        BusinessData businessData = DataSupport.where("user = ? and orderId = ?", userNo, orderId)
                .find(BusinessData.class, true).get(0);
        List<SuppliesNoData> suppliesNoDataList = businessData.getSuppliesNoList();
        for (int i = 0; i < suppliesNoDataList.size(); i++) {
            if(suppliesNoDataList.get(i).getReceivedId() != null
                    && !suppliesNoDataList.get(i).getReceivedId().equals("")){
                suppliesNoDataList.remove(i);
            }
        }

        List<SuppliesNo> list = JsonParseUtils.getLitenerSuppliesApplyState(jsonData);
        for (SuppliesNo suppliesNo : list) {
            if (suppliesNo.getApplyState().equals("不批准")) {
                for (SuppliesNoData suppliesNoData : suppliesNoDataList) {
                    if (suppliesNo.getApplyId().equals(suppliesNoData.getApplyId())) {
                        ContentValues values = new ContentValues();
                        values.put("applyState", "不批准");
                        values.put("reason", suppliesNo.getReason());
                        DataSupport.updateAll(SuppliesNoData.class, values, "applyId = ?", suppliesNoData.getApplyId());
                    }
                }
            }
            if (suppliesNo.getApplyState().equals("已审批")) {
                for (SuppliesNoData suppliesNoData : suppliesNoDataList) {
                    if (suppliesNo.getApplyId().equals(suppliesNoData.getApplyId())) {
                        ContentValues values = new ContentValues();
                        values.put("applyState", "已审批");
                        values.put("approvalTime", suppliesNo.getApprovalTime());
                        DataSupport.updateAll(SuppliesNoData.class, values, "applyId = ?", suppliesNoData.getApplyId());
                    }
                }
            }
        }

        Intent intent = new Intent("action.com.gzrijing.workassistant.SuppliesApply.refresh");
        sendBroadcast(intent);

    }

    private void sendNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(orderId)
                .setContentText("材料申请单已审核")
                .setTicker("材料申请单已审核")
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .build();
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(MyApplication.notificationId++, notification);
    }

}
