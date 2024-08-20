package com.dizaraa.apps.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.dizaraa.apps.R;
import com.dizaraa.apps.VideoPreviewActivity;
import com.dizaraa.apps.model.DataModel;
import com.dizaraa.apps.utils.AdManager;
import com.dizaraa.apps.utils.Utils;

import java.util.ArrayList;

public class FullscreenImageAdapter extends PagerAdapter {
    Activity activity;
    ArrayList<DataModel> imageList;
    public FullscreenImageAdapter(Activity activity, ArrayList<DataModel> imageList) {
        this.activity = activity;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.preview_list_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        ImageView iconplayer = (ImageView) itemView.findViewById(R.id.iconplayer);

        if (!Utils.getBack(imageList.get(position).getFilePath(), "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)").isEmpty()) {
            iconplayer.setVisibility(View.VISIBLE);
        } else {
            iconplayer.setVisibility(View.GONE);
        }

        Glide.with(this.activity).load(imageList.get(position).getFilePath()).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.getBack(imageList.get(position).getFilePath(), "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)").isEmpty()) {
                    Intent intent = new Intent(activity, VideoPreviewActivity.class);
                    intent.putExtra("videoPath", imageList.get(position).getFilePath());

                    if (AdManager.adType.equals("admob")) {
                        AdManager.adCounter++;
                        AdManager.showInterAd(activity, intent);
                    } else if (AdManager.adType.equals("max")){
                        AdManager.adCounter++;
                        AdManager.showMaxInterstitial(activity, intent);
                    }else{
                        AdManager.adCounter++;
                        AdManager.showFBInterstitial(activity, intent);
                    }
                }
            }
        });
        container.addView(itemView);

        return itemView;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((RelativeLayout) object);
    }
}
