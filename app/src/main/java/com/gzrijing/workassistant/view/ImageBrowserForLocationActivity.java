package com.gzrijing.workassistant.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.gzrijing.workassistant.R;
import com.gzrijing.workassistant.adapter.ImageBrowserForLocationAdapter;
import com.gzrijing.workassistant.base.BaseActivity;
import com.gzrijing.workassistant.entity.PicUrl;

import java.util.List;

public class ImageBrowserForLocationActivity extends BaseActivity {

    private TextView tv_topNum;
    private ViewPager vp_image;
    private int position;
    private List<PicUrl> picUrls;
    private ImageBrowserForLocationAdapter adapter;
    private String userNo;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browser);

        initData();
        initViews();
        setListeners();
    }

    private void initData() {
        SharedPreferences app = getSharedPreferences(
                "saveUser", MODE_PRIVATE);
        userNo = app.getString("userNo", "");

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        position = intent.getIntExtra("position", -1);
        picUrls = intent.getParcelableArrayListExtra("picUrls");
    }

    private void initViews() {
        tv_topNum = (TextView) findViewById(R.id.image_browser_top_num_tv);
        if (picUrls.size() > 0) {
            tv_topNum.setText(1 + " / " + picUrls.size());
        }
        vp_image = (ViewPager) findViewById(R.id.image_browser_vp);
        adapter = new ImageBrowserForLocationAdapter(this, picUrls, userNo, orderId);
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
                int size = picUrls.size();
                int index = position % size;
                tv_topNum.setText((index+1) + " / " + size);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
