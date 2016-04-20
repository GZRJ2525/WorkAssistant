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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GetMachineVerifyService extends Service{

    private Handler handler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        String userNo = intent.getStringExtra("userNo");
        String orderId = intent.getStringExtra("orderId");

        String url = null;
        try {
            url = "?cmd=getmachineneedlist&userno="+URLEncoder.encode(userNo, "UTF-8")+"&savedate=&billno=&fileno="
                    + URLEncoder.encode(orderId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Intent intent1 = new Intent("action.com.gzrijing.workassistant.MachineVerify");
                intent1.putExtra("jsonData", response);
                sendBroadcast(intent1);
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(GetMachineVerifyService.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });


        return super.onStartCommand(intent, flags, startId);
    }
}
