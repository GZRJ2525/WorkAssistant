package com.gzrijing.workassistant.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.DistributeGriViewAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.db.BusinessData;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.entity.Subordinate;
import com.gzrijing.workassistant.listener.HttpCallbackListener;
import com.gzrijing.workassistant.service.DownLoadAllImageService;
import com.gzrijing.workassistant.util.HttpUtils;
import com.gzrijing.workassistant.util.ImageUtils;
import com.gzrijing.workassistant.util.JsonParseUtils;
import com.gzrijing.workassistant.util.JudgeDate;
import com.gzrijing.workassistant.util.ToastUtil;
import com.gzrijing.workassistant.util.VoiceUtil;
import com.gzrijing.workassistant.widget.selectdate.ScreenInfo;
import com.gzrijing.workassistant.widget.selectdate.WheelMain;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DistributeActivity extends BaseActivity implements View.OnClickListener {

    private String userNo;
    private String orderId;
    private EditText et_remarks;
    private TextView tv_executor;
    private LinearLayout ll_executor;
    private TextView tv_deadline;
    private LinearLayout ll_deadline;
    private ArrayList<Subordinate> subordinates = new ArrayList<Subordinate>();
    private WheelMain wheelMain;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private GridView gv_image;
    private ArrayList<PicUrl> picUrls = new ArrayList<PicUrl>(); //选中的图片
    private ArrayList<PicUrl> imageUrls = new ArrayList<PicUrl>(); //这个工程的所有图片
    private DistributeGriViewAdapter adapter;
    private Intent imageIntent;
    private TextView tv_record;
    private TextView tv_delRecord;
    private File recordFile;
    private Handler handler = new Handler();
    private ProgressDialog pDialog;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribute);

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

        IntentFilter mIntentFilter = new IntentFilter("action.com.gzrijing.workassistant.Distribute");
        registerReceiver(mBroadcastReceiver, mIntentFilter);

        initImageUrl();

    }

    private void initImageUrl() {
        imageIntent = new Intent(this, DownLoadAllImageService.class);
        imageIntent.putExtra("orderId", orderId);
        startService(imageIntent);
    }


    private void initViews() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_remarks = (EditText) findViewById(R.id.distribute_remarks_et);
        tv_executor = (TextView) findViewById(R.id.distribute_executor_tv);
        ll_executor = (LinearLayout) findViewById(R.id.distribute_executor_ll);
        tv_deadline = (TextView) findViewById(R.id.distribute_deadline_tv);
        ll_deadline = (LinearLayout) findViewById(R.id.distribute_deadline_ll);
        tv_record = (TextView) findViewById(R.id.distribute_record_tv);
        tv_delRecord = (TextView) findViewById(R.id.distribute_del_record_tv);

        gv_image = (GridView) findViewById(R.id.distribute_image_gv);
    }

    private void setListeners() {
        ll_executor.setOnClickListener(this);
        ll_deadline.setOnClickListener(this);
        tv_record.setOnClickListener(this);
        tv_delRecord.setOnClickListener(this);

        gv_image.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position + 1 == picUrls.size()) {
                    Intent intent = new Intent(DistributeActivity.this, ImageSelectorActivity.class);
                    intent.putParcelableArrayListExtra("picUrls", imageUrls);
                    startActivityForResult(intent, 20);
                } else {

                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.distribute_executor_ll:
                Intent intent = new Intent(this, SubordinateActivity.class);
                intent.putExtra("flag", "派工");
                intent.putExtra("orderId", orderId);
                intent.putParcelableArrayListExtra("subordinates", subordinates);
                startActivityForResult(intent, 10);
                break;

            case R.id.distribute_deadline_ll:
                getDeadline();
                break;

            case R.id.distribute_record_tv:
                getRecord();
                break;

            case R.id.distribute_del_record_tv:
                delRecord();
                break;
        }
    }

    private void delRecord() {
        recordFile.delete();
        recordFile = null;
        tv_delRecord.setVisibility(View.GONE);
        tv_record.setText("");
        tv_record.setHint("点击添加");
    }

    private void getRecord() {
        if (tv_record.getText().toString().equals("")) {
            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            startActivityForResult(intent, 30);
        } else {
            player = MediaPlayer.create(this, Uri.parse(recordFile.getPath()));
            player.start();
            pDialog = new ProgressDialog(this);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("正在播放录音...");
            pDialog.show();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    if(pDialog != null){
                        pDialog.dismiss();
                    }
                }
            });
        }
    }

    private void getDeadline() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View timepickerview = inflater.inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(this);
        wheelMain = new WheelMain(timepickerview, true);
        wheelMain.screenheight = screenInfo.getHeight();
        String time = tv_deadline.getText().toString();
        Calendar calendar = Calendar.getInstance();
        if (JudgeDate.isDate(time, "yyyy-MM-dd HH:mm")) {
            try {
                calendar.setTime(dateFormat.parse(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        int h = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        wheelMain.initDateTimePicker(y, m - 1, d, h, min);
        new AlertDialog.Builder(this)
                .setTitle("选择时间")
                .setView(timepickerview)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_deadline.setText(wheelMain.getTime());
                        tv_deadline.setTextColor(getResources().getColor(
                                R.color.black));
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == 10) {
                String executors = data.getStringExtra("executors");
                subordinates = data.getParcelableArrayListExtra("subordinates");
                tv_executor.setText(executors);
            }
        }

        if (requestCode == 20) {
            if (resultCode == 20) {
                imageUrls.clear();
                picUrls.clear();
                ArrayList<PicUrl> imageUrlList = data.getParcelableArrayListExtra("picUrls");
                imageUrls.addAll(imageUrlList);
                for (PicUrl picUrl : imageUrls) {
                    if (picUrl.isCheck()) {
                        picUrls.add(picUrl);
                    }
                }
                PicUrl picUrl = new PicUrl();
                picUrls.add(picUrl);
                Log.e("pic", picUrls.size() + "");
                Log.e("image", imageUrls.size() + "");
                adapter.notifyDataSetChanged();
            }
        }

        if (requestCode == 30) {
            if (resultCode == RESULT_OK) {
                Uri recordUri = data.getData();
                Log.e("recordUri", recordUri.toString());
                String path;
                if (recordUri.toString().split(":")[0].equals("content")) {
                    path = VoiceUtil.getMediaStoreVideoPath(this, recordUri);
                } else {
                    path = recordUri.toString().split(":")[1].substring(2);
                }
                String fileName = path.substring(path.lastIndexOf("/") + 1);
                Log.e("path", path);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sdf.format(new Date(System.currentTimeMillis()));
                tv_record.setText(time + "." + fileName.split("\\.")[1]);
                recordFile = new File(path);
                tv_delRecord.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_distribute, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_distribute) {
            if (tv_executor.getText().toString().equals("")) {
                ToastUtil.showToast(this, "请选择施工员", Toast.LENGTH_SHORT);
                return true;
            }

            if (tv_deadline.getText().toString().equals("")) {
                ToastUtil.showToast(this, "请选择工程期限", Toast.LENGTH_SHORT);
                return true;
            }
            distribute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void distribute() {
        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在提交数据...");
        pDialog.show();
        StringBuilder sb = new StringBuilder();
        for (Subordinate sub : subordinates) {
            if (sub.isCheck()) {
                sb.append(sub.getUserNo() + ",");
            }
        }
        String executors = sb.toString().substring(0, sb.toString().length() - 1);
        Log.e("executors", executors);


        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < picUrls.size() - 1; i++) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("PicUri", picUrls.get(i).getPicUrl());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.e("json", jsonArray.toString());

        String jsonData = "";
        if (!jsonArray.toString().equals("[]")) {
            jsonData = jsonArray.toString();
        }
        final RequestBody requestBody = new FormEncodingBuilder()
                .add("cmd", "doappoint")
                .add("userno", userNo)
                .add("fileno", orderId)
                .add("installuserno", executors)
                .add("installcontent", et_remarks.getText().toString().trim())
                .add("estimatefinishdate", tv_deadline.getText().toString())
                .add("picuri", jsonData)
                .build();
        HttpUtils.sendHttpPostRequest(requestBody, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("response", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.substring(0, 1).equals("E")) {
                            ToastUtil.showToast(DistributeActivity.this, "派工失败", Toast.LENGTH_SHORT);
                            pDialog.dismiss();
                        } else {
                            if (recordFile == null) {
                                saveInfo(response);
                            } else {
                                uploadRecord(response);
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(DistributeActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                });
            }
        });
    }

    private void uploadRecord(String id) {
        String[] key = {"cmd", "userno", "fileno", "togetherid"};
        String[] value = {"uploadappointinstallsound", userNo, orderId, id};

        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        for (int i = 0; i < key.length; i++) {
            builder.addFormDataPart(key[i], value[i]);
        }

        String fileName = recordFile.getName();
        RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), recordFile);
        builder.addFormDataPart("", fileName, fileBody);

        HttpUtils.sendHttpPostRequest(builder.build(), new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.e("responseVoice", response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.substring(0, 1).equals("E")) {
                            ToastUtil.showToast(DistributeActivity.this, "上传录音失败", Toast.LENGTH_SHORT);
                            pDialog.dismiss();
                        } else {
                            recordFile.delete();
                            recordFile = null;
                            saveInfo(response);
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(DistributeActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                });
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

    private void saveInfo(String id) {
        if (pDialog != null) {
            pDialog.dismiss();
        }
        ContentValues values = new ContentValues();
        values.put("state", "已派工");
        DataSupport.updateAll(BusinessData.class, values, "user = ? and orderId = ?", userNo, orderId);
        ToastUtil.showToast(DistributeActivity.this, "派工成功", Toast.LENGTH_SHORT);

        Intent intent = new Intent("action.com.gzrijing.workassistant.LeaderFragment.Distribute");
        intent.putExtra("orderId", orderId);
        sendBroadcast(intent);
        finish();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("action.com.gzrijing.workassistant.Distribute")) {
                String response = intent.getStringExtra("response");
                imageUrls = JsonParseUtils.getImageUrl(response);
                adapter = new DistributeGriViewAdapter(DistributeActivity.this, picUrls, imageUrls);
                gv_image.setAdapter(adapter);
            }
        }
    };

    @Override
    protected void onDestroy() {
        stopService(imageIntent);
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
