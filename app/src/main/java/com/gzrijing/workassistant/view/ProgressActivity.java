package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.ProgressAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.Progress;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.DateUtil;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ProgressActivity extends BaseActivity {

    private String orderId;
    private ListView lv_progress;
    private List<Progress> proInfos = new ArrayList<Progress>();
    private ProgressAdapter adapter;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        initData();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");

        getData();
    }

    private void getData() {
        String url = null;
        try {
            url = "?cmd=getconstask&fileno=" + URLEncoder.encode(orderId, "UTF-8") + "&savedate=";
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
                        List<Progress> list = JsonParseUtils.getProgress(response);
                        Collections.sort(list, new Comparator<Progress>() {
                            @Override
                            public int compare(Progress lhs, Progress rhs) {
                                Date date1 = DateUtil.stringToDate(lhs.getTime());
                                Date date2 = DateUtil.stringToDate(rhs.getTime());
                                // 对日期字段进行升序，如果欲降序可采用after方法
                                if (date1.before(date2)) {
                                    return 1;
                                }
                                return -1;

                            }
                        });
                        proInfos.clear();
                        proInfos.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(ProgressActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(orderId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_progress = (ListView) findViewById(R.id.progress_lv);
        adapter = new ProgressAdapter(this, proInfos);
        lv_progress.setAdapter(adapter);

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
