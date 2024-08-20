package com.dizaraa.apps.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkUtils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.dizaraa.apps.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

public class AdManager {
    public static int adCounter = 1;

    public static String adType = "fb";

    public static boolean isloadFbAd = false;


    public static void initAd(Context context) {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

//        MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("C6094DAF8F5FD2D04193C2197D784750")).build());
    }

    static AdView gadView;
    public static void loadBannerAd(Context context, LinearLayout adContainer) {
        gadView = new AdView(context);
        gadView.setAdUnitId(context.getString(R.string.admob_banner_id));
        adContainer.addView(gadView);
        loadBanner(context);
    }

    static void loadBanner(Context context) {
        AdRequest adRequest =
                new AdRequest.Builder().build();

        AdSize adSize = getAdSize((Activity) context);
        gadView.setAdSize(adSize);
        gadView.loadAd(adRequest);
    }


    static AdSize getAdSize(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }


    public static void adptiveBannerAd(Context context, LinearLayout adContainer) {
        AdView adView = new AdView(context);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        adView.setAdUnitId(context.getString(R.string.admob_banner_id));
        adView.loadAd(adRequest);
        adContainer.addView(adView);
    }

    public static void loadNativeAd(Activity context, LinearLayout frameLayout) {

        AdLoader.Builder builder = new AdLoader.Builder(context, context.getString(R.string.admob_native_id))
                .forNativeAd(nativeAd -> {

                    NativeAdView adView = (NativeAdView) context.getLayoutInflater()
                            .inflate(R.layout.ad_lay, null);
                    // This method sets the text, images and the native ad, etc into the ad
                    // view.
                    populateNativeAdView(nativeAd, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                });

        VideoOptions videoOptions =
                new VideoOptions.Builder().setStartMuted(true).build();

        NativeAdOptions adOptions =
                new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(
                        new AdListener() {
                            @Override
                            public void onAdFailedToLoad(LoadAdError loadAdError) {
                            }
                        })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());


    }

