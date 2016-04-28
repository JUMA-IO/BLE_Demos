package com.juma.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MyViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragmentList;
        private ArrayList<String> titleList;

        public MyViewPagerAdapter(FragmentManager fm,ArrayList f,ArrayList t) {
            super(fm);
            fragmentList = f;
            titleList = t;
            // TODO Auto-generated constructor stub
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            // TODO Auto-generated method stub
            return titleList.get(position);
        }
    }