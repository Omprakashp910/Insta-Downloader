package com.dizaraa.apps;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DownloadBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            //Show a notification
            Toast.makeText(context, "Download Finished. Check Download Folder.", Toast.LENGTH_SHORT).show();
            context.sendBroadcast(new Intent("DOWNLOADED"));
        }
    }
}
