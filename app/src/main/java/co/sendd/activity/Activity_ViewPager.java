package co.sendd.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import co.sendd.R;
import co.sendd.fragments.Fragment_ViewPager_Adapter;

public class Activity_ViewPager extends FragmentActivity {

    public static Activity ViewPager;
    Fragment_ViewPager_Adapter mAdapter1;
    PageIndicator mIndicator;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);
        ViewPager = this;
        mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter1 = new Fragment_ViewPager_Adapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter1);
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
