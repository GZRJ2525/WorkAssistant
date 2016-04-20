package com.gzrijing.workassistant.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.ReportProgressGriViewAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.service.UploadSafetyInspectImageService;
import com.gzrijing.workassistant.util.ImageCompressUtil;
import com.gzrijing.workassistant.util.ImageUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SafetyInspectUploadImageActivity extends BaseActivity {

    private GridView gv_image;
    private String orderId;
    private String userNo;
    private ArrayList<PicUrl> picUrls = new ArrayList<PicUrl>();
    private ReportProgressGriViewAdapter adapter;
    private ProgressDialog pDialog;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_inspect_upload_image);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");

        PicUrl picUrl = new PicUrl();
        picUrls.add(picUrl);

        IntentFilter intentFilter = new IntentFilter("action.com.gzrijing.workassistant.SafeInspectUploadImage");
        registerReceiver(mBroadcastReceiver, intentFilter);

    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gv_image = (GridView) findViewById(R.id.safety_inspect_upload_image_gv);
        adapter = new ReportProgressGriViewAdapter(this, picUrls);
        gv_image.setAdapter(adapter);
    }

    private void setListeners() {
        gv_image.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position + 1 == picUrls.size()) {
                    ImageUtils.showImagePickDialog(SafetyInspectUploadImageActivity.this);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_FROM_CAMERA:
                if (resultCode == RESULT_CANCELED) {
                    ImageUtils.deleteImageUri(this, ImageUtils.imageUriFromCamera);
                    return;
                }
                String path = ImageUtils.getPicPath(this, ImageUtils.imageUriFromCamera);
                Log.e("path", path);
                Bitmap bitmap = ImageCompressUtil.getimage(path);
                String fileName = path.substring(path.lastIndexOf("/") + 1);
                String filePathStr = path.substring(0, path.lastIndexOf("/"));
                File filePath = new File(filePathStr);
                try {
                    ImageUtils.saveFile(this, bitmap, fileName, filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PicUrl picUrl = new PicUrl();
                picUrl.setPicUrl(path);
                picUrls.add(picUrl);
                adapter.notifyDataSetChanged();
                break;

            case ImageUtils.REQUEST_CODE_FROM_ALBUM:
                if (resultCode == RESULT_CANCELED) {
                    return;
                }
                Uri imageUri = data.getData();
                String path1 = ImageUtils.getPicPath(this, imageUri);
                Log.e("path", path1);
                Bitmap bitmap1 = ImageCompressUtil.getimage(path1);
                String fileName1 = path1.substring(path1.lastIndexOf("/") + 1);
                String filePathStr1 = path1.substring(0, path1.lastIndexOf("/"));
                File filePath1 = new File(filePathStr1);
                try {
                    ImageUtils.saveFile(this, bitmap1, fileName1, filePath1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PicUrl picUrl1 = new PicUrl();
                picUrl1.setPicUrl(path1);
                picUrls.add(picUrl1);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_safety_inspect_upload_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_upload) {
            if (picUrls.size() > 1) {
                upload();
            } else {
                ToastUtil.showToast(SafetyInspectUploadImageActivity.this, "请添加图片后上传", Toast.LENGTH_SHORT);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void upload() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在上传图片");
        pDialog.show();
        count = 0;
        Intent intent = new Intent(SafetyInspectUploadImageActivity.this, UploadSafetyInspectImageService.class);
        intent.putExtra("orderId", orderId);
        intent.putExtra("userNo", userNo);
        intent.putParcelableArrayListExtra("picUrls", picUrls);
        startService(intent);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.com.gzrijing.workassistant.SafeInspectUploadImage")) {
                String result = intent.getStringExtra("result");
                if (result.equals("上传图片成功")) {
                    count++;
                    if (count == picUrls.size() - 1) {
                        pDialog.cancel();
                        ToastUtil.showToast(SafetyInspectUploadImageActivity.this, result, Toast.LENGTH_SHORT);
                    }
                } else {
                    pDialog.cancel();
                    ToastUtil.showToast(SafetyInspectUploadImageActivity.this, result, Toast.LENGTH_SHORT);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
