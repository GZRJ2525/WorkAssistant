package com.gzrijing.workassistant.util;

import android.graphics.Bitmap;

import com.gzrijing.workassistant.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class ImageOptHelper {

    public static DisplayImageOptions getImgOptions() {
        DisplayImageOptions imgOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.timeline_image_loading)
                .showImageForEmptyUri(R.drawable.timeline_image_loading)
                .showImageOnFail(R.drawable.timeline_image_failure)
                .build();
        return imgOptions;
    }
}
