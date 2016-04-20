package com.gzrijing.workassistant.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

public class GridViewImageAdapter extends BaseAdapter {

    private String orderId;
    private String userNo;
    private ImageLoader imageLoader;
    private Context context;
    private LayoutInflater listContainer;
    private ArrayList<PicUrl> picUrls;

    public GridViewImageAdapter(Context context, ArrayList<PicUrl> picUrls, String userNo, String orderId) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.picUrls = picUrls;
        imageLoader = ImageLoader.getInstance();
        this.userNo = userNo;
        this.orderId = orderId;
    }

    @Override
    public int getCount() {
        return picUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return picUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder v = null;
        if (convertView == null) {
            v = new ViewHolder();
            convertView = listContainer.inflate(
                    R.layout.gridview_item_image, parent, false);
            v.image = (ImageView) convertView.findViewById(R.id.gridview_item_image_image_iv);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }
        GridView gv = (GridView) parent;
        int horizontalSpacing = gv.getHorizontalSpacing();
        int width = (gv.getWidth() - horizontalSpacing * 2
                - gv.getPaddingLeft() - gv.getPaddingRight()) / 3;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
        v.image.setLayoutParams(params);
        File path = ImageUtils.getImagePath(context, userNo, orderId);
        ImageUtils.getLocaImage(context, picUrls.get(position).getPicUrl(), v.image, path);
        return convertView;
    }

    public static class ViewHolder {
        public ImageView image;
    }
}
