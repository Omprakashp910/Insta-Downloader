package com.dizaraa.apps.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import com.dizaraa.apps.R;

import java.io.File;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class Utils {

    public static String instaDirPath = "ReelDownloader/";
    public static File downloadInstaDir = new File(Environment.getExternalStorageDirectory() + "/Download/ReelDownloader");



    public static String getBack(String paramString1, String paramString2) {
        Matcher localMatcher = Pattern.compile(paramString2).matcher(paramString1);
        if (localMatcher.find()) {
            return localMatcher.group(1);
        }
        return "";
    }


    public static void mShare(String filepath, Activity activity) {
        File fileToShare = new File(filepath);
        if (isImageFile(filepath)) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.setType("image/*");
            Uri photoURI = FileProvider.getUriForFile(
                    activity.getApplicationContext(), activity.getApplicationContext()
                            .getPackageName() + ".provider", fileToShare);
            share.putExtra(Intent.EXTRA_STREAM,
                    photoURI);
            activity.startActivity(Intent.createChooser(share, "Share via"));

        } else if (isVideoFile(filepath)) {

            Uri videoURI = FileProvider.getUriForFile(activity.getApplicationContext(), activity.getApplicationContext()
                    .getPackageName() + ".provider", fileToShare);
            Intent videoshare = new Intent(Intent.ACTION_SEND);
            videoshare.setType("*/*");
            videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            videoshare.putExtra(Intent.EXTRA_STREAM, videoURI);

            activity.startActivity(videoshare);
        }

    }

    public static void shareFile(Context context, boolean isVideo, String path) {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        if (isVideo)
            share.setType("Video/*");
        else
            share.setType("image/*");

        Uri uri;
        if (path.startsWith("content")) {
            uri = Uri.parse(path);
        } else {
            uri = FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName() + ".provider", new File(path));
        }

        share.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(share);
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void mediaScanner(Context context, String filePath, String fileName) {
        try {
            if (Build.VERSION.SDK_INT >= 19) {
                MediaScannerConnection.scanFile(context, new String[]{new File(DIRECTORY_DOWNLOADS + "/" + filePath + fileName).getAbsolutePath()},
                        null, new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {

//                                SharedPrefs.setPath(context, path);
//                                if (context instanceof InstaActivity && !((InstaActivity) context).isFinishing()) {
//                                    ((InstaActivity) context).loadImage();
//                                }
                            }
                        });
            } else {
                context.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.fromFile(new File(DIRECTORY_DOWNLOADS + "/" + filePath + fileName))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static AlertDialog alertDialog = null;

    public static void displayLoader(Context context) {
        if (alertDialog == null) {

            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
            View view = layoutInflaterAndroid.inflate(R.layout.dialog_loading, null);
            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setView(view);

            alertDialog = alert.create();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        }

    }

    public static void dismissLoader() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    public static AlertDialog alertDDialog = null;

    public static void displayDLoader(Context context) {
        if (alertDialog == null) {

            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
            View view = layoutInflaterAndroid.inflate(R.layout.dialog_downloading, null);
            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setView(view);

            alertDDialog = alert.create();
            alertDDialog.setCancelable(false);
            alertDDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDDialog.show();
        }

    }

    public static void dismissDLoader() {
        if (alertDDialog != null && alertDDialog.isShowing()) {
            alertDDialog.dismiss();
            alertDDialog = null;
        }
    }

    public static void downloader(Context context, String downloadURL, String path, String fileName) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "" + context.getString(R.string.dl_started), Toast.LENGTH_SHORT).show();
            }
        });

        String desc = context.getString(R.string.downloading);
//        String timeStamp = String.valueOf(System.currentTimeMillis());
//        String file = "tiktok_" + "_" + timeStamp;
//        String ext = "mp4";
//        String name = file + "." + ext;
        Uri Download_Uri = Uri.parse(downloadURL);


        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(true);
        request.setTitle(context.getString(R.string.app_name));
        request.setVisibleInDownloadsUi(true);
        request.setDescription(desc);
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, path + fileName);
        dm.enqueue(request);

        Utils.mediaScanner(context, path, fileName);
    }

    public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public static boolean isVideoFile(Context context, String path) {
        if (path.startsWith("content")) {
            DocumentFile fromTreeUri = DocumentFile.fromSingleUri(context, Uri.parse(path));
            String mimeType = fromTreeUri.getType();
            return mimeType != null && mimeType.startsWith("video");
        } else {
            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.startsWith("video");
        }
    }

    public static void maxNativeSize(Context context, RelativeLayout relativeLayout){
        FrameLayout.LayoutParams tabp = new FrameLayout.LayoutParams(dpToPixel(context, 300),
                dpToPixel(context, 250));
        relativeLayout.setLayoutParams(tabp);
    }

    public static int dpToPixel(Context context, int dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        return  (int) (dp * scale + 0.5f);
    }
}
