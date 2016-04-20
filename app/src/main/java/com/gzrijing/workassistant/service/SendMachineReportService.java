package com.gzrijing.workassistant.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;

public class SendMachineReportService extends IntentService {

    public SendMachineReportService() {
        super("SendMachineReportService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        ArrayList<PicUrl> picUrls = intent.getParcelableArrayListExtra("picUrls");
        picUrls.remove(0);
        String userNo = intent.getStringExtra("userNo");
        String sendId = intent.getStringExtra("sendId");

        for (int i = 0; i < picUrls.size(); i++) {
            String[] key = {"cmd", "userno", "relationid", "picdescription"};
            String[] value = {"uploadmachinesendpic", userNo, sendId, picUrls.get(i).getPicTime()};

            MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
            for (int j = 0; j < key.length; j++) {
                builder.addFormDataPart(key[j], value[j]);
            }
            File file = new File(picUrls.get(i).getPicUrl());
            if (file != null) {
                String fileName = file.getName();
                RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                builder.addFormDataPart("", fileName, fileBody);
            }
            HttpUtils.sendHttpPostRequest(builder.build(), new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Log.e("res", response);
                    if (response.substring(0, 1).equals("E")) {
                        Intent intent1 = new Intent("action.com.gzrijing.workassistant.SendMachineReport");
                        intent1.putExtra("result", "汇报失败");
                        sendBroadcast(intent1);
                    } else {
                        Intent intent1 = new Intent("action.com.gzrijing.workassistant.SendMachineReport");
                        intent1.putExtra("result", "汇报成功");
                        sendBroadcast(intent1);
                    }

                }

                @Override
                public void onError(Exception e) {
                    Intent intent1 = new Intent("action.com.gzrijing.workassistant.SendMachineReport");
                    intent1.putExtra("result", "与服务器断开连接");
                    sendBroadcast(intent1);
                }
            });

        }
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }
}
