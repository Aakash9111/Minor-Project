package com.example.theftprotection.ui.main.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.theftprotection.ui.main.HomeFrag;
import com.example.theftprotection.ui.main.SettingFrag;
import com.example.theftprotection.ui.main.PicFrag;

public class MyAdapter extends FragmentPagerAdapter{

    private final Context myContext;
    final int totalTabs;

    public MyAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFrag(myContext);
            case 1:
                return new SettingFrag(myContext);
            case 2:
                return new PicFrag(myContext);
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }



    final SparseArray<Fragment> registeredFragments = new SparseArray<>();



    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position){
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }



}