    public static void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
//        if (nativeAd.getBody() == null) {
//            adView.getBodyView().setVisibility(View.INVISIBLE);
//        } else {
//            adView.getBodyView().setVisibility(View.VISIBLE);
//            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
//        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

//        if (nativeAd.getAdvertiser() == null) {
//            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
//        } else {
//            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
//            adView.getAdvertiserView().setVisibility(View.VISIBLE);
//        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);

    }



    static com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;

    public static void loadInterAd(Context context) {
        AdRequest adRequest = new AdRequest.Builder().build();
        com.google.android.gms.ads.interstitial.InterstitialAd.load(context,context.getString(R.string.admob_interstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
            }
        });
    }

    public static void showInterAd(final Activity context, final Intent intent) {
        if (adCounter == 4 && mInterstitialAd != null) {
            adCounter = 1;
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
//                    Log.d("TAG", "The ad was dismissed.");
                    loadInterAd(context);
                    startActivity(context, intent);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
//                     Called when fullscreen content failed to show.
//                    Log.d("TAG", "The ad failed to show.");
                }


                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null;
//                    Log.d("TAG", "The ad was shown.");
                }
            });
            mInterstitialAd.show((Activity) context);
        } else {
            if (adCounter == 4){
                adCounter = 1;
            }
            startActivity(context, intent);
        }
    }


    static void startActivity(Context context, Intent intent) {
        if (intent != null) {
            context.startActivity(intent);
        }
    }


    static MaxAdView maxAdView;

    public static void initMAX(Activity activity) {
        AppLovinSdk.getInstance(activity).setMediationProvider("max");
        AppLovinSdk.initializeSdk(activity, configuration -> { });
        Log.e("intitMAX","Initializing max");
    }

    public static void maxBanner(Activity activity, LinearLayout linearLayout) {
        maxAdView = new MaxAdView(activity.getResources().getString(R.string.max_banner), activity);

        // Stretch to the width of the screen for banners to be fully functional
        int width = ViewGroup.LayoutParams.MATCH_PARENT;

        // Banner height on phones and tablets is 50 and 90, respectively
        int heightPx = activity.getResources().getDimensionPixelSize(R.dimen.banner_height);

        maxAdView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));

        // Set background or background color for banners to be fully functional
        maxAdView.setBackgroundColor(activity.getResources().getColor(R.color.main_bg_home_color));

        if (Utils.isNetworkAvailable(activity)) {
            linearLayout.addView(maxAdView);
            Log.e("LoadingMax","Loading Max Banner");
            // Load the banner
            if (maxAdView != null) {
                maxAdView.loadAd();
                Log.e("LoadedMax","Loaded Max Banner");
            }
        }

    }

    static MaxAd nativeAd;

    public static void createNativeAdMAX(Context context, RelativeLayout nativeAdContainer) {
        nativeAd = null;
        MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(context.getResources().getString(R.string.max_native), context);
        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
            @Override
            public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                Log.e("nativeAdLoaded", "onNativeAdLoaded");
                // Clean up any pre-existing native ad to prevent memory leaks.
                if (nativeAd != null) {
                    nativeAdLoader.destroy(nativeAd);
                    Log.e("nativeAdLoaded", "destroy");
                }

                // Save ad for cleanup.
                nativeAd = ad;

                // Add ad view to view.
                nativeAdContainer.removeAllViews();
                nativeAdContainer.addView(nativeAdView);
            }

            @Override
            public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                // We recommend retrying with exponentially higher delays up to a maximum delay
                Log.e("onNativeAdLoadFailed", "Native Ad Failed");
            }

            @Override
            public void onNativeAdClicked(final MaxAd ad) {
                // Optional click callback
            }
        });

        nativeAdLoader.loadAd();
    }

    static MaxAdView maxAdAdaptive;

    public static void maxBannerAdaptive(Activity activity, LinearLayout linearLayout) {
        maxAdAdaptive = new MaxAdView(activity.getResources().getString(R.string.max_banner), activity);

        // Stretch to the width of the screen for banners to be fully functional
        int width = ViewGroup.LayoutParams.MATCH_PARENT;

        // Get the adaptive banner height.
        int heightDp = MaxAdFormat.BANNER.getAdaptiveSize(activity).getHeight();
        int heightPx = AppLovinSdkUtils.dpToPx(activity, heightDp);

        maxAdAdaptive.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
        maxAdAdaptive.setExtraParameter("adaptive_banner", "true");

        // Set background or background color for banners to be fully functional
        maxAdAdaptive.setBackgroundColor(activity.getResources().getColor(R.color.main_bg_home_color));

        if (Utils.isNetworkAvailable(activity)) {
            linearLayout.addView(maxAdAdaptive);

            // Load the adaptive
            if (maxAdAdaptive != null) {
                maxAdAdaptive.loadAd();
            }
        }
    }


    static Intent maxIntent;
    static int maxRequstCode;
    static MaxInterstitialAd maxInterstitialAd;

    public static void maxInterstital(Activity activity) {
        maxInterstitialAd = new MaxInterstitialAd(activity.getResources().getString(R.string.max_interstitial), activity);
        maxInterstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {
                maxInterstitialAd.loadAd();
                startActivity(activity, maxIntent);
            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
//                if (Utils.isNetworkAvailable(activity)) {
//                    maxInterstitialAd.loadAd();
//                    Log.e("onAdLoadFailed","AdFailed1");
//                }
                startActivity(activity, maxIntent);
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
//                if (Utils.isNetworkAvailable(activity)) {
//                    maxInterstitialAd.loadAd();
//                    Log.e("onAdLoadFailed","AdFailed2");
//                }
                startActivity(activity, maxIntent);
            }
        });

        if (Utils.isNetworkAvailable(activity)) {
            // Load the first ad
            maxInterstitialAd.loadAd();
        }
    }

    public static void showMaxInterstitial(final Activity context, final Intent intent) {
        maxIntent = intent;
        if (adCounter == 4 && maxInterstitialAd != null && maxInterstitialAd.isReady()) {
            adCounter = 1;
            maxInterstitialAd.showAd();
        } else {
            if (adCounter == 4) {
                adCounter = 1;
            }
            startActivity(context, intent);
        }
    }

    public static LinearLayout adView;
    public static com.facebook.ads.NativeAd nativeAdfbPopup;
    public static void loadNativeFbAdPopup(NativeAdLayout nativeAdLayout, Context activity) {

        nativeAdfbPopup = new com.facebook.ads.NativeAd(activity, activity.getString(R.string.fb_native_ad_id));
        NativeAdListener nativeAdListener = new NativeAdListener() {

            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e("onError: ",""+adError);
            }

            @Override
            public void onAdLoaded(Ad ad) {

                if (nativeAdfbPopup == null || nativeAdfbPopup != ad) {
                    return;
                }

                Log.e("onError" ,"onAdLoaded: " );


                inflateFBAdPopup(nativeAdfbPopup,nativeAdLayout, activity);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        // Load an ad
        nativeAdfbPopup.loadAd(
                nativeAdfbPopup.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }
    static void inflateFBAdPopup(com.facebook.ads.NativeAd nativeAd,NativeAdLayout nativeAdLayout, Context activity) {

        // unregister the native Ad View
        nativeAdfbPopup.unregisterView();

        final float scale = activity.getResources().getDisplayMetrics().density;
        int pixels = (int) (300 * scale + 0.5f);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                /*width*/ ViewGroup.LayoutParams.MATCH_PARENT,
                /*height*/ pixels
        );
        nativeAdLayout.setLayoutParams(param);
        // Add the Ad view into the ad container.
//        nativeAdLayout = activity.findViewById(R.id.native_ad_container);

        LayoutInflater inflater = LayoutInflater.from(activity);

        // Inflate the Ad view.
        adView = (LinearLayout) inflater.inflate(R.layout.fbnative, nativeAdLayout, false);

        // adding view
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(activity, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        com.facebook.ads.MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        com.facebook.ads.MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Setting  the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and  button to listen for clicks.
        nativeAd.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon, clickableViews);
    }

    public static AlertDialog ProgressDialog;
    public static void Ad_Popup(Context context) {
        ProgressDialog = null;
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View view = layoutInflaterAndroid.inflate(R.layout.dialog_loading, null);
        AlertDialog.Builder alert = null;
        alert = new AlertDialog.Builder(context);
        alert.setView(view);

        ProgressDialog = alert.create();

        ProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ProgressDialog.setCancelable(false);

        ProgressDialog.show();

    }



    public static void initFBAds(Activity activity) {
        AudienceNetworkAds.initialize(activity);
        AdSettings.setTestMode(true);
    }

    public static void fbBanner(Activity activity, LinearLayout adContainer) {
        adViewFb = new com.facebook.ads.AdView(activity, activity.getString(R.string.fb_bannerad), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        adContainer.addView(adViewFb);
        adViewFb.loadAd();
    }

    public static com.facebook.ads.AdView adViewFb;


    public static com.facebook.ads.InterstitialAd interstitialAd;

    public static void showFBInterstitial(final Activity context, final Intent intent) {
        maxIntent = intent;

        if(adCounter == 4){
            Ad_Popup(context);
            interstitialAd = new com.facebook.ads.InterstitialAd(context, context.getString(R.string.fb_interad));
            // Create listeners for the Interstitial Ad
            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    // Interstitial dismissed callback
                    ProgressDialog.dismiss();
                    startActivity(context, maxIntent);
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    Log.e("onError: ", "" + adError);
                    ProgressDialog.dismiss();
                    startActivity(context, maxIntent);
                }

                @Override
                public void onAdLoaded(Ad ad) {

                    ProgressDialog.dismiss();
                    interstitialAd.show();

                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Ad clicked callback

                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback

                }
            };

            // For auto play video ads, it's recommended to load the ad
            // at least 30 seconds before it is shown
            interstitialAd.loadAd(
                    interstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());
        }else{
            if (adCounter == 4){
                adCounter = 1;
            }
//            ProgressDialog.dismiss();
            startActivity(context, maxIntent);
        }




    }
