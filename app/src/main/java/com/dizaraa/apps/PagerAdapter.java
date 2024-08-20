package com.dizaraa.apps;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.dizaraa.apps.fragment.DownFrag;
import com.dizaraa.apps.fragment.HomeFrag;
import com.dizaraa.apps.fragment.SettingsFrag;

public class PagerAdapter extends FragmentStateAdapter {
    public PagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                HomeFrag tab1 = new HomeFrag();
                return tab1;
            case 1:
                DownFrag tab0 = new DownFrag();
                return tab0;
            case 2:
                SettingsFrag tab2 = new SettingsFrag();
                return tab2;
            default:
                return null;
        }
    }
    @Override
    public int getItemCount() {
        return 3;
    }
}
