package com.gzrijing.workassistant.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ToastUtil;

public class GetReportInfoProblemService extends Service {

    private Handler handler = new Handler();

    public GetReportInfoProblemService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        String id = intent.getStringExtra("id");
        String url = "?cmd=getsomeinstallaccident&togetherid="+id;

        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response", response);
                Intent intent1 = new Intent("action.com.gzrijing.workassistant.ReportInfo.problem");
                intent1.putExtra("jsonData", response);
                sendBroadcast(intent1);
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(GetReportInfoProblemService.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });

        return super.onStartCommand(intent, flags, startId);

    }
}