//        else
//        {
////            ProgressDialog.dismiss();
//            startActivity(context, maxIntent, maxRequstCode);
//        }
//    }

    public static com.facebook.ads.NativeAd nativeAdfb;
    public static NativeAdLayout nativeAdLayout;
    public static void loadNativeFbad(Activity activity) {

        nativeAdfb = new com.facebook.ads.NativeAd(activity, activity.getString(R.string.fb_native_ad_id));
        NativeAdListener nativeAdListener = new NativeAdListener() {

            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e("onError: ",""+adError);
            }

            @Override
            public void onAdLoaded(Ad ad) {

                if (nativeAdfb == null || nativeAdfb != ad) {
                    return;
                }

                Log.e("onError" ,"onAdLoaded: " );


                inflateAd(nativeAdfb,activity);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        // Load an ad
        nativeAdfb.loadAd(
                nativeAdfb.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }


    static void inflateAd(com.facebook.ads.NativeAd nativeAd, Activity activity) {

        // unregister the native Ad View
        nativeAdfb.unregisterView();

        // Add the Ad view into the ad container.
        nativeAdLayout = activity.findViewById(R.id.native_ad_container);

//        final float scale = activity.getResources().getDisplayMetrics().density;
//        int pixels = (int) (300 * scale + 0.5f);
//        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
//                /*width*/ ViewGroup.LayoutParams.MATCH_PARENT,
//                /*height*/ pixels
//        );
//        nativeAdLayout.setLayoutParams(param);

        LayoutInflater inflater = LayoutInflater.from(activity);

        // Inflate the Ad view.
        adView = (LinearLayout) inflater.inflate(R.layout.fbnative, nativeAdLayout, false);

        // adding view
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = activity.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(activity, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        com.facebook.ads.MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        com.facebook.ads.MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Setting  the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and  button to listen for clicks.
        nativeAd.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon, clickableViews);
    }

}
