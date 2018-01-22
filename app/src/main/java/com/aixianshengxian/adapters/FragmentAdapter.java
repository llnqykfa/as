package com.aixianshengxian.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by CWJ on 2016/11/25 0025.
 */

public class FragmentAdapter extends FragmentPagerAdapter {
    public FragmentManager fm;
    List<Fragment> mlist;
    public FragmentAdapter(FragmentManager fm, List<Fragment> mlist) {
        super(fm);
        this.fm = fm ;
        this.mlist =mlist;
    }


    @Override
    public Fragment getItem(int position) {
//        Fragment fragment = null;
//        Log.i("sssssssssssss", "getItem");
//        fragment = mlist.get(position);
//        Bundle bundle = new Bundle();
//        bundle.putString("id", "" + position);
//        fragment.setArguments(bundle);
        return mlist.get(position);

    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mlist.get(position).getArguments().get("title").toString();
    }
//    @Override
//    public Fragment instantiateItem(ViewGroup container, int position) {
//        Fragment fragment = (Fragment) super.instantiateItem(container,
//                position);
//        fm.beginTransaction().show(fragment).commit();
//        return fragment;
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        // super.destroyItem(container, position, object);
//        Fragment fragment = mlist.get(position);
//        fm.beginTransaction().hide(fragment).commit();
//    }
}
