package com.dizaraa.apps;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dizaraa.apps.adapter.FullscreenImageAdapter;
import com.dizaraa.apps.model.DataModel;
import com.dizaraa.apps.R;
import com.dizaraa.apps.utils.AdManager;
import com.dizaraa.apps.utils.LayManager;
import com.dizaraa.apps.utils.Utils;
import com.facebook.ads.NativeAdLayout;

import java.io.File;
import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity {
    ViewPager viewPager;
    ArrayList<DataModel> imageList;
    int position;
    LinearLayout  menu_share, menu_delete;
    FullscreenImageAdapter fullscreenImageAdapter;
    String  statusdownload;
    ImageView backIV;
    LinearLayout bottomLay;
    TextView headerTxt;
    String folderPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        headerTxt = findViewById(R.id.headerTxt);

        backIV = findViewById(R.id.backIV);

        bottomLay = findViewById(R.id.bottomLay);
        LinearLayout.LayoutParams botmparam = LayManager.setLinParams(PreviewActivity.this, 1080, 307);
        bottomLay.setLayoutParams(botmparam);

        viewPager = findViewById(R.id.viewPager);

        menu_share = findViewById(R.id.menu_share);

        menu_delete = findViewById(R.id.menu_delete);

        LinearLayout.LayoutParams btnParam = LayManager.setLinParams(PreviewActivity.this, 300, 100);
        menu_share.setLayoutParams(btnParam);
        menu_delete.setLayoutParams(btnParam);

        imageList = getIntent().getParcelableArrayListExtra("images");
        position = getIntent().getIntExtra("position", 0);
        statusdownload = getIntent().getStringExtra("statusdownload");
        folderPath = getIntent().getStringExtra("folderpath");

        fullscreenImageAdapter = new FullscreenImageAdapter(PreviewActivity.this, imageList);
        viewPager.setAdapter(fullscreenImageAdapter);
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (AdManager.adType.equals("admob")) {
                    AdManager.adCounter++;
                    AdManager.showInterAd(PreviewActivity.this, null);
                } else if (AdManager.adType.equals("max")) {
                    AdManager.adCounter++;
                    AdManager.showMaxInterstitial(PreviewActivity.this, null);
                }else{
                    AdManager.adCounter++;
                    AdManager.showFBInterstitial(PreviewActivity.this, null);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        menu_share.setOnClickListener(clickListener);
        menu_delete.setOnClickListener(clickListener);
        backIV.setOnClickListener(clickListener);

        LinearLayout adContainer = findViewById(R.id.banner_container);
        if (AdManager.adType.equals("admob")) {
            //admob
            AdManager.initAd(PreviewActivity.this);
            AdManager.loadBannerAd(PreviewActivity.this, adContainer);
            AdManager.loadInterAd(PreviewActivity.this);
        } else if (AdManager.adType.equals("max")){
            //MAX + Fb banner Ads
            AdManager.initMAX(PreviewActivity.this);
            AdManager.maxBanner(PreviewActivity.this, adContainer);
            AdManager.maxInterstital(PreviewActivity.this);
        }else{
            AdManager.fbBanner(PreviewActivity.this, adContainer);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.backIV:
                    onBackPressed();
                    break;

                case R.id.menu_share:
                    if (imageList.size() > 0) {
                        Utils.shareFile(PreviewActivity.this, Utils.isVideoFile(PreviewActivity.this, imageList.get(viewPager.getCurrentItem()).getFilePath()),imageList.get(viewPager.getCurrentItem()).getFilePath());
                    } else {
                        finish();
                    }
                    break;

                case R.id.menu_delete:
                    if (imageList.size() > 0) {

                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(PreviewActivity.this);
                        View view = layoutInflaterAndroid.inflate(R.layout.delete_pop_up, null);
                        android.app.AlertDialog.Builder alert = null;
                        alert = new android.app.AlertDialog.Builder(PreviewActivity.this);
                        alert.setView(view);
                        AlertDialog exitDialog = alert.create();
                        exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        exitDialog.setCancelable(false);

                        LinearLayout adaptiveAdContainer =view.findViewById(R.id.banner_adaptive_container);
                        RelativeLayout rl_native_ad =view.findViewById(R.id.rl_native_ad);

                        if (AdManager.adType.equals("admob")) {
                            AdManager.loadNativeAd(PreviewActivity.this,adaptiveAdContainer);
                        } else if (AdManager.adType.equals("max")){
                            AdManager.createNativeAdMAX(PreviewActivity.this,rl_native_ad);
                        }else{
                            NativeAdLayout nativeAdLayout = view.findViewById(R.id.native_ad_container);
                            AdManager.loadNativeFbAdPopup( nativeAdLayout, PreviewActivity.this);
                        }

                        TextView skip = view.findViewById(R.id.skip);
                        TextView yes = view.findViewById(R.id.yes);

                        skip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                exitDialog.dismiss();
                            }
                        });
                        yes.setOnTouchListener(new View.OnTouchListener() {
                            @SuppressLint("ClickableViewAccessibility")
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                exitDialog.dismiss();
                                int currentItem = 0;
//
                                if (statusdownload.equals("download")) {
                                    File file = new File(imageList.get(viewPager.getCurrentItem()).getFilePath());
                                    if (file.exists()) {
                                        boolean del = file.delete();
                                        delete(currentItem);
                                    }
                                }else {
                                    DocumentFile fromTreeUri = DocumentFile.fromSingleUri(PreviewActivity.this, Uri.parse(imageList.get(viewPager.getCurrentItem()).getFilePath()));
                                    if (fromTreeUri.exists()) {
                                        boolean del = fromTreeUri.delete();
                                        delete(currentItem);
                                    }
                                }

                                AdManager.adCounter=4;
                                if (AdManager.adType.equals("admob")) {
                                    AdManager.showInterAd(PreviewActivity.this, null);
                                } else if (AdManager.adType.equals("max")){
                                    AdManager.showMaxInterstitial(PreviewActivity.this, null);
                                }else{
                                    AdManager.showFBInterstitial(PreviewActivity.this, null);
                                }
                                return true;
                            }
                        });
                        exitDialog.show();

