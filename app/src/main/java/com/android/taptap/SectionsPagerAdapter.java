package com.android.taptap;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class SectionsPagerAdapter extends FragmentPagerAdapter {
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){



            case (0):
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            case (1):
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }
    public CharSequence getPageTitle(int possition){

        switch (possition){

            case (0):
                return "CHATS";
            case(1):
                return "REQUESTS";
            default:
                return null;

        }
    }
}
