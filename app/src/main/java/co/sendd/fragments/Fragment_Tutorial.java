package co.sendd.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import co.sendd.R;
import co.sendd.activity.Activity_Main;

/**
 * Created by harshkaranpuria on 5/2/15.
 */
public class Fragment_Tutorial extends Fragment {
    Fragment_Tutorial_Adapter mAdapter1;
    PageIndicator mIndicator;
    private ViewPager mPager;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((Activity_Main) getActivity()).hideActionBar();
        Activity_Main.exit = false;
        if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
            Activity_Main.mProgress.dismiss();
        }

    }

    public void onResume() {
        super.onResume();
        ((Activity_Main) getActivity()).hideActionBar();
        Activity_Main.exit = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tutorial, container, false);
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mAdapter1 = new Fragment_Tutorial_Adapter(getChildFragmentManager());
        mPager.setAdapter(mAdapter1);
        mIndicator = (CirclePageIndicator) rootView.findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


}
