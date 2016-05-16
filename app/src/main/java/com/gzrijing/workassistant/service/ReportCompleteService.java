package com.gzrijing.workassistant.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.entity.ReportComplete;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;

public class ReportCompleteService extends IntentService {

    private String receivables;         //收款性质
    private String contentresult;       //维修内容
    private String drainsize;           //排水口径
    private String draintime;           //排水时间
    private String begindate;           //施工日期
    private String finishdate;          //完工日期
    private String acceptdate;          //验收日期
    private String meterbodyno;         //表身编号
    private String meterproductionplace;//水表产地
    private String metereffectivedate;  //水表有效日期
    private String readmeterdegree;     //行至度数
    private String readmeterdate;       //抄表日期
    private String representa;          //甲方代表
    private String supervision;         //监理单位
    private String supervisionname;     //　监理员
    private String installname;         //安装人员
    private String earthworkname;       //土方人员
    private String payunit;             //第三方交款单位
    private String remark4;             //　备　注
    private Handler handler = new Handler();
    private String userNo;

    public ReportCompleteService() {
        super("ReportCompleteService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final ArrayList<PicUrl> picUrls = intent.getParcelableArrayListExtra("picUrls");
        picUrls.remove(0);
        final String orderId = intent.getStringExtra("orderId");
        initData(intent);

        RequestBody requestBody = getRequestBody(intent);

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.e("response", response);
                if (response.equals("ok")) {
                    for (PicUrl picUrl : picUrls) {
                        String[] key = {"cmd", "userno", "fileno", "picdescription"};
                        String[] value = {"uploadconsfinishpic", userNo, orderId, ""};

                        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
                        for (int i = 0; i < key.length; i++) {
                            builder.addFormDataPart(key[i], value[i]);
                        }
                        File file = new File(picUrl.getPicUrl());
                        if (file != null) {
                            String fileName = file.getName();
                            RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                            builder.addFormDataPart("consconfirmpic", fileName, fileBody);
                        }
                        HttpUtils.sendHttpPostRequest(builder.build(), new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                Log.e("res", response);
                                if (response.substring(0, 1).equals("E")) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.showToast(ReportCompleteService.this, "上传图片失败", Toast.LENGTH_SHORT);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToast(ReportCompleteService.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                                    }
                                });
                            }
                        });
                    }

                    Intent intent1 = new Intent("action.com.gzrijing.workassistant.ReportComplete");
                    intent1.putExtra("result", "汇报成功");
                    sendBroadcast(intent1);
                } else {
                    Intent intent1 = new Intent("action.com.gzrijing.workassistant.ReportComplete");
                    intent1.putExtra("result", "汇报失败");
                    intent1.putExtra("response", response);
                    sendBroadcast(intent1);
                }
            }

            @Override
            public void onError(Exception e) {
                Intent intent1 = new Intent("action.com.gzrijing.workassistant.ReportComplete");
                intent1.putExtra("result", "与服务器断开连接");
                sendBroadcast(intent1);
            }
        });
    }

    private void initData(Intent intent) {

        receivables = "noc";
        contentresult = "noc";
        drainsize = "noc";
        draintime = "noc";
        begindate = "noc";
        finishdate = "noc";
        acceptdate = "noc";
        meterbodyno = "noc";
        meterproductionplace = "noc";
        metereffectivedate = "noc";
        readmeterdegree = "noc";
        readmeterdate = "noc";
        representa = "noc";
        supervision = "noc";
        supervisionname = "noc";
        installname = "noc";
        earthworkname = "noc";
        payunit = "noc";
        remark4 = "noc";

    }

    private RequestBody getRequestBody(Intent intent) {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        String orderId = intent.getStringExtra("orderId");
        ArrayList<ReportComplete> infos = intent.getParcelableArrayListExtra("infos");

        for (ReportComplete info : infos) {
            if (info.getKey().equals("维修内容") || info.getKey().equals("施工内容") || info.getKey().equals("处理结果")) {
                contentresult = info.getValue();
            }
            if (info.getKey().equals("排水口径")) {
                drainsize = "DN" + info.getValue();
            }
            if (info.getKey().equals("排水时间")) {
                draintime = info.getValue();
            }
            if (info.getKey().equals("施工日期")) {
                begindate = info.getValue();
            }
            if (info.getKey().equals("完工日期")) {
                finishdate = info.getValue();
            }
            if (info.getKey().equals("验收日期")) {
                acceptdate = info.getValue();
            }
            if (info.getKey().equals("表身编号")) {
                meterbodyno = info.getValue();
            }
            if (info.getKey().equals("水表产地")) {
                meterproductionplace = info.getValue();
            }
            if (info.getKey().equals("水表有效日期")) {
                metereffectivedate = info.getValue();
            }
            if (info.getKey().equals("行至度数")) {
                readmeterdegree = info.getValue();
            }
            if (info.getKey().equals("抄表日期")) {
                readmeterdate = info.getValue();
            }
            if (info.getKey().equals("甲方代表")) {
                representa = info.getValue();
            }
            if (info.getKey().equals("监理单位")) {
                supervision = info.getValue();
            }
            if (info.getKey().equals("　监理员")) {
                supervisionname = info.getValue();
            }
            if (info.getKey().equals("安装人员")) {
                installname = info.getValue();
            }
            if (info.getKey().equals("土方人员")) {
                earthworkname = info.getValue();
            }
            if (info.getKey().equals("第三方交款单位")) {
                payunit = info.getValue();
            }
            if (info.getKey().equals("　备　注")) {
                remark4 = info.getValue();
            }
        }

        RequestBody requestBody = new FormEncodingBuilder()//卢工接口10. 完成工程项目
                .add("cmd", "dofinishcons")
                .add("userno", userNo)
                .add("fileno", orderId)
                .add("receivables", "")
                .add("contentresult", contentresult)
                .add("drainsize", drainsize)
                .add("draintime", draintime)
                .add("begindate", begindate)
                .add("finishdate", finishdate)
                .add("acceptdate", acceptdate)
                .add("meterbodyno", meterbodyno)
                .add("meterproductionplace", meterproductionplace)
                .add("metereffectivedate", metereffectivedate)
                .add("readmeterdegree", readmeterdegree)
                .add("readmeterdate", readmeterdate)
                .add("representa", representa)
                .add("supervision", supervision)
                .add("supervisionname", supervisionname)
                .add("installname", installname)
                .add("earthworkname", earthworkname)
                .add("payunit", payunit)
                .add("remark4", remark4)
                .build();

        return requestBody;
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
