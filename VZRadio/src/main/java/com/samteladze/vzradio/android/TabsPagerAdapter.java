package com.samteladze.vzradio.android;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    private MainActivity mMainActivity;

    /*
    NOTE:
    Passing MainActivity to get access to string resources (page titles to be precise).
    Is there a better way? A?
     */
    public TabsPagerAdapter(FragmentManager fm, MainActivity mainActivity) {
        super(fm);
        mMainActivity = mainActivity;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return new EventsFragment();
            case 1:
                return new RadioFragment();
            case 2:
                return new TopChartFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale locale = Locale.getDefault();
        switch (position) {
            case 0:
                return  mMainActivity.getString(R.string.events_fragment_title);
            case 1:
                return mMainActivity.getString(R.string.radio_fragment_title);
            case 2:
                return mMainActivity.getString(R.string.top_chart_fragment_title);
        }
        return null;
    }
}
