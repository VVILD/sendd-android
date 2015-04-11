package co.sendd.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

/**
 * Created by Kuku on 06/03/15.
 */
public class Fragment_ViewPager_Adapter extends FragmentPagerAdapter implements IconPagerAdapter {


    public Fragment_ViewPager_Adapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return Fragment_ViewPager.create(position);

    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public int getIconResId(int index) {
        return 0;
    }
}
