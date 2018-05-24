package com.tijchat.tijchat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Switch;

import com.tijchat.tijchat.Fragments.Add;
import com.tijchat.tijchat.Fragments.Chat;
import com.tijchat.tijchat.Fragments.ContactFragments;

/**
 * Created by DELL PC on 12/05/2018.
 */

class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Add addFragment = new Add();
                return addFragment;

            case 1:
                Chat chatFragment = new Chat();
                return chatFragment;

            case 2:
                ContactFragments contactFragments = new ContactFragments();
                return contactFragments;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){

        switch (position){

            case 0:
                return "Add";

            case 1:
                return "Chats";

            case 2:
                return "Contacts";

            default:
                return null;
        }
    }
}
