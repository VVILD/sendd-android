package co.sendd.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import co.sendd.R;

public final class Fragment_Tutorials extends Fragment {
    public static final String ARG_PAGE = "page";
    private int mPageNumber;

    public static Fragment_Tutorials create(int pageNumber) {
        Fragment_Tutorials fragment = new Fragment_Tutorials();
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
        int[] a = new int[]{R.drawable.camera_icon, R.drawable.bike_icon, R.drawable.packaging_icon, R.drawable.relax_icon};
        String[] xx = new String[]{"Click a photo", "On demand pickup", "Free Packaging", "Sit back & relax"};
        String[] yy = new String[]{"Click the photo of the item & set up the destination address. We sendd across the world !", "Sendd representative will be at your doorstep to pickup your parcel at your preferred time.", "Trained packing specialists will bring world class packing materials and assist in packaging to make sure your items reach safely.", "We will pick up your items, pack, and sendd them using the most cost effective & reliable shipping options."};
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tutorials, container, false);
        ImageView icon = (ImageView) rootView.findViewById(R.id.ivICONS);
        icon.setImageResource(a[mPageNumber]);
        TextView text = (TextView) rootView.findViewById(R.id.textView1);
        text.setText(xx[mPageNumber]);
        TextView text2 = (TextView) rootView.findViewById(R.id.textView2);
        text2.setText(yy[mPageNumber]);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
