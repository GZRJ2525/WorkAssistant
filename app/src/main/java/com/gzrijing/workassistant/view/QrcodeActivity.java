package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ImageUtils;
import com.gzrijing.workassistant.util.ToastUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

public class QrcodeActivity extends BaseActivity {

    private ImageView iv_qrcode;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        initViews();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        String billNo = intent.getStringExtra("billNo");
        String flag = intent.getStringExtra("flag");
        RequestBody requestBody = null;
        if(flag.equals("send")){
            String machineNo = intent.getStringExtra("machineNo");
            requestBody = new FormEncodingBuilder()
                    .add("cmd", "makeqrcode")
                    .add("detail", billNo + "," + machineNo)
                    .build();

        }
        if(flag.equals("return")){
            requestBody = new FormEncodingBuilder()
                    .add("cmd", "makeqrcode")
                    .add("detail", billNo)
                    .build();
        }

        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ImageUtils.getHttpImage(QrcodeActivity.this, response, iv_qrcode);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(QrcodeActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                    }
                });
            }
        });

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iv_qrcode = (ImageView) findViewById(R.id.qrcode_iv);
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
