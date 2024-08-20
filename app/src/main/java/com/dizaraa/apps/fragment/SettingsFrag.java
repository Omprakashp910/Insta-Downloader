package com.dizaraa.apps.fragment;

import static android.content.Context.MODE_PRIVATE;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.NativeAdLayout;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.dizaraa.apps.PrivacyActivity;
import com.dizaraa.apps.R;
import com.dizaraa.apps.utils.AdManager;

public class SettingsFrag extends Fragment implements View.OnClickListener{
    RelativeLayout moreapp, policy, shareapp, rateapp;
    SwitchCompat mode;
    SharedPreferences sharedPreferences_two;
    TextView folderpath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.fragment_setting, container, false);
        mode=vi.findViewById(R.id.modeSwitch);
        SharedPreferences sharepref_two = getActivity().getSharedPreferences("mypref", MODE_PRIVATE);
        String file = sharepref_two.getString("file", "");
        folderpath=vi.findViewById(R.id.folderpath);
        folderpath.setText(file);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_pref", MODE_PRIVATE);
        boolean onn = sharedPreferences.getBoolean("on",false);

        if (onn)
        {
            mode.setChecked(true);
        }else {
            mode.setChecked(false);
        }



        sharedPreferences_two = getActivity().getSharedPreferences("my_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences_two.edit();


        mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                    if (isChecked) {
                        editor.putBoolean("on",true).apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    else {
                        editor.putBoolean("on",false).apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
            }
        });

        moreapp =vi.findViewById(R.id.moreapp);
        moreapp.setOnClickListener(this);
        policy =vi.findViewById(R.id.policy);
        policy.setOnClickListener(this);
        shareapp = vi.findViewById(R.id.shareapp);
        shareapp.setOnClickListener(this);
        rateapp = vi.findViewById(R.id.rateapp);

        rateapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateUs();

            }
        });

        LinearLayout adaptiveAdContainer =vi.findViewById(R.id.banner_adaptive_container);
        RelativeLayout rl_native_ad =vi.findViewById(R.id.rl_native_ad);
        if (AdManager.adType.equals("admob")) {
            AdManager.initAd(getActivity());
            AdManager.loadNativeAd(getActivity(), adaptiveAdContainer);
        } else if (AdManager.adType.equals("max")){
            AdManager.initMAX(getActivity());
            AdManager.createNativeAdMAX(getActivity(), rl_native_ad);
        }else {
//            NativeAdLayout nativeAdLayout = vi.findViewById(R.id.native_ad_container);
//            AdManager.loadNativeFbAdPopup( nativeAdLayout, getActivity());
            AdManager.loadNativeFbad(getActivity());
        }

        return vi;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.shareapp:
                Intent myapp = new Intent(Intent.ACTION_SEND);
                myapp.setType("text/plain");
                myapp.putExtra(Intent.EXTRA_TEXT, "Hey my friend check out this app\n https://play.google.com/store/apps/details?id=" + getActivity().getPackageName() + " \n");
                startActivity(myapp);
                break;

            case R.id.policy:
                startActivityes(new Intent(getActivity(), PrivacyActivity.class));
                break;

            case R.id.moreapp:
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/dev?id=7081479513420377164&hl=en_IN")));
                break;
        }
    }

    void startActivityes(Intent intent) {
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
    }
    public void rateUs() {


        ReviewManager manager = ReviewManagerFactory.create(getActivity());
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(getActivity(), reviewInfo);
                flow.addOnCompleteListener(task2 -> {
                });
            }
        });
    }


}