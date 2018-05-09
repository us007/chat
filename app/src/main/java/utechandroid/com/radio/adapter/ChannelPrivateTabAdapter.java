package utechandroid.com.radio.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import utechandroid.com.radio.ui.channelJoinRequest.ChannelJoinRequestFragment;
import utechandroid.com.radio.ui.channelmembers.ChannelMembersFragment;

/**
 * Created by Utsav Shah on 16-Nov-17.
 */
public class ChannelPrivateTabAdapter extends FragmentPagerAdapter {

    private String id;

    public ChannelPrivateTabAdapter(FragmentManager fm,String id) {
        super(fm);
        this.id = id;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new ChannelJoinRequestFragment();
            Bundle args = new Bundle();
            args.putString("id", id);
            fragment.setArguments(args);
        } else if (position == 1) {
            fragment = new ChannelMembersFragment();
            Bundle args = new Bundle();
            args.putString("id", id);
            fragment.setArguments(args);
        } /*else if (position == 2) {
            fragment = new FragmentC();
        }*/
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = "Requests";
        } else if (position == 1) {
            title = "Members";
        } /*else if (position == 2) {
            title = "Tab-3";
        }*/
        return title;
    }
}


