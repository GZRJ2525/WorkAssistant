package com.gzrijing.workassistant.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.DetailedInfo;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.util.VoiceUtil;
import com.gzrijing.workassistant.view.ImageBrowserForLocationActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DetailedInfoAdapter extends BaseAdapter {

    private String orderId;
    private String userNo;
    private Context context;
    private LayoutInflater listContainer;
    private List<DetailedInfo> list;
    private ArrayList<PicUrl> picUrls;
    private String recordFileName;
    private MediaPlayer player;
    private ProgressDialog pDialog;

    public DetailedInfoAdapter(Context context, List<DetailedInfo> list, ArrayList<PicUrl> picUrls, String recordFileName, String userNo, String orderId) {
        this.context = context;
        listContainer = LayoutInflater.from(context);//【
        this.list = list;
        this.picUrls = picUrls;
        this.recordFileName = recordFileName;
        this.userNo = userNo;
        this.orderId = orderId;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (picUrls.size() > 0 && position + 1 == list.size()) {
            convertView = listContainer.inflate(
                    R.layout.listview_item_gridview_image, parent, false);
            GridView gv_image = (GridView) convertView.findViewById(R.id.listview_item_gridview_image_gv);//自定义布局listview_item_gridview_image_gv
            GridViewImageAdapter adapter = new GridViewImageAdapter(context, picUrls, userNo, orderId);
            gv_image.setAdapter(adapter);
            gv_image.setOnItemClickListener(new AdapterView.OnItemClickListener() {//点击某个图片项
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (picUrls.size() > 0) {
                        Intent intent = new Intent(context, ImageBrowserForLocationActivity.class);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("position", position);
                        intent.putParcelableArrayListExtra("picUrls", picUrls);
                        context.startActivity(intent);
                    }
                }
            });
        } else {
            convertView = listContainer.inflate(
                    R.layout.listview_item_detailed_info, parent, false);
            TextView key = (TextView) convertView.findViewById(R.id.listview_item_detailed_info_key_tv);
            TextView value = (TextView) convertView.findViewById(R.id.listview_item_detailed_info_value_tv);
            key.setText(list.get(position).getKey());
            value.setText(list.get(position).getValue());
            if (recordFileName != null && !recordFileName.equals("")) {
                if (picUrls.size() > 0 && position + 2 == list.size()) {
                    value.setTextColor(convertView.getResources().getColor(R.color.blue));
                    value.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File recordDir = VoiceUtil.getVoicePath(context, userNo, orderId);
                            File recordFile = new File(recordDir, recordFileName);
                            player = MediaPlayer.create(context, Uri.parse(recordFile.getPath()));
                            player.start();
                            pDialog = new ProgressDialog(context);
                            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pDialog.setMessage("正在播放录音...");
                            pDialog.setCancelable(false);
                            pDialog.show();
                            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mp.release();
                                    pDialog.dismiss();
                                }
                            });
                        }
                    });
                }

                if (picUrls.size() == 0 && position + 1 == list.size()) {
                    value.setTextColor(convertView.getResources().getColor(R.color.blue));
                    value.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File recordDir = VoiceUtil.getVoicePath(context, userNo, orderId);
                            File recordFile = new File(recordDir, recordFileName);
                            player = MediaPlayer.create(context, Uri.parse(recordFile.getPath()));
                            player.start();
                            pDialog = new ProgressDialog(context);
                            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pDialog.setMessage("正在播放录音...");
                            pDialog.setCancelable(false);
                            pDialog.show();
                            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mp.release();
                                    pDialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        }
        return convertView;
    }
}
