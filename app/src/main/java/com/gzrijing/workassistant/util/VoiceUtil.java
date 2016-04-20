package com.gzrijing.workassistant.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;

/**
 * 声音工具
 */
public class VoiceUtil {

    public static File getVoicePath(Context context, String userNo, String orderId){
        // 未安装SD卡时不做保存
        String storageState = Environment.getExternalStorageState();
        if (!storageState.equals(Environment.MEDIA_MOUNTED)) {
            ToastUtil.showToast(context, "未检测到SD卡", Toast.LENGTH_SHORT);
            return null;
        }

        // 声音文件保存路径
        File storageDirectory = Environment.getExternalStorageDirectory();
        File path = new File(storageDirectory, "/GZRJWorkassistant/voice/"+userNo+"/"+orderId);
        // 声音路径不存在创建之
        if (!path.exists()) {
            path.mkdirs();
        }

        return path;
    }

    /**
     * 获取音频路径
     * @param uri
     * @return
     */
    public static String getMediaStoreVideoPath(Context context, Uri uri) {
        ContentResolver mResolver = context.getContentResolver();
        String[] proj = {MediaStore.Video.Media.DATA};
        Cursor cursor = mResolver.query(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        return path;
    }

}
