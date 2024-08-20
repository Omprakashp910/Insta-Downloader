package com.dizaraa.apps.fragment;

import static android.content.Context.CLIPBOARD_SERVICE;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dizaraa.apps.PreviewActivity;
import com.dizaraa.apps.R;
import com.dizaraa.apps.model.DataModel;
import com.dizaraa.apps.utils.AdManager;
import com.dizaraa.apps.utils.InstaDownload;
import com.dizaraa.apps.utils.SharedPrefs;
import com.dizaraa.apps.utils.Utils;
import com.facebook.ads.NativeAdLayout;

import java.io.File;
import java.util.ArrayList;


public class HomeFrag extends Fragment {
    ImageView lastDownload, clearTxt, play,link;
    CardView recentLay;
    EditText linkEdt;
    TextView downloadBtn, pasteBtn,downtxt;
    private ClipboardManager clipBoard;

    String[] permissionsList = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    String[] permissionsList33 = new String[]{Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.fragment_home, container, false);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !checkPermissions(getActivity(), permissionsList33)) {
            ActivityCompat.requestPermissions(getActivity(), permissionsList33, 211);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && !checkPermissions(getActivity(), permissionsList)) {
            ActivityCompat.requestPermissions(getActivity(), permissionsList, 21);
        }


        link=vi.findViewById(R.id.link);
        linkEdt = vi.findViewById(R.id.linkEdt);
        downtxt=vi.findViewById(R.id.downtxt);
        pasteBtn = vi.findViewById(R.id.pasteBtn);
        pasteBtn.setOnClickListener(v -> {
            pasteUrl();
        });

        clearTxt = vi.findViewById(R.id.clearTxt);
        clearTxt.setOnClickListener(v -> {
            linkEdt.setText("");
        });

        downloadBtn =vi.findViewById(R.id.downloadBtn);
        downloadBtn.setOnClickListener(view -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !checkPermissions(getActivity(), permissionsList33)) {
                ActivityCompat.requestPermissions(getActivity(), permissionsList33, 211);
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && !checkPermissions(getActivity(), permissionsList)) {
                ActivityCompat.requestPermissions(getActivity(), permissionsList, 21);
            } else {
                if (Utils.isNetworkAvailable(getActivity())) {
                    if (linkEdt.getText().toString().trim().length() == 0) {
                        Toast.makeText(getActivity(), "Please paste url and download!!!!", Toast.LENGTH_SHORT).show();
                    } else {
                        final String url = linkEdt.getText().toString();

                        if (!Patterns.WEB_URL.matcher(url).matches() && !url.contains("instagram")) {
                            Toast.makeText(getActivity(), R.string.invalid, Toast.LENGTH_SHORT).show();
                        } else {
                            InstaDownload.INSTANCE.startInstaDownload(url, getActivity());
                            linkEdt.getText().clear();
                            clipBoard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                            ClipData data = ClipData.newPlainText("", "");
                            clipBoard.setPrimaryClip(data);
                        }

                        if (AdManager.adType.equals("admob")) {
                            AdManager.adCounter++;
                            AdManager.showInterAd(getActivity(), null);
                        } else if (AdManager.adType.equals("max")){
                            AdManager.adCounter++;
                            AdManager.showMaxInterstitial(getActivity(), null);
                        }else{
                            AdManager.adCounter++;
                            AdManager.showFBInterstitial(getActivity(), null);
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Internet Connection not available!!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        lastDownload = vi.findViewById(R.id.lastDownload);
        lastDownload.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PreviewActivity.class);
            File file = new File(SharedPrefs.getPath(getActivity()));
            ArrayList<DataModel> media = new ArrayList<>();
            media.add(new DataModel(file.getAbsolutePath(), file.getName()));
            intent.putParcelableArrayListExtra("images", media);
            intent.putExtra("position", 0);
            intent.putExtra("statusdownload", "download");

            if (AdManager.adType.equals("admob")) {
                AdManager.adCounter++;
                AdManager.showInterAd(getActivity(), intent);
            } else if (AdManager.adType.equals("max")){
                AdManager.adCounter++;
                AdManager.showMaxInterstitial(getActivity(), intent);
            }else{
                AdManager.adCounter++;
                AdManager.showFBInterstitial(getActivity(), intent);
            }

        });

        recentLay =vi.findViewById(R.id.recentLay);
        play =vi.findViewById(R.id.play);
        new LoadRecent().execute();

        LinearLayout adaptiveAdContainer =vi.findViewById(R.id.banner_adaptive_container);
        RelativeLayout rl_native_ad =vi.findViewById(R.id.rl_native_ad);

        if (AdManager.adType.equals("admob")) {
            AdManager.initAd(getActivity());
            AdManager.loadNativeAd(getActivity(), adaptiveAdContainer);
        } else if (AdManager.adType.equals("max")){
            AdManager.initMAX(getActivity());
            AdManager.createNativeAdMAX(getActivity(), rl_native_ad);
        }else{
            AdManager.loadNativeFbad(getActivity());
//            NativeAdLayout nativeAdLayout = vi.findViewById(R.id.native_ad_container);
//            AdManager.loadNativeFbAdPopup( nativeAdLayout, getActivity());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter("DOWNLOADED"), Context.RECEIVER_EXPORTED);
        }
        return vi;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new LoadRecent().execute();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
       getActivity().unregisterReceiver(broadcastReceiver);
    }

    public void pasteUrl() {
        clipBoard = (ClipboardManager)  getActivity().getSystemService(CLIPBOARD_SERVICE);
        Log.e("pasteUrl: ", "");
        try {
            linkEdt.setText("");
            ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
            linkEdt.setText(item.getText().toString());
            Utils.dismissLoader();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.dismissLoader();
        }
    }

    public static boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 21) {
            if (!checkPermissions(getActivity(), permissionsList)) {
                ActivityCompat.requestPermissions(getActivity(), permissionsList, 21);
            }
        }
        if (requestCode == 211) {
            if (!checkPermissions(getActivity(), permissionsList33)) {
                ActivityCompat.requestPermissions(getActivity(), permissionsList33, 211);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getLatestFilefromDir(Utils.downloadInstaDir)==null){
            recentLay.setVisibility(View.GONE);
            downtxt.setVisibility(View.GONE);
        }
            Utils.displayLoader(getActivity());
        new Handler().postDelayed(() -> pasteUrl(), 500);
    }

    private class LoadRecent extends AsyncTask<Void, Void, Void> {
        String path = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.displayLoader(getActivity());
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if(getLatestFilefromDir(Utils.downloadInstaDir)!=null) {
                path = getLatestFilefromDir(Utils.downloadInstaDir).getAbsolutePath();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!path.equals("")) {
                recentLay.setVisibility(View.VISIBLE);
                downtxt.setVisibility(View.VISIBLE);
                Glide.get(getActivity()).clearMemory();
                Glide.with(getActivity()).applyDefaultRequestOptions(RequestOptions.skipMemoryCacheOf(true)).
                        load(path).into(lastDownload);
                SharedPrefs.setPath(getActivity(), path);

                if (!Utils.getBack(path, "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)").isEmpty()) {
                    play.setVisibility(View.VISIBLE);
                } else{
                    play.setVisibility(View.GONE);
                }
            }else {
                recentLay.setVisibility(View.GONE);
                downtxt.setVisibility(View.GONE);
            }
            Utils.dismissLoader();
        }
    }

    private File getLatestFilefromDir(File dir){
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }
        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile;
    }
}