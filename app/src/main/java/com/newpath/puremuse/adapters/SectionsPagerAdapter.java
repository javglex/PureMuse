package com.newpath.puremuse.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.newpath.puremuse.ui.main.CollectionsFragment;
import com.newpath.puremuse.ui.main.MainFragment;
import com.newpath.puremuse.utils.Constants;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return CollectionsFragment.newInstance(Constants.COLLECTION_TYPE.ALBUM);
            case 1:
                return MainFragment.Companion.newInstance(-1);
            case 2:
                return CollectionsFragment.newInstance(Constants.COLLECTION_TYPE.PLAYLIST);
            default:
                return MainFragment.Companion.newInstance(-1);
        }

    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Albums";
            case 1:
                return "PureMuse";
            case 2:
                return "Playlists";
        }
        return null;
    }
}