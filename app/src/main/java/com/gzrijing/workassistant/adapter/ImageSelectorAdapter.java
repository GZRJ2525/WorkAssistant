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

public class ImageSelectorAdapter extends PagerAdapter{

    private Context context;
    private List<PicUrl> imageUrls;
    private List<View> picViews;

    public ImageSelectorAdapter(Context context, List<PicUrl> imageUrls){
        this.context = context;
        this.imageUrls = imageUrls;
        initViews();
    }

    private void initViews() {
        picViews = new ArrayList<View>();
        for(int i=0; i<imageUrls.size(); i++) {
            // 填充显示图片的页面布局
            View view = View.inflate(context, R.layout.viewpage_item_image_selector, null);
            picViews.add(view);
        }
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = picViews.get(position);
        ImageView image = (ImageView) view.findViewById(R.id.viewpage_item_image_selector_image_iv);
        final ImageView select = (ImageView) view.findViewById(R.id.viewpage_item_image_selector_select_iv);

        String imageUrl = imageUrls.get(position).getPicUrl();
        ImageUtils.getHttpImage(context, imageUrl, image);

        if(imageUrls.get(position).isCheck()){
            select.setImageResource(R.drawable.login_checkbox_on);
        }else{
            select.setImageResource(R.drawable.login_checkbox_off);
        }

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ischeck = !imageUrls.get(position).isCheck();
                imageUrls.get(position).setCheck(ischeck);
                if(ischeck){
                    select.setImageResource(R.drawable.login_checkbox_on);

                }else{
                    select.setImageResource(R.drawable.login_checkbox_off);
                }
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
