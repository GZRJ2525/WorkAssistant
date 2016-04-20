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
import com.gzrijing.workassistant.entity.BusinessByWorker;
import com.gzrijing.workassistant.entity.DetailedInfo;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.receiver.NotificationReceiver;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ImageUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.gzrijing.workassistant.util.VoiceUtil;
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

public class GetWorkerBusinessService extends IntentService {

    private String userNo;
    private ImageLoader mImageLoader;
    private Handler handler = new Handler();


    public GetWorkerBusinessService() {
        super("GetWorkerBusinessService");
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sp = getSharedPreferences("saveUser", MODE_PRIVATE);
        userNo = sp.getString("userNo", "");
        String userRank = sp.getString("userRank", "");

        if (userRank.equals("0")) {
            List<TimeData> timeDataList = DataSupport.select("time").where("userNo = ?", userNo).find(TimeData.class);
            String time = "2016-1-10 10:00:00";
            if (!timeDataList.toString().equals("[]")) {
                time = timeDataList.get(0).getTime();
            } else {
                TimeData timeData = new TimeData();
                timeData.setTime(time);
                timeData.setUserNo(userNo);
                timeData.save();
            }
            String url = null;
            try {
                url = "?cmd=getmycons&userno=" + URLEncoder.encode(userNo, "UTF-8") + "&fileno=&begindate=" + URLEncoder.encode(time, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Log.e("response", response);
                    saveData(response);
                    sendNotification1();
                }

                @Override
                public void onError(Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(GetWorkerBusinessService.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                        }
                    });
                }
            });
        } else {
            sendNotification();
        }

    }


    private void saveData(String data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String time = sdf.format(new Date(System.currentTimeMillis()));

        ContentValues values = new ContentValues();
        values.put("time", time);
        DataSupport.updateAll(TimeData.class, values, "userNo = ?", userNo);

        List<BusinessByWorker> list = JsonParseUtils.getWorkerBusiness(data);
        for (final BusinessByWorker order : list) {
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
            data1.setRecordFileName(order.getRecordFileName());
            if (order.getRecordFileName() != null && !order.getRecordFileName().equals("")) {
                String url = HttpUtils.voiceURLPath + order.getRecordFileName();
                File dir = VoiceUtil.getVoicePath(this, userNo, order.getOrderId());
                HttpUtils.downloadFile(url, dir, new HttpCallbackListener() {
                    @Override
                    public void onFinish(final String response) {
                        Log.e("response", response);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!response.equals("ok")) {
                                    ToastUtil.showToast(GetWorkerBusinessService.this, order.getOrderId() + "下载录音文件失败", Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(GetWorkerBusinessService.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                            }
                        });
                    }
                });
            }

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
                mImageLoader.loadImage(HttpUtils.imageURLPath + "/Pic/" + picUrl.getPicUrl(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        try {
                            File path = ImageUtils.getImagePath(GetWorkerBusinessService.this, userNo, order.getOrderId());
                            ImageUtils.saveFile(GetWorkerBusinessService.this, loadedImage, picUrl.getPicUrl(), path);
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

        Intent intent = new Intent("action.com.gzrijing.workassistant.WorkerFragment");
        intent.putExtra("jsonData", data);
        sendBroadcast(intent);
    }

    private void sendNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("")
                .setContentText("有一条我的任务需要确认")
                .setTicker("有一条我的任务需要确认")
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .build();
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(MyApplication.notificationId++, notification);
    }

    private void sendNotification1() {
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
