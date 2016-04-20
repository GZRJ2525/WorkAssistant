package com.gzrijing.workassistant.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GetSewageWellsService extends IntentService {
    private Handler handler = new Handler();

    public GetSewageWellsService() {
        super("GetSewageWellsService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        String userNo = intent.getStringExtra("userNo");
        String url = null;
        try {
            url = "?cmd=GetPlanSlop&userno=" + URLEncoder.encode(userNo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("污水井计划", response);
                Intent intent1 = new Intent("action.com.gzrijing.workassistant.WorkerFragment.Inspection");
                intent1.putExtra("jsonData", response);
                sendBroadcast(intent1);
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(GetSewageWellsService.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }
}
