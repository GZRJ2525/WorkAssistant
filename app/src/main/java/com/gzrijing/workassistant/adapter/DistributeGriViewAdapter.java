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

import java.util.ArrayList;
import java.util.List;

public class DistributeGriViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater listContainer;
    private List<PicUrl> picUrls;
    private ArrayList<PicUrl> imageUrls;

    public DistributeGriViewAdapter(Context context, List<PicUrl> picUrls, ArrayList<PicUrl> imageUrls) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.picUrls = picUrls;
        this.imageUrls = imageUrls;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder v = null;
        if (convertView == null) {
            v = new ViewHolder();
            convertView = listContainer.inflate(
                    R.layout.gridview_item_image, parent, false);
            v.image = (ImageView) convertView.findViewById(R.id.gridview_item_image_image_iv);
            v.delete = (ImageView) convertView.findViewById(R.id.gridview_item_image_delete_iv);
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

        if (picUrls.size() == 1) {
            v.image.setImageResource(R.drawable.upload_photo);
            v.delete.setVisibility(View.GONE);
            return convertView;
        }

        if (picUrls.size() > 1 && position + 1 == picUrls.size()) {
            v.image.setImageResource(R.drawable.upload_photo);
            v.delete.setVisibility(View.GONE);
        } else {
            ImageUtils.getHttpImage(context, picUrls.get(position).getPicUrl(), v.image);

            v.delete.setVisibility(View.VISIBLE);
            v.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < imageUrls.size(); i++) {
                        if (picUrls.get(position).getPicUrl().equals(imageUrls.get(i).getPicUrl())) {
                            imageUrls.get(i).setCheck(false);
                        }
                    }
                    picUrls.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
        return convertView;
    }

    public static class ViewHolder {
        public ImageView image;
        public ImageView delete;
    }

}
