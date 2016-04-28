package com.gzrijing.workassistant.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gzrijing.workassistant.base.MyApplication;
import com.gzrijing.workassistant.db.BusinessData;
import com.gzrijing.workassistant.db.DetailedInfoData;
import com.gzrijing.workassistant.db.ImageData;
import com.gzrijing.workassistant.db.TimeData;
import com.gzrijing.workassistant.entity.BusinessByLeader;
import com.gzrijing.workassistant.entity.DetailedInfo;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.receiver.NotificationReceiver;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ImageUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class GetLeaderBusinessService extends IntentService {

    private String userNo;
    private ImageLoader mImageLoader;
    private Handler handler = new Handler();

    public GetLeaderBusinessService() {
        super("GetLeaderBusinessService");
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String sitLeader = intent.getStringExtra("sitLeader");
        if(sitLeader.equals("3")){
            Intent intent1 = new Intent("action.com.gzrijing.workassistant.InspectionStationFragment");
            sendBroadcast(intent1);
            sendNotification();
        }else{
            SharedPreferences sp = getSharedPreferences("saveUser", MODE_PRIVATE);
            userNo = sp.getString("userNo", "");

            List<TimeData> timeDataList = DataSupport.select("time").where("userNo = ?", userNo).find(TimeData.class);
            String time = "2016-1-10 10:00:00";
            if (!timeDataList.toString().equals("[]")) {
                Log.e("time", timeDataList.get(0).getTime());
                time = timeDataList.get(0).getTime();
            } else {
                TimeData timeData = new TimeData();
                timeData.setTime(time);
                timeData.setUserNo(userNo);
                timeData.save();
            }
            String url = null;
            try {
                url = "?cmd=getconstruction&userno=" + URLEncoder.encode(userNo, "UTF-8") + "&begindate=" + URLEncoder.encode(time, "UTF-8");
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
                            ToastUtil.showToast(GetLeaderBusinessService.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                        }
                    });
                }
            });
        }

    }


    private void saveData(String data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String time = sdf.format(new Date(System.currentTimeMillis()));

        ContentValues values = new ContentValues();
        values.put("time", time);
        DataSupport.updateAll(TimeData.class, values, "userNo = ?", userNo);

        List<BusinessByLeader> list = JsonParseUtils.getLeaderBusiness(data);
        for (final BusinessByLeader order : list) {
            BusinessData data1 = new BusinessData();
            data1.setUser(userNo);
            data1.setOrderId(order.getOrderId());
            data1.setUrgent(order.isUrgent());
            data1.setType(order.getType());
            data1.setState(order.getState());
            data1.setReceivedTime(order.getReceivedTime());
            data1.setDeadline(order.getDeadline());
            data1.setFlag(order.getFlag());
            data1.setTemInfoNum(order.getTemInfoNum());
            List<DetailedInfo> infos = order.getDetailedInfos();
            for (DetailedInfo info : infos) {
                DetailedInfoData data2 = new DetailedInfoData();
                data2.setKey(info.getKey());
                data2.setValue(info.getValue());
                data2.save();
                data1.getDetailedInfoList().add(data2);
            }
            List<PicUrl> picUrls = order.getPicUrls();
            for (final PicUrl picUrl : picUrls) {
                Log.e("picUrl", HttpUtils.imageURLPath + "/Pic/" + picUrl.getPicUrl());
                mImageLoader.loadImage(HttpUtils.imageURLPath + "/Pic/" + picUrl.getPicUrl(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        try {
                            File path = ImageUtils.getImagePath(GetLeaderBusinessService.this, userNo, order.getOrderId());
                            ImageUtils.saveFile(GetLeaderBusinessService.this, loadedImage, picUrl.getPicUrl(), path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                ImageData data3 = new ImageData();
                data3.setUrl(picUrl.getPicUrl());
                data3.save();
                data1.getImageDataList().add(data3);
            }
            data1.save();
        }

        Intent intent = new Intent("action.com.gzrijing.workassistant.LeaderFragment");
        intent.putExtra("jsonData", data);
        sendBroadcast(intent);
    }

    private void sendNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("")
                .setContentText("有一条新工程项目需要确认")
                .setTicker("有一条新工程项目需要确认")
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .build();
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(MyApplication.notificationId++, notification);
    }
}
