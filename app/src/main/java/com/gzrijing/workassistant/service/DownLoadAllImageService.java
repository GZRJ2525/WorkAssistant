package com.gzrijing.workassistant.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ToastUtil;

public class DownLoadAllImageService extends Service {
    private Handler handler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String orderId = intent.getStringExtra("orderId");
        String url = "?cmd=getconspic&fileno=" + orderId + "&relationid=&pictype=V_App_ConsAllPic";

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Intent intent1 = new Intent("action.com.gzrijing.workassistant.Distribute");
                intent1.putExtra("response", response);
                sendBroadcast(intent1);
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(DownLoadAllImageService.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }
}
