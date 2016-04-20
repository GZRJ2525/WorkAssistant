package com.gzrijing.workassistant.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;

public class PipeInspectionFormService extends IntentService {

    public PipeInspectionFormService() {
        super("PipeInspectionFormService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String flag = intent.getStringExtra("flag");
        if(flag.equals("0")){
            uploadImage(intent);
        }
        if(flag.equals("1")){
            uploadData(intent);
        }

    }

    private void uploadData(Intent intent){
        String type = intent.getStringExtra("type");
        String id = intent.getStringExtra("id");
        String jsonData = intent.getStringExtra("jsonData");
        Log.e("jsonData", jsonData);

        RequestBody requestBody = null;
        if (type.equals("0")) {
            requestBody = new FormEncodingBuilder()
                    .add("cmd", "RecordTaskFire")
                    .add("fileno", id)
                    .add("fileJson", jsonData)
                    .build();
        }
        if (type.equals("1")) {
            requestBody = new FormEncodingBuilder()
                    .add("cmd", "RecordTaskValve")
                    .add("fileno", id)
                    .add("fileJson", jsonData)
                    .build();
        }
        if (type.equals("2")) {
            requestBody = new FormEncodingBuilder()
                    .add("cmd", "RecordTaskSlop")
                    .add("fileno", id)
                    .add("fileJson", jsonData)
                    .build();
        }

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response", response);
                if (response.equals("ok")) {
                    Intent intent1 = new Intent("action.com.gzrijing.workassistant.PipeInspectionForm.uploadData");
                    intent1.putExtra("result", "提交成功");
                    sendBroadcast(intent1);
                } else {
                    Intent intent1 = new Intent("action.com.gzrijing.workassistant.PipeInspectionForm.uploadData");
                    intent1.putExtra("result", "提交失败");
                    sendBroadcast(intent1);
                }

            }

            @Override
            public void onError(Exception e) {
                Intent intent1 = new Intent("action.com.gzrijing.workassistant.PipeInspectionForm.uploadData");
                intent1.putExtra("result", "与服务器断开连接");
                sendBroadcast(intent1);
            }
        });

    }

    private void uploadImage(Intent intent){
        ArrayList<PicUrl> picUrls = intent.getParcelableArrayListExtra("picUrls");
        picUrls.remove(0);
        String userNo = intent.getStringExtra("userNo");
        String type = intent.getStringExtra("type");
        String id = intent.getStringExtra("id");

        for (int i = 0; i < picUrls.size(); i++) {
            String[] key = {"cmd", "userno", "class", "picdescription", "fileno"};
            String[] value = {"uploadtaskpicValve", userNo, type, "", id};

            MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
            for (int j = 0; j < key.length; j++) {
                builder.addFormDataPart(key[j], value[j]);
            }
            File file = new File(picUrls.get(i).getPicUrl());
            if (file != null) {
                String fileName = file.getName();
                RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                builder.addFormDataPart("constaskpic", fileName, fileBody);
            }

            HttpUtils.sendHttpPostRequest(builder.build(), new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Log.e("response", response);
                    if (response.substring(0, 1).equals("E")) {
                        Intent intent1 = new Intent("action.com.gzrijing.workassistant.PipeInspectionForm.uploadImage");
                        intent1.putExtra("result", "提交失败");
                        sendBroadcast(intent1);
                    } else {
                        Intent intent1 = new Intent("action.com.gzrijing.workassistant.PipeInspectionForm.uploadImage");
                        intent1.putExtra("result", "提交成功");
                        sendBroadcast(intent1);
                    }
                }

                @Override
                public void onError(Exception e) {
                    Intent intent1 = new Intent("action.com.gzrijing.workassistant.PipeInspectionForm");
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
