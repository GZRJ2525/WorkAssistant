package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.ImageSelectorAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.PicUrl;

import java.util.ArrayList;

public class ImageSelectorActivity extends BaseActivity {

    private int position;
    private ArrayList<PicUrl> imageUrls;
    private TextView tv_topNum;
    private ViewPager vp_image;
    private ImageSelectorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        Intent intent = getIntent();
        imageUrls = intent.getParcelableArrayListExtra("picUrls");
        Log.e("imageUrl", "ac---"+imageUrls.size());
    }

    private void initViews() {
        tv_topNum = (TextView) findViewById(R.id.image_selector_top_num_tv);
        if (imageUrls.size() > 0) {
            tv_topNum.setText(1 + " / " + imageUrls.size());
        }
        vp_image = (ViewPager) findViewById(R.id.image_selector_vp);
        adapter = new ImageSelectorAdapter(this, imageUrls);
        vp_image.setAdapter(adapter);
        vp_image.setCurrentItem(position);

    }

    private void setListeners() {
        vp_image.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int size = imageUrls.size();
                int index = position % size;
                tv_topNum.setText((index + 1) + " / " + size);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putParcelableArrayListExtra("picUrls", imageUrls);
        setResult(20, intent);
        super.onBackPressed();
    }
}
