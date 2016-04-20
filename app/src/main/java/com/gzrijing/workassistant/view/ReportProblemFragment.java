package com.gzrijing.workassistant.view;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.ReportProgressGriViewAdapter;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.service.ReportProblemService;
import com.gzrijing.workassistant.util.ImageCompressUtil;
import com.gzrijing.workassistant.util.ImageUtils;
import com.gzrijing.workassistant.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ReportProblemFragment extends Fragment implements View.OnClickListener{

    private String orderId;
    private View layoutView;
    private EditText et_describe;
    private Button btn_report;
    private String userNo;
    private GridView gv_image;
    private ArrayList<PicUrl> picUrls;
    private ReportProgressGriViewAdapter adapter;
    private Uri imageUri;

    public ReportProblemFragment() {

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
        layoutView = inflater.inflate(R.layout.fragment_report_problem, container, false);

        initViews();
        setListeners();

        return layoutView;
    }

    private void initData() {
        SharedPreferences app = getActivity().getSharedPreferences(
                "saveUser", getActivity().MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        Bundle bundle = getArguments();
        orderId = bundle.getString("orderId");

        IntentFilter intentFilter = new IntentFilter("action.com.gzrijing.workassistant.reportProblemFragment");
        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void initViews() {
        et_describe = (EditText) layoutView.findViewById(R.id.fragment_report_problem_describe_et);
        btn_report = (Button) layoutView.findViewById(R.id.fragment_report_problem_report_btn);

        gv_image = (GridView) layoutView.findViewById(R.id.fragment_report_problem_image_gv);
        adapter = new ReportProgressGriViewAdapter(getActivity(), picUrls);
        gv_image.setAdapter(adapter);
    }

    private void setListeners() {
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
        new AlertDialog.Builder(getActivity())
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("uri", "ok");
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

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
      case R.id.fragment_report_problem_report_btn:
          btn_report.setVisibility(View.GONE);
          String content = et_describe.getText().toString().trim();

          Intent intent = new Intent(getActivity(), ReportProblemService.class);
          intent.putParcelableArrayListExtra("picUrls", picUrls);
          intent.putExtra("userNo", userNo);
          intent.putExtra("orderId", orderId);
          intent.putExtra("content", content);
          getActivity().startService(intent);
          break;
      }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("action.com.gzrijing.workassistant.reportProblemFragment")){
                btn_report.setVisibility(View.VISIBLE);
                ToastUtil.showToast(getActivity(), "汇报成功", Toast.LENGTH_SHORT);
            }
        }
    };

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

}
