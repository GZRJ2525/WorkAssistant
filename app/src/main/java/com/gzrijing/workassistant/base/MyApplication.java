package com.gzrijing.workassistant.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.litepal.LitePalApplication;
import org.litepal.tablemanager.Connector;

public class MyApplication extends LitePalApplication {
    private static Context context;
    private static SQLiteDatabase db;
    private static ImageLoaderConfiguration configuration;
    public static int notificationId;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        if(db == null){
            db = Connector.getDatabase(); //创建数据表
        }

        if(configuration == null){
            //创建默认的ImageLoader配置参数
            configuration = ImageLoaderConfiguration
                    .createDefault(this);
        }
        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);

    }

    public static Context getContext() {
        return context;
    }
}