//                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PreviewActivity.this);
//                        alertDialog.setTitle("Delete");
//                        alertDialog.setMessage("Sure to Delete this Image?");
//                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                int currentItem = 0;
//
//                                if (statusdownload.equals("download")) {
//                                    File file = new File(imageList.get(viewPager.getCurrentItem()).getFilePath());
//                                    if (file.exists()) {
//                                        boolean del = file.delete();
//                                        delete(currentItem);
//                                    }
//                                }else {
//                                    DocumentFile fromTreeUri = DocumentFile.fromSingleUri(PreviewActivity.this, Uri.parse(imageList.get(viewPager.getCurrentItem()).getFilePath()));
//                                    if (fromTreeUri.exists()) {
//                                        boolean del = fromTreeUri.delete();
//                                        delete(currentItem);
//                                    }
//                                }
//
//                                AdManager.adCounter=4;
//                                if (AdManager.adType.equals("admob")) {
//                                    AdManager.showInterAd(PreviewActivity.this, null);
//                                } else if (AdManager.adType.equals("max")){
//                                    AdManager.showMaxInterstitial(PreviewActivity.this, null);
//                                }else{
//                                    AdManager.showFBInterstitial(PreviewActivity.this, null);
//                                }
//                            }
//                        });
//                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//                        alertDialog.show();

                    } else {
                        finish();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    void delete(int currentItem){
        if (imageList.size() > 0 && viewPager.getCurrentItem() < imageList.size()) {
            currentItem = viewPager.getCurrentItem();
        }
        imageList.remove(viewPager.getCurrentItem());
        fullscreenImageAdapter = new FullscreenImageAdapter(PreviewActivity.this, imageList);
        viewPager.setAdapter(fullscreenImageAdapter);

        Intent intent = new Intent();
        setResult(10, intent);

        if (imageList.size() > 0) {
            viewPager.setCurrentItem(currentItem);
        } else {
            finish();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
