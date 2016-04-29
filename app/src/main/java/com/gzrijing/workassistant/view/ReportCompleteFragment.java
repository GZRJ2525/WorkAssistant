package com.gzrijing.workassistant.view;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.ReportProgressGriViewAdapter;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.entity.ReportComplete;
import com.gzrijing.workassistant.service.ReportCompleteService;
import com.gzrijing.workassistant.util.ImageCompressUtil;
import com.gzrijing.workassistant.util.ImageUtils;
import com.gzrijing.workassistant.util.JudgeDate;
import com.gzrijing.workassistant.util.ToastUtil;
import com.gzrijing.workassistant.widget.selectdate.ScreenInfo;
import com.gzrijing.workassistant.widget.selectdate.WheelMain;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ReportCompleteFragment extends Fragment implements View.OnClickListener {

    private View layoutView;
    private String orderId;
    private String type;
    private ArrayList<ReportComplete> infos = new ArrayList<ReportComplete>();
    private WheelMain wheelMain;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private LinearLayout ll_item1;
    private TextView tv_key1;
    private EditText et_value1;
    private LinearLayout ll_item2;
    private TextView tv_key2;
    private EditText et_value2;
    private LinearLayout ll_item3;
    private TextView tv_key3;
    private EditText et_value3;
    private LinearLayout ll_item4;
    private TextView tv_key4;
    private EditText et_value4;
    private LinearLayout ll_item5;
    private TextView tv_key5;
    private EditText et_value5;
    private LinearLayout ll_item6;
    private TextView tv_key6;
    private EditText et_value6;
    private LinearLayout ll_item8;
    private TextView tv_key8;
    private EditText et_value8;
    private LinearLayout ll_item9;
    private TextView tv_key9;
    private EditText et_value9;
    private LinearLayout ll_item10;
    private TextView tv_key10;
    private EditText et_value10;
    private LinearLayout ll_item11;
    private TextView tv_key11;
    private EditText et_value11;
    private LinearLayout ll_item12;
    private TextView tv_key12;
    private EditText et_value12;
    private LinearLayout ll_item14;
    private TextView tv_key14;
    private TextView tv_value14;
    private LinearLayout ll_item15;
    private TextView tv_key15;
    private TextView tv_value15;
    private LinearLayout ll_item16;
    private TextView tv_key16;
    private TextView tv_value16;
    private LinearLayout ll_item17;
    private TextView tv_key17;
    private TextView tv_value17;
    private LinearLayout ll_item18;
    private TextView tv_key18;
    private TextView tv_value18;
    private LinearLayout ll_item19;
    private TextView tv_key19;
    private EditText et_value19;
    private Button btn_report;
    private ArrayList<PicUrl> picUrls;
    private GridView gv_image;
    private ReportProgressGriViewAdapter adapter;
    private Uri imageUri;

    public ReportCompleteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            imageUri = savedInstanceState.getParcelable("imageUri");
            picUrls = savedInstanceState.getParcelableArrayList("picUrls");
        }else{
            picUrls = new ArrayList<PicUrl>();
            PicUrl picUrl = new PicUrl();
            picUrls.add(picUrl);
        }
        initData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("imageUri", imageUri);
        outState.putParcelableArrayList("picUrls", picUrls);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_report_complete, container, false);

        initViews();
        setListeners();
        return layoutView;
    }

    private void initData() {
        orderId = getArguments().getString("orderId");
        type = getArguments().getString("type");
        String[] key = new String[0];
        if(type.equals("供水维修")){
            key = getResources().getStringArray(R.array.reportComplete_form1);
        }
        if(type.equals("供水管网维护")){
            key = getResources().getStringArray(R.array.reportComplete_form2);
        }
        if(type.equals("挂表工程")){
            key = getResources().getStringArray(R.array.reportComplete_form3);
        }
        if(type.equals("移表户外")){
            key = getResources().getStringArray(R.array.reportComplete_form4);
        }
        if(type.equals("故障水表")){
            key = getResources().getStringArray(R.array.reportComplete_form5);
        }
        if(type.equals("违章水表")){
            key = getResources().getStringArray(R.array.reportComplete_form6);
        }
        if(type.equals("供水工程") || type.equals("供水零散工程") || type.equals("供水维修通知单工程")){
            key = getResources().getStringArray(R.array.reportComplete_form7);
        }
        if(type.equals("污水工程") || type.equals("污水零散工程")){
            key = getResources().getStringArray(R.array.reportComplete_form8);
        }
        if(type.equals("污水维修工程")){
            key = getResources().getStringArray(R.array.reportComplete_form9);
        }
        if(type.equals("其他工程")){
            key = getResources().getStringArray(R.array.reportComplete_form10);
        }

        for (int i = 0; i < key.length; i++) {
            ReportComplete info = new ReportComplete();
            info.setKey(key[i]);
            infos.add(info);
        }

        IntentFilter mIntentFilter = new IntentFilter("action.com.gzrijing.workassistant.ReportComplete");
        getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);

    }

    private void initViews() {
        ll_item1 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item1_ll);
        tv_key1 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item1_key_tv);
        et_value1 = (EditText) layoutView.findViewById(R.id.fragment_report_complete_item1_value_et);

        ll_item2 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item2_ll);
        tv_key2 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item2_key_tv);
        et_value2 = (EditText) layoutView.findViewById(R.id.fragment_report_complete_item2_value_et);

        ll_item3 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item3_ll);
        tv_key3 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item3_key_tv);
        et_value3 = (EditText) layoutView.findViewById(R.id.fragment_report_complete_item3_value_et);

        ll_item4 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item4_ll);
        tv_key4 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item4_key_tv);
        et_value4 = (EditText) layoutView.findViewById(R.id.fragment_report_complete_item4_value_et);

        ll_item5 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item5_ll);
        tv_key5 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item5_key_tv);
        et_value5 = (EditText) layoutView.findViewById(R.id.fragment_report_complete_item5_value_et);

        ll_item6 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item6_ll);
        tv_key6 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item6_key_tv);
        et_value6 = (EditText) layoutView.findViewById(R.id.fragment_report_complete_item6_value_et);

        ll_item8 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item8_ll);
        tv_key8 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item8_key_tv);
        et_value8 = (EditText) layoutView.findViewById(R.id.fragment_report_complete_item8_value_et);

        ll_item9 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item9_ll);
        tv_key9 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item9_key_tv);
        et_value9 = (EditText) layoutView.findViewById(R.id.fragment_report_complete_item9_value_et);

        ll_item10 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item10_ll);
        tv_key10 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item10_key_tv);
        et_value10 = (EditText) layoutView.findViewById(R.id.fragment_report_complete_item10_value_et);

        ll_item11 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item11_ll);
        tv_key11 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item11_key_tv);
        et_value11 = (EditText) layoutView.findViewById(R.id.fragment_report_complete_item11_value_et);

        ll_item12 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item12_ll);
        tv_key12 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item12_key_tv);
        et_value12 = (EditText) layoutView.findViewById(R.id.fragment_report_complete_item12_value_et);

        ll_item14 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item14_ll);
        tv_key14 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item14_key_tv);
        tv_value14 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item14_value_tv);

        ll_item15 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item15_ll);
        tv_key15 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item15_key_tv);
        tv_value15 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item15_value_tv);

        ll_item16 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item16_ll);
        tv_key16 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item16_key_tv);
        tv_value16 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item16_value_tv);

        ll_item17 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item17_ll);
        tv_key17 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item17_key_tv);
        tv_value17 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item17_value_tv);

        ll_item18 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item18_ll);
        tv_key18 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item18_key_tv);
        tv_value18 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item18_value_tv);

        ll_item19 = (LinearLayout) layoutView.findViewById(R.id.fragment_report_complete_item19_ll);
        tv_key19 = (TextView) layoutView.findViewById(R.id.fragment_report_complete_item19_key_tv);
        et_value19 = (EditText) layoutView.findViewById(R.id.fragment_report_complete_item19_value_et);

        btn_report = (Button) layoutView.findViewById(R.id.fragment_report_complete_report_btn);

        gv_image = (GridView) layoutView.findViewById(R.id.fragment_report_complete_image_gv);
        adapter = new ReportProgressGriViewAdapter(getActivity(), picUrls);
        gv_image.setAdapter(adapter);

        replay();
    }

    private void replay() {
        for(ReportComplete info : infos){
            if(info.getKey().equals("维修内容")){
                ll_item1.setVisibility(View.VISIBLE);
            }
            if(info.getKey().equals("施工内容")){
                ll_item2.setVisibility(View.VISIBLE);
            }
            if(info.getKey().equals("处理结果")){
                ll_item3.setVisibility(View.VISIBLE);
            }
            if(info.getKey().equals("表身编号")){
                ll_item4.setVisibility(View.VISIBLE);
            }
            if(info.getKey().equals("水表产地")){
                ll_item5.setVisibility(View.VISIBLE);
            }
            if(info.getKey().equals("行至度数")){
                ll_item6.setVisibility(View.VISIBLE);
            }
            if(info.getKey().equals("甲方代表")){
                ll_item8.setVisibility(View.VISIBLE);
            }
            if(info.getKey().equals("监理单位")){
                ll_item9.setVisibility(View.VISIBLE);
            }
            if(info.getKey().equals("　监理员")){
                ll_item10.setVisibility(View.VISIBLE);
            }
            if(info.getKey().equals("安装人员")){
                ll_item11.setVisibility(View.VISIBLE);
            }
            if(info.getKey().equals("土方人员")){
                ll_item12.setVisibility(View.VISIBLE);
            }
            if(info.getKey().equals("抄表日期")){
                ll_item14.setVisibility(View.VISIBLE);
            }
            if(info.getKey().equals("水表有效日期")){
                ll_item15.setVisibility(View.VISIBLE);
            }
            if(info.getKey().equals("施工日期")){
                ll_item16.setVisibility(View.VISIBLE);
            }
            if(info.getKey().equals("完工日期")){
                ll_item17.setVisibility(View.VISIBLE);
            }
            if(info.getKey().equals("验收日期")){
                ll_item18.setVisibility(View.VISIBLE);
            }
            if(info.getKey().equals("　备　注")){
                ll_item19.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setListeners() {
        tv_value14.setOnClickListener(this);
        tv_value15.setOnClickListener(this);
        tv_value16.setOnClickListener(this);
        tv_value17.setOnClickListener(this);
        tv_value18.setOnClickListener(this);

        btn_report.setOnClickListener(this);

        gv_image.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position + 1 == picUrls.size()) {
                    showImagePickDialog();
                }
            }
        });

    }

    /**
     * 显示获取照片不同方式对话框
     */
    private void showImagePickDialog() {
        String title = "选择获取图片方式";
        String[] items = new String[]{"拍照", "相册"};
        new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                pickImageFromCamera();
                                break;
                            case 1:
                                pickImageFromAlbum();
                                break;
                        }
                    }
                })
                .show();
    }

    /**
     * 打开本地相册选取图片
     */
    private void pickImageFromAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, ImageUtils.REQUEST_CODE_FROM_ALBUM);
    }

    /**
     * 打开相机拍照获取图片
     */
    private void pickImageFromCamera() {
        imageUri = ImageUtils.createImageUri(getActivity());

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, ImageUtils.REQUEST_CODE_FROM_CAMERA);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_report_complete_item14_value_tv:
                getDate(tv_value14);
                break;

            case R.id.fragment_report_complete_item15_value_tv:
                getDate(tv_value15);
                break;

            case R.id.fragment_report_complete_item16_value_tv:
                getDate(tv_value16);
                break;

            case R.id.fragment_report_complete_item17_value_tv:
                getDate(tv_value17);
                break;

            case R.id.fragment_report_complete_item18_value_tv:
                getDate(tv_value18);
                break;

            case R.id.fragment_report_complete_report_btn:
                report();
                break;
        }
    }

    private void report() {
        for(ReportComplete info : infos){
            if(info.getKey().equals("维修内容")){
                info.setValue(et_value1.getText().toString().trim());
            }
            if(info.getKey().equals("施工内容")){
                info.setValue(et_value2.getText().toString().trim());
            }
            if(info.getKey().equals("处理结果")){
                info.setValue(et_value3.getText().toString().trim());
            }
            if(info.getKey().equals("表身编号")){
                info.setValue(et_value4.getText().toString().trim());
            }
            if(info.getKey().equals("水表产地")){
                info.setValue(et_value5.getText().toString().trim());
            }
            if(info.getKey().equals("行至度数")){
                info.setValue(et_value6.getText().toString().trim());
            }
            if(info.getKey().equals("甲方代表")){
                info.setValue(et_value8.getText().toString().trim());
            }
            if(info.getKey().equals("监理单位")){
                info.setValue(et_value9.getText().toString().trim());
            }
            if(info.getKey().equals("　监理员")){
                info.setValue(et_value10.getText().toString().trim());
            }
            if(info.getKey().equals("安装人员")){
                info.setValue(et_value11.getText().toString().trim());
            }
            if(info.getKey().equals("土方人员")){
                info.setValue(et_value12.getText().toString().trim());
            }
            if(info.getKey().equals("抄表日期")){
                info.setValue(tv_value14.getText().toString());
            }
            if(info.getKey().equals("水表有效日期")){
                info.setValue(tv_value15.getText().toString());
            }
            if(info.getKey().equals("施工日期")){
                info.setValue(tv_value16.getText().toString());
            }
            if(info.getKey().equals("完工日期")){
                info.setValue(tv_value17.getText().toString());
            }
            if(info.getKey().equals("验收日期")){
                info.setValue(tv_value18.getText().toString());
            }
            if(info.getKey().equals("　备　注")){
                info.setValue(et_value19.getText().toString().trim());
            }
        }

        btn_report.setVisibility(View.GONE);

        Intent intent = new Intent(getActivity(), ReportCompleteService.class);
        intent.putParcelableArrayListExtra("picUrls", picUrls);
        intent.putExtra("orderId", orderId);
        intent.putParcelableArrayListExtra("infos", infos);
        getActivity().startService(intent);
    }

    private void getDate(final TextView tv_value) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View timepickerview = inflater.inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(getActivity());
        wheelMain = new WheelMain(timepickerview, true);
        wheelMain.screenheight = screenInfo.getHeight();
        String time = tv_value.getText().toString();
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
        new AlertDialog.Builder(getActivity())
                .setTitle("选择时间")
                .setView(timepickerview)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_value.setText(wheelMain.getTime());
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_FROM_CAMERA:
                if (resultCode == getActivity().RESULT_CANCELED) {
                    ImageUtils.deleteImageUri(getActivity(), imageUri);
                    return;
                }
                String path = ImageUtils.getPicPath(getActivity(), imageUri);
                Log.e("path", path);
                Bitmap bitmap = ImageCompressUtil.getimage(path);
                String fileName = path.substring(path.lastIndexOf("/") + 1);
                String filePathStr = path.substring(0, path.lastIndexOf("/"));
                File filePath = new File(filePathStr);
                try {
                    ImageUtils.saveFile(getActivity(), bitmap, fileName, filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PicUrl picUrl = new PicUrl();
                picUrl.setPicUrl(path);
                picUrls.add(picUrl);
                adapter.notifyDataSetChanged();
                break;

            case ImageUtils.REQUEST_CODE_FROM_ALBUM:
                if (resultCode == getActivity().RESULT_CANCELED) {
                    return;
                }
                Uri imageUri = data.getData();
                Log.e("imageUri", imageUri.toString());

                String path1;
                if (imageUri.toString().split(":")[0].equals("content")) {
                    path1 = ImageUtils.getPicPath(getActivity(), imageUri);
                } else {
                    path1 = imageUri.toString().split(":")[1].substring(2);
                }
                Log.e("path", path1);
                Bitmap bitmap1 = ImageCompressUtil.getimage(path1);
                String fileName1 = path1.substring(path1.lastIndexOf("/") + 1);
                String filePathStr1 = path1.substring(0, path1.lastIndexOf("/"));
                File filePath1 = new File(filePathStr1);
                try {
                    ImageUtils.saveFile(getActivity(), bitmap1, fileName1, filePath1);
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

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("action.com.gzrijing.workassistant.ReportComplete")){
                String result = intent.getStringExtra("result");
                if(result.equals("汇报成功")){
                    ToastUtil.showToast(context, "汇报成功", Toast.LENGTH_SHORT);
                    getActivity().finish();
                }
                if(result.equals("汇报失败")){
                    String response = intent.getStringExtra("response");
                    ToastUtil.showToast(context, response, Toast.LENGTH_LONG);
                }
                if(result.equals("与服务器断开连接")){
                    ToastUtil.showToast(context, "与服务器断开连接", Toast.LENGTH_SHORT);
                }
                btn_report.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
