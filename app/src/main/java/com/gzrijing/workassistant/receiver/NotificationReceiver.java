package com.gzrijing.workassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gzrijing.workassistant.util.ActivityManagerUtil;
import com.gzrijing.workassistant.view.MainActivity;

public class NotificationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        if (ActivityManagerUtil.activities.size() != 0) {
            Log.e("tag", "NotificationReceiver_true");
            ActivityManagerUtil.finishAll();
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainIntent.putExtra("fragId", "0");
            context.startActivity(mainIntent);
        } else {
            Log.e("tag", "NotificationReceiver_false");
            Intent launchIntent = context.getPackageManager().
                    getLaunchIntentForPackage("com.gzrijing.workassistant");
            context.startActivity(launchIntent);
        }
    }
}
