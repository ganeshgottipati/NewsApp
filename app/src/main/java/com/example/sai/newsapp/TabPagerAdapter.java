package com.example.sai.newsapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

/**
 * Created by sai on 15-06-2018.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    private String titles[]={"Sports","Education","Business","Movies","Political"};
    public TabPagerAdapter(FragmentManager fragmentManager)
    {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {

        if(position==0)
            return new Sports();
        else if(position==1)
            return new Education();
        else if(position==2)
            return new Business();
        else if(position==3)
            return new Movies();
        else if(position == 4)
            return new Political();
        else
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }

    public CharSequence getPageTitle(int position)
    {
        return titles[position];
    }
}

