package com.dizaraa.apps.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dizaraa.apps.InstaActivity;
import com.dizaraa.apps.PreviewActivity;
import com.dizaraa.apps.R;
import com.dizaraa.apps.model.DataModel;
import com.dizaraa.apps.utils.AdManager;
import com.dizaraa.apps.utils.Utils;
import com.facebook.ads.NativeAdLayout;

import java.io.File;
import java.util.ArrayList;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {
    private Activity activity;
    private File file;
    ArrayList<DataModel> mData;

    public DownloadAdapter(Activity paramActivity, ArrayList<DataModel> paramArrayList) {
        this.mData = paramArrayList;
        this.activity = paramActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.download_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final DataModel jpast = (DataModel) this.mData.get(position);
        this.file = new File(jpast.getFilePath());
        if (!this.file.isDirectory()) {
            if (!Utils.getBack(jpast.getFilePath(), "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)").isEmpty()) {
                try {
                    Glide.with(this.activity).load(this.file).apply(new RequestOptions().placeholder(R.color.black).error(android.R.color.black).optionalTransform(new RoundedCorners(1))).into(holder.imagevi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.imagePlayer.setVisibility(View.VISIBLE);
            } else if (!Utils.getBack(jpast.getFilePath(), "((\\.3ga|\\.aac|\\.aif|\\.aifc|\\.aiff|\\.amr|\\.au|\\.aup|\\.caf|\\.flac|\\.gsm|\\.kar|\\.m4a|\\.m4p|\\.m4r|\\.mid|\\.midi|\\.mmf|\\.mp2|\\.mp3|\\.mpga|\\.ogg|\\.oma|\\.opus|\\.qcp|\\.ra|\\.ram|\\.wav|\\.wma|\\.xspf)$)").isEmpty()) {
                holder.imagePlayer.setVisibility(View.GONE);
            } else if (!Utils.getBack(jpast.getFilePath(), "((\\.jpg|\\.png|\\.gif|\\.jpeg|\\.bmp)$)").isEmpty()) {
                holder.imagePlayer.setVisibility(View.GONE);
                Glide.with(this.activity).load(this.file).apply(new RequestOptions().placeholder(R.color.black).error(android.R.color.black).optionalTransform(new RoundedCorners(1))).into(holder.imagevi);
            }

            holder.deleteIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(position, activity);
                }
            });

            holder.shareIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    share(jpast.getFilePath(), activity);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private RelativeLayout cardView;
        private ImageView imagePlayer;
        private ImageView imagevi, shareIV, deleteIV;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imagevi = (itemView.findViewById(R.id.imageView));
            this.imagePlayer = (itemView.findViewById(R.id.iconplayer));
            this.cardView = (itemView.findViewById(R.id.card_view));
            this.cardView.setOnClickListener(this);
            this.shareIV = itemView.findViewById(R.id.shareIV);
            this.deleteIV = itemView.findViewById(R.id.deleteIV);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(activity, PreviewActivity.class);
            intent.putParcelableArrayListExtra("images", mData);
            intent.putExtra("position", getAdapterPosition());
            intent.putExtra("statusdownload", "download");
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

    void share(String path, Activity activity) {
        Utils.mShare(path, activity);
    }
    @SuppressLint("ClickableViewAccessibility")
    void delete(final int position, Activity activity) {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(activity);
        View view = layoutInflaterAndroid.inflate(R.layout.delete_pop_up, null);
        android.app.AlertDialog.Builder alert = null;
        alert = new AlertDialog.Builder(activity);
        alert.setView(view);
        AlertDialog exitDialog = alert.create();
        exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitDialog.setCancelable(false);
        exitDialog.show();

        LinearLayout adaptiveAdContainer =view.findViewById(R.id.banner_adaptive_container);
        RelativeLayout rl_native_ad =view.findViewById(R.id.rl_native_ad);

        if (AdManager.adType.equals("admob")) {
            AdManager.loadNativeAd(activity,adaptiveAdContainer);
        } else if (AdManager.adType.equals("max")){
            AdManager.createNativeAdMAX(activity,rl_native_ad);
        }else{
            NativeAdLayout nativeAdLayout = view.findViewById(R.id.native_ad_container);
            AdManager.loadNativeFbAdPopup( nativeAdLayout, activity);
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
                File file = new File(mData.get(position).getFilePath());
                if (file.exists()) {
                    file.delete();
                    mData.remove(position);
                    notifyDataSetChanged();
                }

                AdManager.adCounter=4;
                if (AdManager.adType.equals("admob")) {
                    AdManager.showInterAd(activity, null);
                } else if (AdManager.adType.equals("max")){
                    AdManager.showMaxInterstitial(activity, null);
                }else{
                    AdManager.showFBInterstitial(activity, null);
                }
                return true;
            }
        });

//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
//        alertDialog.setTitle("Delete");
//        alertDialog.setMessage("Are You Sure to Delete this File?");
//        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                File file = new File(mData.get(position).getFilePath());
//                if (file.exists()) {
//                    file.delete();
//                    mData.remove(position);
//                    notifyDataSetChanged();
//                }
//
//                AdManager.adCounter=4;
//                if (AdManager.adType.equals("admob")) {
//                    AdManager.showInterAd(activity, null);
//                } else if (AdManager.adType.equals("max")){
//                    AdManager.showMaxInterstitial(activity, null);
//                }else{
//                    AdManager.showFBInterstitial(activity, null);
//                }
//            }
//        });
//        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
//        alertDialog.show();
    }
}
