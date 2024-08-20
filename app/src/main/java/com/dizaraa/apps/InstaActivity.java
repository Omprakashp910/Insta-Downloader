package com.dizaraa.apps;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.airbnb.lottie.LottieAnimationView;
import com.dizaraa.apps.utils.AdManager;
import com.dizaraa.apps.R;
import com.facebook.ads.NativeAdLayout;

import io.ak1.BubbleTabBar;

public class InstaActivity extends AppCompatActivity {
    LottieAnimationView instaBtn;

    public static ViewPager2 simpleViewPager;
    BubbleTabBar tabLayout;
    public static PagerAdapter adapter;

    AlertDialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta);

        simpleViewPager = findViewById(R.id.viewpager_1);
        tabLayout = findViewById(R.id.bubbleTabBar);
        adapter = new PagerAdapter(InstaActivity.this);
        simpleViewPager.setAdapter(adapter);

        simpleViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.setSelected(position, true);

            }
        });

        tabLayout.addBubbleListener(id -> {
            switch (id) {
                case R.id.home:
                    simpleViewPager.setCurrentItem(0);
                    break;


                case R.id.log:
                    simpleViewPager.setCurrentItem(1);
                    break;


                case R.id.doc:
                    simpleViewPager.setCurrentItem(2);
                    break;
            }
            ;
        });

        instaBtn = findViewById(R.id.instaBtn);
        instaBtn.setOnClickListener(v -> launchInstagram());

        LinearLayout adContainer = findViewById(R.id.banner_container);


        if (AdManager.adType.equals("admob")) {
            AdManager.initAd(InstaActivity.this);
            AdManager.loadBannerAd(InstaActivity.this, adContainer);
        } else if (AdManager.adType.equals("max")){
            AdManager.initMAX(InstaActivity.this);
            AdManager.maxBanner(this, adContainer);
        }else{
            AdManager.initFBAds(InstaActivity.this);
            AdManager.fbBanner(this, adContainer);
        }
        exitAlert();
    }

        public void launchInstagram () {
            String instagramApp = "com.instagram.android";
            try {
                Intent intent = getPackageManager().getLaunchIntentForPackage(instagramApp);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), R.string.instagram_not_found, Toast.LENGTH_SHORT).show();
            }
        }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        exitDialog.show();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void exitAlert() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(InstaActivity.this);
        View view = layoutInflaterAndroid.inflate(R.layout.dialogg_layouts, null);
        AlertDialog.Builder alert = null;
        alert = new AlertDialog.Builder(InstaActivity.this);
        alert.setView(view);
        exitDialog = alert.create();
        exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitDialog.setCancelable(false);

        LinearLayout adaptiveAdContainer =view.findViewById(R.id.banner_adaptive_container);
        RelativeLayout rl_native_ad =view.findViewById(R.id.rl_native_ad);

        if (AdManager.adType.equals("admob")) {
            AdManager.loadNativeAd(InstaActivity.this,adaptiveAdContainer);
        } else if (AdManager.adType.equals("max")){
            AdManager.createNativeAdMAX(InstaActivity.this,rl_native_ad);
        }else{
            NativeAdLayout nativeAdLayout = view.findViewById(R.id.native_ad_container);
            AdManager.loadNativeFbAdPopup( nativeAdLayout, this);
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
                InstaActivity.super.onBackPressed();
                return true;
            }
        });


    }
}

