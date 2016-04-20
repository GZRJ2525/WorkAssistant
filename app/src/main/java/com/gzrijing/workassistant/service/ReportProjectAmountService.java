package com.gzrijing.workassistant.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.gzrijing.workassistant.entity.Supplies;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;

public class ReportProjectAmountService extends IntentService {

    public ReportProjectAmountService() {
        super("ReportProjectAmountService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        String userNo = intent.getStringExtra("userNo");
        String orderId = intent.getStringExtra("orderId");
        String type = intent.getStringExtra("type");
        String content = intent.getStringExtra("content");
        String civil = intent.getStringExtra("civil");
        String flag = intent.getStringExtra("flag");
        String id = intent.getStringExtra("id");
        if(id == null){
            id = "";
        }
        ArrayList<Supplies> suppliesList = intent.getParcelableArrayListExtra("suppliesList");

        JSONArray jsonArray = new JSONArray();
        for(Supplies supplies : suppliesList){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("MakingNo", supplies.getId());
                jsonObject.put("Qty", supplies.getNum());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "doconsconfirm")
                .add("userno", userNo)
                .add("id", id)
                .add("fileno", orderId)
                .add("receivables", type)
                .add("conscontent", content)
                .add("earthworkcontent", civil)
                .add("isneedcheck", flag)
                .add("consmakingjson", jsonArray.toString())
                .build();

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("respone", response);
                if (response.substring(0, 1).equals("E")) {
                    Intent intent1 = new Intent("action.com.gzrijing.workassistant.ReportProjectAmountFragment");
                    intent1.putExtra("result", "汇报失败");
                    sendBroadcast(intent1);
                } else {
                    Intent intent1 = new Intent("action.com.gzrijing.workassistant.ReportProjectAmountFragment");
                    intent1.putExtra("result", "汇报成功");
                    sendBroadcast(intent1);
                }
            }

            @Override
            public void onError(Exception e) {
                Intent intent1 = new Intent("action.com.gzrijing.workassistant.ReportProjectAmountFragment");
                intent1.putExtra("result", "与服务器断开连接");
                sendBroadcast(intent1);
            }
        });

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
