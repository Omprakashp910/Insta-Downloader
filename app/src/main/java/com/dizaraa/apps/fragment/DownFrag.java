package com.dizaraa.apps.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dizaraa.apps.R;
import com.dizaraa.apps.adapter.DownloadAdapter;
import com.dizaraa.apps.model.DataModel;
import com.dizaraa.apps.utils.AdManager;
import com.dizaraa.apps.utils.Utils;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class DownFrag extends Fragment{

    public SharedPreferences sharedPreferences_one;

    File file;
    ArrayList<DataModel> downloadImageList = new ArrayList<>();
    ArrayList<DataModel> downloadVideoList = new ArrayList<>();
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView mRecyclerView;
    LinearLayout isEmptyList;
    DownloadAdapter mAdapter;
    TextView txt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.fragment_down_frag, container, false);

        mRecyclerView = vi.findViewById(R.id.my_recycler_view_1);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(this.mLayoutManager);
        isEmptyList = vi.findViewById(R.id.isEmptyList);
        txt = vi.findViewById(R.id.txt);

        if (AdManager.adType.equals("admob")) {
            AdManager.initAd(getActivity());
            AdManager.loadInterAd(getActivity());
        } else if (AdManager.adType.equals("max")){
            AdManager.initMAX(getActivity());
            AdManager.maxInterstital(getActivity());
        }else{
            AdManager.initFBAds(getActivity());
        }
        return vi;
    }


    @Override
    public void onResume() {
        super.onResume();
        loadMedia();
    }

    public void loadMedia() {
        file = Utils.downloadInstaDir;
        sharedPreferences_one = getActivity().getSharedPreferences("mypref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences_one.edit();
        editor.putString("file", String.valueOf(file));
        editor.apply();

        downloadImageList.clear();
        downloadVideoList.clear();
        if (!file.isDirectory()) {
            return;
        }

        displayfiles(file, mRecyclerView);

    }

    void displayfiles(File file, final RecyclerView mRecyclerView) {
        File[] listfilemedia =dirListByAscendingDate (file);
        if (listfilemedia.length != 0) {
            isEmptyList.setVisibility(View.GONE);
        } else {
            isEmptyList.setVisibility(View.VISIBLE);
        }
        int i = 0;
        while (i < listfilemedia.length) {
            downloadImageList.add(new DataModel(listfilemedia[i].getAbsolutePath(), listfilemedia[i].getName()));
            i++;
        }
        if (downloadImageList.size() > 0) {
            isEmptyList.setVisibility(View.GONE);
        } else {
            isEmptyList.setVisibility(View.VISIBLE);
        }
        mAdapter = new DownloadAdapter(getActivity(), downloadImageList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public static File[] dirListByAscendingDate(File folder) {
        if (!folder.isDirectory()) {
            return null;
        }
        File[] sortedByDate = folder.listFiles();
        if (sortedByDate == null || sortedByDate.length <= 1) {
            return sortedByDate;
        }
        Arrays.sort(sortedByDate, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        return sortedByDate;
    }



}