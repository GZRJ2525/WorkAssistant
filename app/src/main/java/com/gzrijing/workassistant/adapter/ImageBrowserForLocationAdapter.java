package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.util.ImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageBrowserForLocationAdapter extends PagerAdapter{

    private String orderId;
    private String userNo;
    private Context context;
    private List<PicUrl> picUrls;
    private List<View> picViews;

    public ImageBrowserForLocationAdapter(Context context, List<PicUrl> picUrls, String userNo, String orderId){
        this.context = context;
        this.picUrls = picUrls;
        this.userNo = userNo;
        this.orderId = orderId;
        initViews();
    }

    private void initViews() {
        picViews = new ArrayList<View>();
        for(int i=0; i<picUrls.size(); i++) {
            // 填充显示图片的页面布局
            View view = View.inflate(context, R.layout.viewpage_item_image_browser, null);
            picViews.add(view);
        }
    }

    @Override
    public int getCount() {
        return picUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = picViews.get(position);
        ImageView mImageView = (ImageView) view.findViewById(R.id.viewpage_item_image_browser_image_iv);
        String picUrl = picUrls.get(position).getPicUrl();

        File path = ImageUtils.getImagePath(context, userNo, orderId);
        ImageUtils.getLocaImage(context, picUrl, mImageView, path);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
