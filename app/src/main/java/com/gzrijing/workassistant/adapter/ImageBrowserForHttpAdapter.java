package com.gzrijing.workassistant.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.entity.PicUrl;
import com.gzrijing.workassistant.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageBrowserForHttpAdapter extends PagerAdapter{

    private Context context;
    private List<PicUrl> picUrls;
    private List<View> picViews;

    public ImageBrowserForHttpAdapter(Context context, List<PicUrl> picUrls){
        this.context = context;
        this.picUrls = picUrls;
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

        ImageUtils.getHttpImage(context, picUrl, mImageView);
        //*使图片实现可以放大缩小的功能*
        PhotoViewAttacher mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.update();
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
