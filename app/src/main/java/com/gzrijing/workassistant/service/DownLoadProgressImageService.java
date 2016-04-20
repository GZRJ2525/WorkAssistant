package com.gzrijing.workassistant.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DownLoadProgressImageService extends Service {
    private Handler handler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String fileNo = intent.getStringExtra("fileNo");
        String id = intent.getStringExtra("id");
        String url = null;
        try {
            url = "?cmd=getconspic&fileno=" + URLEncoder.encode(fileNo, "UTF-8")
                    + "&relationid=" + id + "&pictype=WnW_ConsTaskPic";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response", response);
                Intent intent1 = new Intent("action.com.gzrijing.workassistant.ReportInfoProgress");
                intent1.putExtra("response", response);
                sendBroadcast(intent1);
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(DownLoadProgressImageService.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });


        return super.onStartCommand(intent, flags, startId);
    }
}
