package co.sendd.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.sendd.R;
import co.sendd.activity.Activity_Main;
import co.sendd.helper.Utils;

public final class Fragment_ViewPager extends Fragment {

    public static final String ARG_PAGE = "page";
    private int mPageNumber;

    public static Fragment_ViewPager create(int pageNumber) {
        Fragment_ViewPager fragment = new Fragment_ViewPager();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int[] a = new int[]{R.drawable.logo_circle_icon, R.drawable.camera_icon, R.drawable.bike_icon, R.drawable.packaging_icon, R.drawable.relax_icon, R.drawable.logo_circle_icon};
        String[] xx = new String[]{"The most convenient way to sendd anything anywhere", "Click a photo", "On demand pickup", "Free Packaging", "Sit back & relax", "Get ready to Sendd"};
        String[] yy = new String[]{"<< Swipe to know how", "Click the photo of the item & set up the destination address. We sendd across the world !", "Sendd representative will be at your doorstep to pickup your parcel at your preferred time.", "Trained packing specialists will bring world class packing materials and assist in packaging to make sure your items reach safely.", "We will pick up your items, pack, and sendd them using the most cost effective & reliable shipping options.", ""};
        int[] sss = new int[]{View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE};
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_viewpager, container, false);
        ImageView icon = (ImageView) rootView.findViewById(R.id.ivICONS);
        icon.setImageResource(a[mPageNumber]);
        TextView text = (TextView) rootView.findViewById(R.id.textView1);
        text.setText(xx[mPageNumber]);
        TextView text2 = (TextView) rootView.findViewById(R.id.textView2);
        text2.setText(yy[mPageNumber]);
        LinearLayout buttonslayout = (LinearLayout) rootView.findViewById(R.id.llviewPagerButtons);
        buttonslayout.setVisibility(sss[mPageNumber]);

        Button bGetStrated = (Button) rootView.findViewById(R.id.bGetStarted);

        bGetStrated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils mUtils = new Utils(getActivity());
                mUtils.setvalue("Tutorial_Displayed", "true");
                Intent i = new Intent(getActivity(), Activity_Main.class);
                startActivity(i);
                getActivity().finish();
                getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
