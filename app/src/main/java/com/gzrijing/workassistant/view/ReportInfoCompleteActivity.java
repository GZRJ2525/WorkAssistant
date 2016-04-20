package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.GridViewImageForReportInfoAdapter;
import com.gzrijing.workassistant.adapter.PrintInfoAdapter;
import com.gzrijing.workassistant.adapter.SuppliesApplyingAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.Acceptance;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ReportInfoCompleteActivity extends BaseActivity {

    private String orderId;
    private ArrayList<Acceptance> infos = new ArrayList<Acceptance>();
    private ArrayList<PicUrl> picUrls = new ArrayList<PicUrl>();
    private String userNo;
    private Handler handler = new Handler();
    private ListView lv_info;
    private ImageLoader mImageLoader;
    private ListView lv_suppliesClient;
    private ListView lv_suppliesWater;
    private PrintInfoAdapter detailedAdapter;
    private SuppliesApplyingAdapter clientAdapter;
    private SuppliesApplyingAdapter waterAdapter;
    private GridView gv_image;
    private GridViewImageForReportInfoAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info_complete);

        initViews();
        initData();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        mImageLoader = ImageLoader.getInstance();

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        getCompleteInfo();
        getCompletePicUrl();
    }

    private void getCompletePicUrl() {
        String url = null;
        try {
            url = "?cmd=getconspic&fileno=" + URLEncoder.encode(orderId, "UTF-8") + "&relationid=&pictype=WnW_ConsFinishPic";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
                    @Override
                    public void onFinish(final String response) {
                        Log.e("response", response);
                        handler.post(new Runnable() {
                                         @Override
                                         public void run() {
                                             ArrayList<PicUrl> picUrlList = JsonParseUtils.getReportCompletePicUrl(response);
                                             picUrls.addAll(picUrlList);
                                             imageAdapter = new GridViewImageForReportInfoAdapter(ReportInfoCompleteActivity.this, picUrls);
                                             gv_image.setAdapter(imageAdapter);
                                         }
                                     }
                        );

                    }

                    @Override
                    public void onError(Exception e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(ReportInfoCompleteActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                            }
                        });
                    }
                }

        );
    }

    private void getCompleteInfo() {
        String url = null;
        try {
            url = "?cmd=getfinishconstruction&userno=" + URLEncoder.encode(userNo, "UTF-8") +
                    "&fileno=" + URLEncoder.encode(orderId, "UTF-8") + "&enddate=&isfinish=1";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Acceptance> list = JsonParseUtils.getReportCompleteInfo(response);
                        infos.addAll(list);
                        if (!infos.toString().equals("[]")) {
                            detailedAdapter = new PrintInfoAdapter(ReportInfoCompleteActivity.this, infos.get(0).getDetailedInfos());
                            clientAdapter = new SuppliesApplyingAdapter(ReportInfoCompleteActivity.this, infos.get(0).getSuppliesByClient());
                            waterAdapter = new SuppliesApplyingAdapter(ReportInfoCompleteActivity.this, infos.get(0).getSuppliesByWater());
                            lv_info.setAdapter(detailedAdapter);
                            lv_suppliesClient.setAdapter(clientAdapter);
                            lv_suppliesWater.setAdapter(waterAdapter);
                        }
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(ReportInfoCompleteActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_info = (ListView) findViewById(R.id.report_info_complete_info_lv);
        lv_suppliesClient = (ListView) findViewById(R.id.report_info_complete_client_supplies_lv);
        lv_suppliesWater = (ListView) findViewById(R.id.report_info_complete_water_supplies_lv);

        gv_image = (GridView) findViewById(R.id.report_info_complete_image_gv);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
