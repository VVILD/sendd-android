package co.sendd.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import co.sendd.R;
import co.sendd.activity.Activity_Main;
import co.sendd.activity.Activity_Reigister_Phone;
import co.sendd.activity.Activity_Shipment_details;
import co.sendd.databases.Db_CompleteOrder;
import co.sendd.gettersandsetters.CompleteOrder;
import co.sendd.helper.NetworkUtils;
import co.sendd.helper.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Fragment_MyShipments extends Fragment {
    Utils utils;
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity_Main.exit = false;
        utils = new Utils(getActivity());
        TabHost mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
        mTabHost.setup();
        ((Activity_Main) getActivity()).setActionBarTitle("Previous Bookings");
        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.pager);
        TabsAdapter mTabsAdapter = new TabsAdapter(getActivity(), mTabHost, mViewPager);

        if ((utils.isRegisterd())) {
            if (utils.isSynced()) {
                Activity_Main.exit = false;
                mTabsAdapter.addTab(mTabHost.newTabSpec("ALL").setIndicator("ALL"),
                        All_Fragment.class, null);
                mTabsAdapter.addTab(mTabHost.newTabSpec("UPCOMING").setIndicator("UPCOMING"),
                        Upcoming_Fragment.class, null);
                mTabsAdapter.addTab(mTabHost.newTabSpec("COMPLETED").setIndicator("COMPLETED"),
                        Completed_Fragment.class, null);
                mTabsAdapter.addTab(mTabHost.newTabSpec("CANCELLED").setIndicator("CANCELLED"),
                        Cancelled_Fragment.class, null);
                mTabHost.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
                    Activity_Main.mProgress.dismiss();
                }
            } else {
                final ProgressDialog pd;
                pd = new ProgressDialog(getActivity());
                pd.setMessage("Fetching bookings, please wait");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
                final NetworkUtils mnetworkutils = new NetworkUtils(getActivity());
                if (mnetworkutils.isnetconnected()) {
                    mnetworkutils.getapi().getPreviousShipments(utils.getvalue("RegisteredPhone"), "100", new Callback<Response>() {
                        @Override
                        public void success(Response previousShipments, Response response) {
                            if (previousShipments.getBody() != null) {
                                BufferedReader reader;
                                StringBuilder sb = new StringBuilder();
                                try {
                                    reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                                    String line;
                                    try {
                                        while ((line = reader.readLine()) != null) {
                                            sb.append(line);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                String result = sb.toString();
                                try {
                                    JSONObject jObj = new JSONObject(result);
                                    JSONArray shipments = jObj.getJSONArray("objects");
                                    for (int j = 0; j < shipments.length(); j++) {
                                        CompleteOrder co = new CompleteOrder();
                                        Db_CompleteOrder DBCO = new Db_CompleteOrder();
                                        JSONObject booking = shipments.getJSONObject(j);
                                        co.setCategory(booking.getString("category"));
                                        if (booking.getString("date").equals("null")) {
                                            co.setDate(null);
                                        } else {
                                            co.setDate(formatter.parse(booking.getString("date")));
                                        }
                                        co.setPickup_address(booking.getString("address"));
                                        co.setPickup_name(booking.getString("name"));
                                        co.setPickup_phone(booking.getString("phone"));
                                        co.setPickup_pincode("");
                                        co.setTime(booking.getString("time"));
                                        co.setOrder_Status(booking.getString("status"));
                                        co.setPaid(booking.getString("paid"));
                                        co.setTotal_cost("");
                                        co.setDrop_address(booking.getString("drop_address"));
                                        co.setDrop_phone(booking.getString("drop_phone"));
                                        co.setDrop_name(booking.getString("drop_name"));
                                        co.setDrop_pincode("pincode");
                                        co.setCost(booking.getString("price"));
                                        co.setImage_uri(booking.getString("img"));
                                        co.setTracking_no(booking.getString("tracking_no"));
                                        co.setOrder_id(booking.getString("order"));
                                        DBCO.AddToDB(co);
                                    }

                                    utils.Sync();
                                    Intent i = new Intent(getActivity().getApplicationContext(), Activity_Main.class);
                                    i.putExtra("openpreviousBooking", true);
                                    startActivity(i);
                                    getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                                } catch (JSONException | ParseException | NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (pd.isShowing()) {
                                pd.dismiss();
                            }
                            if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
                                Activity_Main.mProgress.dismiss();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
                                Activity_Main.mProgress.dismiss();
                            }
                            Toast.makeText(getActivity(), "Failed to fetch bookings. Please try again later", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }
            }
        } else {
            Intent i = new Intent(getActivity(), Activity_Reigister_Phone.class);
            i.putExtra("PreviousShipments", true);
            getActivity().startActivity(i);
            getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_myshipments, container, false);
    }

    public void onResume() {
        super.onResume();
        Activity_Main.exit = false;
        ((Activity_Main) getActivity()).setActionBarTitle("Previous Bookings");
        if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
            Activity_Main.mProgress.dismiss();
        }

    }

    public static class All_Fragment extends Fragment {
        ListView AllShipmentListView;
        Shipments_adapter mAdapter;
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config;
        private ArrayList<CompleteOrder> order_data;
        private ArrayList<CompleteOrder> Order_List;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return getActivity().getLayoutInflater().inflate(R.layout.fragments_allshipments, container, false);
        }

        public ArrayList<CompleteOrder> ShowItemList() {

            Order_List = new ArrayList<>();
            List<Db_CompleteOrder> list = Db_CompleteOrder.getAllAddress();
            for (int i = 0; i < list.size(); i++) {
                Db_CompleteOrder addDBReceiver = list.get(i);
                CompleteOrder completeOrder = new CompleteOrder();
                completeOrder.setDate(addDBReceiver.date);
                completeOrder.setPickup_address(addDBReceiver.pickup_address);
                completeOrder.setPickup_name(addDBReceiver.pickup_name);
                completeOrder.setPickup_phone(addDBReceiver.pickup_phone);
                completeOrder.setPickup_pincode(addDBReceiver.pickup_pincode);
                completeOrder.setTime(addDBReceiver.time);
                completeOrder.setOrder_Status(addDBReceiver.Order_Status);
                completeOrder.setPaid(addDBReceiver.paid);
                completeOrder.setTotal_cost(addDBReceiver.total_cost);
                completeOrder.setDrop_address(addDBReceiver.drop_address);
                completeOrder.setDrop_phone(addDBReceiver.drop_phone);
                completeOrder.setDrop_name(addDBReceiver.drop_name);
                completeOrder.setDrop_pincode(addDBReceiver.drop_pincode);
                completeOrder.setCost(addDBReceiver.cost);
                completeOrder.setImage_uri(addDBReceiver.image_uri);
                completeOrder.setTracking_no(addDBReceiver.tracking_no);
                completeOrder.setCategory(addDBReceiver.category);
                completeOrder.setOrder_id(addDBReceiver.Order_id);

                Order_List.add(completeOrder);
            }
            Collections.reverse(Order_List);
            return Order_List;
        }

        @Override
        public void onResume() {
            super.onResume();
            if (!ImageLoader.getInstance().isInited())
                ImageLoader.getInstance().init(config);

        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            config = new ImageLoaderConfiguration.Builder(getActivity()).defaultDisplayImageOptions(defaultOptions).build();
            AllShipmentListView = (ListView) view.findViewById(R.id.allshipment_list);
            order_data = ShowItemList();
            mAdapter = new Shipments_adapter(getActivity(), R.layout.list_item_allshipments, order_data);
            AllShipmentListView.setAdapter(mAdapter);
            AllShipmentListView.invalidateViews();
            mAdapter.notifyDataSetChanged();
            AllShipmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getActivity(), Activity_Shipment_details.class);
                    i.putExtra("tracking_no", Order_List.get(position).getTracking_no());
                    i.putExtra("imageURI", Order_List.get(position).getImage_uri());
                    SimpleDateFormat sdf = new SimpleDateFormat("h:m a, d MMMM yyyy", Locale.US);
                    if (Order_List.get(position).getDate() != null)
                        i.putExtra("datetime", sdf.format(Order_List.get(position).getDate()));
                    startActivity(i);
                    getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                }
            });
            if (!ImageLoader.getInstance().isInited())
                ImageLoader.getInstance().init(config);

        }

    }

    public static class Upcoming_Fragment extends Fragment {
        ListView AllShipmentListView;
        Shipments_adapter mAdapter;
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config;
        private ArrayList<CompleteOrder> order_data;
        private ArrayList<CompleteOrder> Order_List;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            return getActivity().getLayoutInflater().inflate(R.layout.fragments_allshipments, container, false);
        }

        public ArrayList<CompleteOrder> ShowItemList() {

            Order_List = new ArrayList<>();
            List<Db_CompleteOrder> list = Db_CompleteOrder.getAllAddress();
            for (int i = 0; i < list.size(); i++) {

                Db_CompleteOrder addDBReceiver = list.get(i);
                CompleteOrder completeOrder = new CompleteOrder();
                if (addDBReceiver.Order_Status.equals("P")) {
                    completeOrder.setDate(addDBReceiver.date);
                    completeOrder.setPickup_address(addDBReceiver.pickup_address);
                    completeOrder.setPickup_name(addDBReceiver.pickup_name);
                    completeOrder.setPickup_phone(addDBReceiver.pickup_phone);
                    completeOrder.setPickup_pincode(addDBReceiver.pickup_pincode);
                    completeOrder.setTime(addDBReceiver.time);
                    completeOrder.setOrder_Status(addDBReceiver.Order_Status);
                    completeOrder.setPaid(addDBReceiver.paid);
                    completeOrder.setTotal_cost(addDBReceiver.total_cost);
                    completeOrder.setDrop_address(addDBReceiver.drop_address);
                    completeOrder.setDrop_phone(addDBReceiver.drop_phone);
                    completeOrder.setDrop_name(addDBReceiver.drop_name);
                    completeOrder.setDrop_pincode(addDBReceiver.drop_pincode);
                    completeOrder.setCost(addDBReceiver.cost);
                    completeOrder.setImage_uri(addDBReceiver.image_uri);
                    completeOrder.setTracking_no(addDBReceiver.tracking_no);
                    completeOrder.setCategory(addDBReceiver.category);
                    completeOrder.setOrder_id(addDBReceiver.Order_id);

                    Order_List.add(completeOrder);
                }
            }
            Collections.reverse(Order_List);
            return Order_List;
        }

        @Override
        public void onResume() {
            super.onResume();
            order_data = ShowItemList();
            mAdapter = new Shipments_adapter(getActivity(), R.layout.list_item_allshipments, order_data);
            AllShipmentListView.setAdapter(mAdapter);
            AllShipmentListView.invalidateViews();
            mAdapter.notifyDataSetChanged();
            if (!ImageLoader.getInstance().isInited())
                ImageLoader.getInstance().init(config);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            AllShipmentListView = (ListView) view.findViewById(R.id.allshipment_list);
            order_data = ShowItemList();
            config = new ImageLoaderConfiguration.Builder(getActivity()).defaultDisplayImageOptions(defaultOptions).build();
            mAdapter = new Shipments_adapter(getActivity(), R.layout.list_item_allshipments, order_data);
            AllShipmentListView.setAdapter(mAdapter);
            AllShipmentListView.invalidateViews();
            mAdapter.notifyDataSetChanged();
            AllShipmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getActivity(), Activity_Shipment_details.class);
                    i.putExtra("tracking_no", Order_List.get(position).getTracking_no());
                    i.putExtra("imageURI", Order_List.get(position).getImage_uri());
                    SimpleDateFormat sdf = new SimpleDateFormat("h:m a, d MMMM yyyy", Locale.US);
                    if (Order_List.get(position).getDate() != null)
                        i.putExtra("datetime", sdf.format(Order_List.get(position).getDate()));
                    startActivity(i);
                    getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                }
            });


            if (!ImageLoader.getInstance().isInited())
                ImageLoader.getInstance().init(config);

        }

    }

    public static class Cancelled_Fragment extends Fragment {

        ListView AllShipmentListView;
        Shipments_adapter mAdapter;
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config;
        private ArrayList<CompleteOrder> order_data;
        private ArrayList<CompleteOrder> Order_List;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            return getActivity().getLayoutInflater().inflate(R.layout.fragments_allshipments, container, false);
        }

        public ArrayList<CompleteOrder> ShowItemList() {

            Order_List = new ArrayList<>();
            List<Db_CompleteOrder> list = Db_CompleteOrder.getAllAddress();
            for (int i = 0; i < list.size(); i++) {
                Db_CompleteOrder addDBReceiver = list.get(i);
                CompleteOrder completeOrder = new CompleteOrder();
                if (addDBReceiver.Order_Status.equals("CA")) {
                    completeOrder.setDate(addDBReceiver.date);
                    completeOrder.setPickup_address(addDBReceiver.pickup_address);
                    completeOrder.setPickup_name(addDBReceiver.pickup_name);
                    completeOrder.setPickup_phone(addDBReceiver.pickup_phone);
                    completeOrder.setPickup_pincode(addDBReceiver.pickup_pincode);
                    completeOrder.setTime(addDBReceiver.time);
                    completeOrder.setOrder_Status(addDBReceiver.Order_Status);
                    completeOrder.setPaid(addDBReceiver.paid);
                    completeOrder.setTotal_cost(addDBReceiver.total_cost);
                    completeOrder.setDrop_address(addDBReceiver.drop_address);
                    completeOrder.setDrop_phone(addDBReceiver.drop_phone);
                    completeOrder.setDrop_name(addDBReceiver.drop_name);
                    completeOrder.setDrop_pincode(addDBReceiver.drop_pincode);
                    completeOrder.setCost(addDBReceiver.cost);
                    completeOrder.setImage_uri(addDBReceiver.image_uri);
                    completeOrder.setTracking_no(addDBReceiver.tracking_no);

                    completeOrder.setCategory(addDBReceiver.category);
                    completeOrder.setOrder_id(addDBReceiver.Order_id);

                    Order_List.add(completeOrder);
                }
            }
            Collections.reverse(Order_List);
            return Order_List;
        }

        @Override
        public void onResume() {
            super.onResume();
            order_data = ShowItemList();
            mAdapter = new Shipments_adapter(getActivity(), R.layout.list_item_allshipments, order_data);
            AllShipmentListView.setAdapter(mAdapter);
            AllShipmentListView.invalidateViews();
            mAdapter.notifyDataSetChanged();
            if (!ImageLoader.getInstance().isInited())
                ImageLoader.getInstance().init(config);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            AllShipmentListView = (ListView) view.findViewById(R.id.allshipment_list);
            config = new ImageLoaderConfiguration.Builder(getActivity()).defaultDisplayImageOptions(defaultOptions).build();
            order_data = ShowItemList();
            mAdapter = new Shipments_adapter(getActivity(), R.layout.list_item_allshipments, order_data);
            mAdapter.notifyDataSetChanged();
            AllShipmentListView.setAdapter(mAdapter);
            AllShipmentListView.invalidateViews();
            AllShipmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getActivity(), Activity_Shipment_details.class);
                    i.putExtra("tracking_no", Order_List.get(position).getTracking_no());
                    i.putExtra("imageURI", Order_List.get(position).getImage_uri());
                    SimpleDateFormat sdf = new SimpleDateFormat("h:m a, d MMMM yyyy", Locale.US);
                    if (Order_List.get(position).getDate() != null)
                        i.putExtra("datetime", sdf.format(Order_List.get(position).getDate()));
                    startActivity(i);
                    getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                }
            });

            if (!ImageLoader.getInstance().isInited())
                ImageLoader.getInstance().init(config);

        }
    }

    public static class Completed_Fragment extends Fragment {

        ListView AllShipmentListView;
        Shipments_adapter mAdapter;
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config;
        private ArrayList<CompleteOrder> order_data;
        private ArrayList<CompleteOrder> Order_List;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            return getActivity().getLayoutInflater().inflate(R.layout.fragments_allshipments, container, false);
        }

        public ArrayList<CompleteOrder> ShowItemList() {

            Order_List = new ArrayList<>();
            List<Db_CompleteOrder> list = Db_CompleteOrder.getAllAddress();
            for (int i = 0; i < list.size(); i++) {
                Db_CompleteOrder addDBReceiver = list.get(i);
                CompleteOrder completeOrder = new CompleteOrder();
                if (addDBReceiver.Order_Status.equals("C")) {
                    completeOrder.setDate(addDBReceiver.date);
                    completeOrder.setPickup_address(addDBReceiver.pickup_address);
                    completeOrder.setPickup_name(addDBReceiver.pickup_name);
                    completeOrder.setPickup_phone(addDBReceiver.pickup_phone);
                    completeOrder.setPickup_pincode(addDBReceiver.pickup_pincode);
                    completeOrder.setTime(addDBReceiver.time);
                    completeOrder.setOrder_Status(addDBReceiver.Order_Status);
                    completeOrder.setPaid(addDBReceiver.paid);
                    completeOrder.setTotal_cost(addDBReceiver.total_cost);
                    completeOrder.setDrop_address(addDBReceiver.drop_address);
                    completeOrder.setDrop_phone(addDBReceiver.drop_phone);
                    completeOrder.setDrop_name(addDBReceiver.drop_name);
                    completeOrder.setDrop_pincode(addDBReceiver.drop_pincode);
                    completeOrder.setCost(addDBReceiver.cost);
                    completeOrder.setImage_uri(addDBReceiver.image_uri);
                    completeOrder.setTracking_no(addDBReceiver.tracking_no);
                    completeOrder.setCategory(addDBReceiver.category);
                    completeOrder.setOrder_id(addDBReceiver.Order_id);

                    Order_List.add(completeOrder);
                }
            }
            Collections.reverse(Order_List);
            return Order_List;
        }

        @Override
        public void onResume() {
            super.onResume();
            order_data = ShowItemList();
            mAdapter = new Shipments_adapter(getActivity(), R.layout.list_item_allshipments, order_data);
            AllShipmentListView.setAdapter(mAdapter);
            AllShipmentListView.invalidateViews();
            mAdapter.notifyDataSetChanged();
            if (!ImageLoader.getInstance().isInited())
                ImageLoader.getInstance().init(config);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            AllShipmentListView = (ListView) view.findViewById(R.id.allshipment_list);
            config = new ImageLoaderConfiguration.Builder(getActivity()).defaultDisplayImageOptions(defaultOptions).build();
            order_data = ShowItemList();
            mAdapter = new Shipments_adapter(getActivity(), R.layout.list_item_allshipments, order_data);
            mAdapter.notifyDataSetChanged();
            AllShipmentListView.setAdapter(mAdapter);
            AllShipmentListView.invalidateViews();
            AllShipmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getActivity(), Activity_Shipment_details.class);
                    i.putExtra("tracking_no", Order_List.get(position).getTracking_no());
                    Log.i("tracking_no", Order_List.get(position).getTracking_no());
                    i.putExtra("imageURI",  Order_List.get(position).getImage_uri());
                    SimpleDateFormat sdf = new SimpleDateFormat("h:m a, d MMMM yyyy", Locale.US);
                    if (Order_List.get(position).getDate() != null)
                        i.putExtra("datetime", sdf.format(Order_List.get(position).getDate()));
                    startActivity(i);
                    getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                }
            });

            if (!ImageLoader.getInstance().isInited())
                ImageLoader.getInstance().init(config);

        }
    }

    public class TabsAdapter extends FragmentPagerAdapter implements
            TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final TabHost mTabHost;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        public TabsAdapter(FragmentActivity activity, TabHost tabHost,
                           ViewPager pager) {
            super(getChildFragmentManager());
            mContext = activity;
            mTabHost = tabHost;
            mViewPager = pager;
            mTabHost.setOnTabChangedListener(this);
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
            TabWidget widget = mTabHost.getTabWidget();

            LinearLayout ll = (LinearLayout) widget.getParent();
            HorizontalScrollView hs = new HorizontalScrollView(getActivity());
            hs.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT));
            ll.addView(hs, 0);
            ll.removeView(widget);
            hs.addView(widget);
            hs.setHorizontalScrollBarEnabled(false);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new TabFactory(mContext));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);
            mTabs.add(info);
            mTabHost.addTab(tabSpec);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(), null);
        }

        public void onTabChanged(String tabId) {
            int position = mTabHost.getCurrentTab();
            mViewPager.setCurrentItem(position);
        }

        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {

            TabWidget widget = mTabHost.getTabWidget();

            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mTabHost.setCurrentTab(position);
            widget.setDescendantFocusability(oldFocusability);
        }

        public void onPageScrollStateChanged(int state) {
        }

        final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        class TabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public TabFactory(Context context) {
                mContext = context;
            }

            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(100);
                v.setMinimumHeight(0);
                return v;
            }
        }
    }

    public static class Shipments_Holder {
        private TextView Name;
        private TextView Address;
        private TextView Status;
        private TextView Date;
        private ImageView ItemImage;
    }

    public static class Shipments_adapter extends ArrayAdapter<CompleteOrder> {
        private Context c;
        private List<CompleteOrder> order_list;

        public Shipments_adapter(Context context, int resource, List<CompleteOrder> objects) {
            super(context, resource, objects);
            this.c = context;
            this.order_list = objects;
        }


        @Override
        public void add(CompleteOrder object) {
            super.add(object);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return order_list.size();
        }

        @Override
        public CompleteOrder getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public int getPosition(CompleteOrder item) {
            return super.getPosition(item);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Shipments_Holder mshipments_holder;
            if (convertView == null) {
                mshipments_holder = new Shipments_Holder();
                LayoutInflater inflater = (LayoutInflater) c
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_allshipments, parent, false);
                mshipments_holder.Name = (TextView) convertView.findViewById(R.id.tvShipmentname);
                mshipments_holder.Address = (TextView) convertView.findViewById(R.id.tvShipmentaddress);
                mshipments_holder.Status = (TextView) convertView.findViewById(R.id.bshipmentstatus);
                mshipments_holder.ItemImage = (ImageView) convertView.findViewById(R.id.ivItem_Image);
                mshipments_holder.Date = (TextView) convertView.findViewById(R.id.tvDate);
                convertView.setTag(mshipments_holder);
            } else {
                mshipments_holder = (Shipments_Holder) convertView.getTag();
            }

            if (order_list.get(position).getImage_uri().equals("http://sendmates.com/static")) {
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.box_sample_icon, mshipments_holder.ItemImage);
            } else {
                if (order_list.get(position).getImage_uri().contains("sendmates") || order_list.get(position).getImage_uri().contains("128.199.159.90")) {
                    ImageLoader.getInstance().displayImage(order_list.get(position).getImage_uri(), mshipments_holder.ItemImage);
                } else {
                    ImageLoader.getInstance().displayImage("file://" + order_list.get(position).getImage_uri(), mshipments_holder.ItemImage);
                }
            }

            mshipments_holder.Name.setText(order_list.get(position).getDrop_name());
            mshipments_holder.Address.setText(order_list.get(position).getDrop_address());
            SimpleDateFormat sdf = new SimpleDateFormat("d - LLL", Locale.US);
            if (order_list.get(position).getDate() == null) {
                mshipments_holder.Date.setText("");

            } else {
                mshipments_holder.Date.setText(sdf.format(order_list.get(position).getDate()));
            }
            mshipments_holder.Status.setText("Tracking Id: " + order_list.get(position).getTracking_no());
            return convertView;
        }
    }
}
