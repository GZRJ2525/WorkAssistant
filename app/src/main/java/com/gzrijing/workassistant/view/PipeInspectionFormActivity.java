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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.ReportProgressGriViewAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.service.PipeInspectionFormService;
import com.gzrijing.workassistant.util.ImageCompressUtil;
import com.gzrijing.workassistant.util.ImageUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PipeInspectionFormActivity extends BaseActivity implements View.OnClickListener {

    private String id;
    private String type;
    private ImageView iv_check1;
    private TextView tv_name1;
    private LinearLayout ll_remark1;
    private EditText et_remark1;
    private ImageView iv_check2;
    private TextView tv_name2;
    private LinearLayout ll_remark2;
    private EditText et_remark2;
    private ImageView iv_check3;
    private TextView tv_name3;
    private LinearLayout ll_remark3;
    private EditText et_remark3;
    private ImageView iv_check4;
    private TextView tv_name4;
    private LinearLayout ll_remark4;
    private EditText et_remark4;
    private ImageView iv_check5;
    private TextView tv_name5;
    private LinearLayout ll_remark5;
    private EditText et_remark5;
    private EditText et_problem;
    private Button btn_submit;
    private ArrayList<String> nameList = new ArrayList<String>();
    private ArrayList<String> checkList = new ArrayList<String>(); // 没问题为1 有问题为0
    private GridView gv_image;
    private ReportProgressGriViewAdapter adapter;
    private ArrayList<PicUrl> picUrls;
    private String userNo;
    private LinearLayout ll_item5;
    private ProgressDialog pDialog;
    private int position;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pipe_inspection_form);
        if (savedInstanceState != null) {
            ImageUtils.imageUriFromCamera = savedInstanceState.getParcelable("imageUriFromCamera");
            picUrls = savedInstanceState.getParcelableArrayList("picUrls");
        }else{
            picUrls = new ArrayList<PicUrl>();
            PicUrl picUrl = new PicUrl();
            picUrls.add(picUrl);
        }
        initData();
        initViews();
        setListeners();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("imageUriFromCamera", ImageUtils.imageUriFromCamera);
        outState.putParcelableArrayList("picUrls", picUrls);
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        type = intent.getStringExtra("type");
        position = intent.getIntExtra("position", -1);

        if (type.equals("0")) {
            String[] data = {"开关", "出水口", "外观", "出水压力", "阀门性能"};
            for (int i = 0; i < data.length; i++) {
                nameList.add(data[i]);
                checkList.add("1");
            }
        }

        if (type.equals("1")) {
            String[] data = {"井体", "井内杂物", "井盖", "阀门性能"};
            for (int i = 0; i < data.length; i++) {
                nameList.add(data[i]);
                checkList.add("1");
            }
        }

        if (type.equals("2")) {
            String[] data = {"井内批灰", "井内杂物", "井盖", "安全网"};
            for (int i = 0; i < data.length; i++) {
                nameList.add(data[i]);
                checkList.add("1");
            }
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.com.gzrijing.workassistant.PipeInspectionForm.uploadImage");
        intentFilter.addAction("action.com.gzrijing.workassistant.PipeInspectionForm.uploadData");
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(id);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iv_check1 = (ImageView) findViewById(R.id.pipe_inspection_form_standard_checkbox1_iv);
        tv_name1 = (TextView) findViewById(R.id.pipe_inspection_form_standard_name1_tv);
        tv_name1.setText(nameList.get(0));
        ll_remark1 = (LinearLayout) findViewById(R.id.pipe_inspection_form_standard_remark1_ll);
        et_remark1 = (EditText) findViewById(R.id.pipe_inspection_form_standard_remark1_et);

        iv_check2 = (ImageView) findViewById(R.id.pipe_inspection_form_standard_checkbox2_iv);
        tv_name2 = (TextView) findViewById(R.id.pipe_inspection_form_standard_name2_tv);
        tv_name2.setText(nameList.get(1));
        ll_remark2 = (LinearLayout) findViewById(R.id.pipe_inspection_form_standard_remark2_ll);
        et_remark2 = (EditText) findViewById(R.id.pipe_inspection_form_standard_remark2_et);

        iv_check3 = (ImageView) findViewById(R.id.pipe_inspection_form_standard_checkbox3_iv);
        tv_name3 = (TextView) findViewById(R.id.pipe_inspection_form_standard_name3_tv);
        tv_name3.setText(nameList.get(2));
        ll_remark3 = (LinearLayout) findViewById(R.id.pipe_inspection_form_standard_remark3_ll);
        et_remark3 = (EditText) findViewById(R.id.pipe_inspection_form_standard_remark3_et);

        iv_check4 = (ImageView) findViewById(R.id.pipe_inspection_form_standard_checkbox4_iv);
        tv_name4 = (TextView) findViewById(R.id.pipe_inspection_form_standard_name4_tv);
        tv_name4.setText(nameList.get(3));
        ll_remark4 = (LinearLayout) findViewById(R.id.pipe_inspection_form_standard_remark4_ll);
        et_remark4 = (EditText) findViewById(R.id.pipe_inspection_form_standard_remark4_et);

        iv_check5 = (ImageView) findViewById(R.id.pipe_inspection_form_standard_checkbox5_iv);
        tv_name5 = (TextView) findViewById(R.id.pipe_inspection_form_standard_name5_tv);
        ll_remark5 = (LinearLayout) findViewById(R.id.pipe_inspection_form_standard_remark5_ll);
        et_remark5 = (EditText) findViewById(R.id.pipe_inspection_form_standard_remark5_et);
        ll_item5 = (LinearLayout) findViewById(R.id.pipe_inspection_form_item5_ll);
        if (type.equals("0")) {
            tv_name5.setText(nameList.get(4));
            ll_item5.setVisibility(View.VISIBLE);
        }


        et_problem = (EditText) findViewById(R.id.pipe_inspection_form_problem_et);
        btn_submit = (Button) findViewById(R.id.pipe_inspection_form_submit_btn);

        gv_image = (GridView) findViewById(R.id.pipe_inspection_form_image_gv);
        adapter = new ReportProgressGriViewAdapter(this, picUrls);
        gv_image.setAdapter(adapter);

    }

    private void setListeners() {
        iv_check1.setOnClickListener(this);
        iv_check2.setOnClickListener(this);
        iv_check3.setOnClickListener(this);
        iv_check4.setOnClickListener(this);
        iv_check5.setOnClickListener(this);
        btn_submit.setOnClickListener(this);

        gv_image.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position + 1 == picUrls.size()) {
                    ImageUtils.showImagePickDialog(PipeInspectionFormActivity.this);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pipe_inspection_form_standard_checkbox1_iv:
                Log.e("0", checkList.get(0));
                if (checkList.get(0).equals("1")) {
                    iv_check1.setImageResource(R.drawable.login_checkbox_off);
                    checkList.set(0, "0");
                    ll_remark1.setVisibility(View.VISIBLE);
                } else {
                    iv_check1.setImageResource(R.drawable.login_checkbox_on);
                    checkList.set(0, "1");
                    et_remark1.setText("");
                    ll_remark1.setVisibility(View.GONE);
                }
                break;

            case R.id.pipe_inspection_form_standard_checkbox2_iv:
                if (checkList.get(1).equals("1")) {
                    iv_check2.setImageResource(R.drawable.login_checkbox_off);
                    checkList.set(1, "0");
                    ll_remark2.setVisibility(View.VISIBLE);
                } else {
                    iv_check2.setImageResource(R.drawable.login_checkbox_on);
                    checkList.set(1, "1");
                    et_remark2.setText("");
                    ll_remark2.setVisibility(View.GONE);
                }
                break;

            case R.id.pipe_inspection_form_standard_checkbox3_iv:
                if (checkList.get(2).equals("1")) {
                    iv_check3.setImageResource(R.drawable.login_checkbox_off);
                    checkList.set(2, "0");
                    ll_remark3.setVisibility(View.VISIBLE);
                } else {
                    iv_check3.setImageResource(R.drawable.login_checkbox_on);
                    checkList.set(2, "1");
                    et_remark3.setText("");
                    ll_remark3.setVisibility(View.GONE);
                }
                break;

            case R.id.pipe_inspection_form_standard_checkbox4_iv:
                if (checkList.get(3).equals("1")) {
                    iv_check4.setImageResource(R.drawable.login_checkbox_off);
                    checkList.set(3, "0");
                    ll_remark4.setVisibility(View.VISIBLE);
                } else {
                    iv_check4.setImageResource(R.drawable.login_checkbox_on);
                    checkList.set(3, "1");
                    et_remark4.setText("");
                    ll_remark4.setVisibility(View.GONE);
                }
                break;

            case R.id.pipe_inspection_form_standard_checkbox5_iv:
                if (checkList.get(4).equals("1")) {
                    iv_check5.setImageResource(R.drawable.login_checkbox_off);
                    checkList.set(4, "0");
                    ll_remark5.setVisibility(View.VISIBLE);
                } else {
                    iv_check5.setImageResource(R.drawable.login_checkbox_on);
                    checkList.set(4, "1");
                    et_remark5.setText("");
                    ll_remark5.setVisibility(View.GONE);
                }
                break;

            case R.id.pipe_inspection_form_submit_btn:
                if (picUrls.size() > 1) {

                    submitImage();
                } else {
                    ToastUtil.showToast(PipeInspectionFormActivity.this, "请添加附件再提交", Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    private void submitImage() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在提交");
        pDialog.show();
        count = 0;

        Intent intent = new Intent(this, PipeInspectionFormService.class);
        intent.putExtra("type", type);
        intent.putExtra("userNo", userNo);
        intent.putExtra("id", id);
        intent.putExtra("flag", "0");
        intent.putParcelableArrayListExtra("picUrls", picUrls);
        startService(intent);
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
                Log.e("imageUriFromCamera", ImageUtils.imageUriFromCamera.toString());
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
                Log.e("imageUri", imageUri.toString());

                String path1;
                if (imageUri.toString().split(":")[0].equals("content")) {
                    path1 = ImageUtils.getPicPath(this, imageUri);
                } else {
                    path1 = imageUri.toString().split(":")[1].substring(2);
                }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.com.gzrijing.workassistant.PipeInspectionForm.uploadImage")) {
                String result = intent.getStringExtra("result");
                if (result.equals("提交成功")) {
                    count++;
                    if (count == picUrls.size() - 1) {
                        submitData();
                    }
                } else {
                    pDialog.cancel();
                    ToastUtil.showToast(PipeInspectionFormActivity.this, result, Toast.LENGTH_SHORT);
                }
            }

            if (action.equals("action.com.gzrijing.workassistant.PipeInspectionForm.uploadData")) {
                String result = intent.getStringExtra("result");
                if (result.equals("提交成功")) {
                    ToastUtil.showToast(PipeInspectionFormActivity.this, result, Toast.LENGTH_SHORT);
                    Intent intent1 = new Intent("action.com.gzrijing.workassistant.PipeInspectMap.inspection");
                    intent1.putExtra("position", position);
                    sendBroadcast(intent1);
                    finish();
                } else {
                    ToastUtil.showToast(PipeInspectionFormActivity.this, result, Toast.LENGTH_SHORT);
                }
                pDialog.cancel();
            }
        }
    };

    private void submitData() {
        Intent intent = new Intent(this, PipeInspectionFormService.class);
        intent.putExtra("type", type);
        intent.putExtra("id", id);
        intent.putExtra("flag", "1");

        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Item1", checkList.get(0));
            jsonObject.put("Item2", checkList.get(1));
            jsonObject.put("Item3", checkList.get(2));
            jsonObject.put("Item4", checkList.get(3));
            jsonObject.put("ItemR1", et_remark1.getText().toString().trim());
            jsonObject.put("ItemR2", et_remark2.getText().toString().trim());
            jsonObject.put("ItemR3", et_remark3.getText().toString().trim());
            jsonObject.put("ItemR4", et_remark4.getText().toString().trim());
            if (type.equals("0")) {
                jsonObject.put("Item5", checkList.get(4));
                jsonObject.put("ItemR5", et_remark5.getText().toString().trim());
            }
            jsonObject.put("Remark", et_problem.getText().toString().trim());
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra("jsonData", jsonArray.toString());
        startService(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
