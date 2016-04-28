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

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageBrowserForLocationAdapter extends PagerAdapter {

    private String orderId;
    private String userNo;
    private Context context;
    private List<PicUrl> picUrls;
    private List<View> picViews;

    public ImageBrowserForLocationAdapter(Context context, List<PicUrl> picUrls, String userNo, String orderId) {
        this.context = context;
        this.picUrls = picUrls;
        this.userNo = userNo;
        this.orderId = orderId;
        initViews();
    }

    private void initViews() {
        picViews = new ArrayList<View>();
        for (int i = 0; i < picUrls.size(); i++) {
            // 填充显示图片的页面布局
            View view = View.inflate(context, R.layout.viewpage_item_image_browser, null);//view 为imageview
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
        ImageView mImageView = (ImageView) view.findViewById(R.id.viewpage_item_image_browser_image_iv);//存放图片的控件
        String picUrl = picUrls.get(position).getPicUrl();//图片的url

        File path = ImageUtils.getImagePath(context, userNo, orderId);//图片工具 获得图片存放路径
        ImageUtils.getLocaImage(context, picUrl, mImageView, path);//图片工具 打开本地图片
        //*使图片实现可以放大缩小的功能*
        PhotoViewAttacher mAttacher = new PhotoViewAttacher(mImageView);//
        mAttacher.update();//

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
