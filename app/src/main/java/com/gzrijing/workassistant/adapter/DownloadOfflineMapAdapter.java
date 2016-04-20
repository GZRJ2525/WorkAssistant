package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.LocalMap;
import com.gzrijing.workassistant.widget.MyProgressBar;

import java.util.HashMap;
import java.util.List;

public class DownloadOfflineMapAdapter extends BaseAdapter {

    private HashMap<Integer, View> lmap = new HashMap<Integer, View>();
    private LayoutInflater listContainer;
    private List<LocalMap> localMaps;
    private final MKOfflineMap mOffline;

    public DownloadOfflineMapAdapter(Context context, List<LocalMap> localMaps, MKOfflineMap mOffline) {
        listContainer = LayoutInflater.from(context);
        this.localMaps = localMaps;
        this.mOffline = mOffline;
    }

    @Override
    public int getCount() {
        return localMaps.size();
    }

    @Override
    public Object getItem(int position) {
        return localMaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder v = null;
        if (lmap.get(position) == null) {
            v = new ViewHolder();
            convertView = listContainer.inflate(
                    R.layout.listview_item_download_offline_map, parent, false);
            v.cityName = (TextView) convertView.findViewById(R.id.download_offline_map_city_name_tv);
            v.size = (TextView) convertView.findViewById(R.id.download_offline_map_size_tv);
            v.download = (Button) convertView.findViewById(R.id.download_offline_map_downlond_btn);
            v.pause = (Button) convertView.findViewById(R.id.download_offline_map_pause_btn);
            v.delete = (Button) convertView.findViewById(R.id.download_offline_map_delete_btn);
            v.mProgressBar = (MyProgressBar) convertView.findViewById(R.id.download_offline_map_progressbar);
            convertView.setTag(v);
            lmap.put(position, convertView);
        } else {
            convertView = lmap.get(position);
            v = (ViewHolder) convertView.getTag();
        }
        final LocalMap city = (LocalMap) getItem(position);
        v.cityName.setText(city.getCityName());
        v.size.setText(formatDataSize(city.getSize()));
        if (city.getRatio() == 100) {
            v.download.setVisibility(View.GONE);
            v.pause.setVisibility(View.GONE);
            v.delete.setVisibility(View.VISIBLE);
            v.mProgressBar.setVisibility(View.GONE);
        } else if (city.getRatio() == 0) {
            v.download.setVisibility(View.VISIBLE);
            v.pause.setVisibility(View.GONE);
            v.delete.setVisibility(View.GONE);
        } else {
            v.mProgressBar.setVisibility(View.VISIBLE);
            v.mProgressBar.setProgress(city.getRatio());
        }

        final MyProgressBar myProgressBar = v.mProgressBar;
        final Button dlButton = v.download;
        final Button pButton = v.pause;
        final Button dButton = v.delete;
        v.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOffline.start(city.getCityId());
                myProgressBar.setVisibility(View.VISIBLE);
                dlButton.setVisibility(View.GONE);
                pButton.setVisibility(View.VISIBLE);
                dButton.setVisibility(View.GONE);
            }
        });
        v.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOffline.pause(city.getCityId());
                myProgressBar.setVisibility(View.VISIBLE);
                dlButton.setVisibility(View.VISIBLE);
                pButton.setVisibility(View.GONE);
                dButton.setVisibility(View.GONE);
            }
        });
        v.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOffline.remove(city.getCityId());
                myProgressBar.setVisibility(View.GONE);
                myProgressBar.setProgress(0);
                dlButton.setVisibility(View.VISIBLE);
                pButton.setVisibility(View.GONE);
                dButton.setVisibility(View.GONE);
            }
        });
        return convertView;
    }

    //离线包大小转换成M单位
    public String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    class ViewHolder {
        private TextView cityName;
        private TextView size;
        private Button download;
        private Button pause;
        private Button delete;
        private MyProgressBar mProgressBar;
    }
}
