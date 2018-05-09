package utechandroid.com.radio.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import utechandroid.com.radio.ui.MyRadios.MyRadiosFragment;
import utechandroid.com.radio.ui.channel.ChannelFragment;

/**
 * Created by Utsav Shah on 14-Oct-17.
 */


public class HomeTabAdapter extends FragmentPagerAdapter {

    public HomeTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new ChannelFragment();
        } else if (position == 1) {
            fragment = new MyRadiosFragment();
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
            title = "Channels";
        } else if (position == 1) {
            title = "My Radio";
        } /*else if (position == 2) {
            title = "Tab-3";
        }*/
        return title;
    }
}
