package co.sendd.fragments;


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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import co.sendd.activity.Activity_Main;
import co.sendd.activity.Activity_Shipment_details;
import co.sendd.databases.Db_CompleteOrder;
import co.sendd.gettersandsetters.CompleteOrder;
import co.sendd.helper.Utils;
import co.sendd.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kuku on 11/03/15.
 */
public class Fragment_MyShipments extends Fragment {
    private TabHost mTabHost;
    private ViewPager mViewPager;
    private TabsAdapter mTabsAdapter;
    public Context c = getActivity();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity_Main.exit = false;
        Activity_Main.noShipmentTv.setVisibility(View.GONE);

        mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
        mTabHost.setup();
        ((Activity_Main) getActivity()).setActionBarTitle("Previous Shipments");
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mTabsAdapter = new TabsAdapter(getActivity(), mTabHost, mViewPager);
        if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
            Activity_Main.mProgress.dismiss();
        }
        mTabsAdapter.addTab(mTabHost.newTabSpec("ALL").setIndicator("ALL"),
                All_Fragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("UPCOMING").setIndicator("UPCOMING"),
                Upcoming_Fragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("COMPLETED").setIndicator("COMPLETED"),
                Completed_Fragment.class, null);

        mTabHost.setBackgroundColor(getActivity().getResources().getColor(R.color.white));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentview = getActivity().getLayoutInflater().inflate(R.layout.fragment_myshipments, container, false);

        return contentview;
    }

    public void onResume(){
        super.onResume();
        Activity_Main.exit = false;
        Activity_Main.noShipmentTv.setVisibility(View.GONE);
         mTabHost.setup();
        ((Activity_Main) getActivity()).setActionBarTitle("Previous Shipments");
         if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
            Activity_Main.mProgress.dismiss();
        }
    }

    public class TabsAdapter extends FragmentPagerAdapter implements
            TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final TabHost mTabHost;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

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

        class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabsAdapter(FragmentActivity activity, TabHost tabHost,
                           ViewPager pager) {
            super(getChildFragmentManager());
            mContext = activity;
            mTabHost = tabHost;
            mViewPager = pager;
            mTabHost.setOnTabChangedListener(this);
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mContext));
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


            Log.d("the frag item called", String.valueOf(position));
            return Fragment.instantiate(mContext, info.clss.getName(),
                    null);


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
    }

    public static class All_Fragment extends Fragment {
        ListView AllShipmentListView;
        AllShipments_adapter mAdapter;
        private ArrayList<CompleteOrder> order_data;
        private ArrayList<CompleteOrder> Order_List;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View contentview = getActivity().getLayoutInflater().inflate(R.layout.fragments_allshipments, container, false);
            return contentview;
        }

        public ArrayList<CompleteOrder> ShowItemList() {

            Utils utils = new Utils(getActivity());
            Db_CompleteOrder newItem = new Db_CompleteOrder();
            Order_List = new ArrayList<CompleteOrder>();
            List<Db_CompleteOrder> list = newItem.getAllAddress();
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
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            AllShipmentListView = (ListView) view.findViewById(R.id.allshipment_list);
            order_data = ShowItemList();
            mAdapter = new AllShipments_adapter(getActivity(), R.layout.list_item_allshipments, order_data);
            AllShipmentListView.setAdapter(mAdapter);
            AllShipmentListView.invalidateViews();
            mAdapter.notifyDataSetChanged();
            AllShipmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getActivity(), Activity_Shipment_details.class);
                    i.putExtra("tracking_no",Integer.parseInt(Order_List.get(position).getTracking_no()));
                    i.putExtra("imageURI",Order_List.get(position).getImage_uri());
                    SimpleDateFormat sdf = new SimpleDateFormat("h:m a, d MMMM yyyy", Locale.US);
                    i.putExtra("datetime",sdf.format(Order_List.get(position).getDate()));
                    startActivity(i);
                    getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                }
            });

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).defaultDisplayImageOptions(defaultOptions).build();
            ImageLoader.getInstance().init(config);

        }

        public static  class AllShipments_Holder {
            private TextView Name;
            private TextView Address;
            private TextView Status;
            private TextView Date;
            private ImageView ItemImage;
        }

        public class AllShipments_adapter extends ArrayAdapter<CompleteOrder> {
            private Context c;
            private List<CompleteOrder> order_list;

            public AllShipments_adapter(Context context, int resource, List<CompleteOrder> objects) {
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
                AllShipments_Holder mshipments_holder;
                if (convertView == null) {
                    mshipments_holder = new AllShipments_Holder();
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
                    mshipments_holder = (AllShipments_Holder) convertView.getTag();
                }
                ImageLoader.getInstance().displayImage("file://" + order_list.get(position).getImage_uri(), mshipments_holder.ItemImage);

                mshipments_holder.Name.setText(order_list.get(position).getDrop_name());
                mshipments_holder.Address.setText(order_list.get(position).getDrop_address());
                SimpleDateFormat sdf = new SimpleDateFormat("d - LLL", Locale.US);
                mshipments_holder.Date.setText(sdf.format(order_list.get(position).getDate()));
                mshipments_holder.Status.setText("Tracking Id: "+order_list.get(position).getTracking_no());


                return convertView;
            }


        }
    }

    public static class Upcoming_Fragment extends Fragment {
        ListView AllShipmentListView;
        UpcomingShipments_adapter mAdapter;
        private ArrayList<CompleteOrder> order_data;
        private ArrayList<CompleteOrder> Order_List;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View contentview = getActivity().getLayoutInflater().inflate(R.layout.fragments_allshipments, container, false);
            return contentview;
        }

        public ArrayList<CompleteOrder> ShowItemList() {

            Utils utils = new Utils(getActivity());
            Db_CompleteOrder newItem = new Db_CompleteOrder();
            Order_List = new ArrayList<CompleteOrder>();
            List<Db_CompleteOrder> list = newItem.getAllAddress();
            for (int i = 0; i < list.size(); i++) {

                Db_CompleteOrder addDBReceiver = list.get(i);
                CompleteOrder completeOrder = new CompleteOrder();
                if(addDBReceiver.Order_Status.equals("P")){
                    completeOrder.setDate(addDBReceiver.date);
                    completeOrder.setPickup_address(addDBReceiver.pickup_address);
                    completeOrder.setPickup_name(addDBReceiver.pickup_name);
                    completeOrder.setPickup_phone(addDBReceiver.pickup_phone);
                    completeOrder.setPickup_pincode(addDBReceiver.pickup_pincode);
                    completeOrder.setTime(addDBReceiver.time);
                    completeOrder.setOrder_Status(addDBReceiver.Order_Status);
                    completeOrder.setPaid(addDBReceiver.paid);
                    completeOrder.setTotal_cost(addDBReceiver.total_cost);
                    completeOrder.setDrop_address( addDBReceiver.drop_address);
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
        public void onResume(){
            super.onResume();
            order_data = ShowItemList();
            mAdapter = new UpcomingShipments_adapter(getActivity(), R.layout.list_item_allshipments, order_data);
            AllShipmentListView.setAdapter(mAdapter);
            AllShipmentListView.invalidateViews();
            mAdapter.notifyDataSetChanged();
        }
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            AllShipmentListView = (ListView) view.findViewById(R.id.allshipment_list);
            order_data = ShowItemList();
            mAdapter = new UpcomingShipments_adapter(getActivity(), R.layout.list_item_allshipments, order_data);
            AllShipmentListView.setAdapter(mAdapter);
            AllShipmentListView.invalidateViews();
            mAdapter.notifyDataSetChanged();
            AllShipmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getActivity(), Activity_Shipment_details.class);
                    i.putExtra("tracking_no",Integer.parseInt(Order_List.get(position).getTracking_no()));
                    i.putExtra("imageURI",Order_List.get(position).getImage_uri());
                    SimpleDateFormat sdf = new SimpleDateFormat("h:m a, d MMMM yyyy", Locale.US);
                    i.putExtra("datetime",sdf.format(Order_List.get(position).getDate()));
                    startActivity(i);
                    getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                }
            });

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).defaultDisplayImageOptions(defaultOptions).build();
            ImageLoader.getInstance().init(config);

        }

        public class AllShipments_Holder {
            private TextView Name;
            private TextView Address;
            private TextView Status;
            private TextView Date;
            private ImageView ItemImage;
        }

        public class UpcomingShipments_adapter extends ArrayAdapter<CompleteOrder> {
            private Context c;
            private List<CompleteOrder> order_list;

            public UpcomingShipments_adapter(Context context, int resource, List<CompleteOrder> objects) {
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
                AllShipments_Holder mshipments_holder;
                if (convertView == null) {
                    mshipments_holder = new AllShipments_Holder();
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
                    mshipments_holder = (AllShipments_Holder) convertView.getTag();
                }
                ImageLoader.getInstance().displayImage("file://" + order_list.get(position).getImage_uri(), mshipments_holder.ItemImage);

                mshipments_holder.Name.setText(order_list.get(position).getDrop_name());
                mshipments_holder.Address.setText(order_list.get(position).getDrop_address());
                SimpleDateFormat sdf = new SimpleDateFormat("d - LLL", Locale.US);
                mshipments_holder.Date.setText(sdf.format(order_list.get(position).getDate()));
                mshipments_holder.Status.setText("Tracking Id: "+order_list.get(position).getTracking_no());
                mAdapter.notifyDataSetChanged();
                return convertView;
            }


        }

    }

    public static class Completed_Fragment extends Fragment {

        ListView AllShipmentListView;
        CompletedShipments_adapter mAdapter;
        private ArrayList<CompleteOrder> order_data;
        private ArrayList<CompleteOrder> Order_List;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View contentview = getActivity().getLayoutInflater().inflate(R.layout.fragments_allshipments, container, false);
            return contentview;
        }

        public ArrayList<CompleteOrder> ShowItemList() {

            Utils utils = new Utils(getActivity());
            Db_CompleteOrder newItem = new Db_CompleteOrder();
            Order_List = new ArrayList<>();
            List<Db_CompleteOrder> list = newItem.getAllAddress();
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
        public void onResume(){
            super.onResume();
            order_data = ShowItemList();
            mAdapter = new CompletedShipments_adapter(getActivity(), R.layout.list_item_allshipments, order_data);
            AllShipmentListView.setAdapter(mAdapter);
            AllShipmentListView.invalidateViews();
            mAdapter.notifyDataSetChanged();
        }
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            AllShipmentListView = (ListView) view.findViewById(R.id.allshipment_list);
            order_data = ShowItemList();
            mAdapter = new CompletedShipments_adapter(getActivity(), R.layout.list_item_allshipments, order_data);
            mAdapter.notifyDataSetChanged();
            AllShipmentListView.setAdapter(mAdapter);
            AllShipmentListView.invalidateViews();
            AllShipmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getActivity(), Activity_Shipment_details.class);
                    i.putExtra("tracking_no", Integer.parseInt(Order_List.get(position).getTracking_no()));
                    i.putExtra("imageURI", Order_List.get(position).getImage_uri());
                    SimpleDateFormat sdf = new SimpleDateFormat("h:m a, d MMMM yyyy", Locale.US);
                    i.putExtra("datetime", sdf.format(Order_List.get(position).getDate()));
                    startActivity(i);
                    getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                }
            });

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).defaultDisplayImageOptions(defaultOptions).build();
            ImageLoader.getInstance().init(config);

        }

        public static class AllShipments_Holder {
            private TextView Name;
            private TextView Address;
            private TextView Status;
            private TextView Date;
            private ImageView ItemImage;
        }

        public class CompletedShipments_adapter extends ArrayAdapter<CompleteOrder> {
            private Context c;
            private List<CompleteOrder> order_list;

            public CompletedShipments_adapter(Context context, int resource, List<CompleteOrder> objects) {
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
                AllShipments_Holder mshipments_holder;
                if (convertView == null) {
                    mshipments_holder = new AllShipments_Holder();
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
                    mshipments_holder = (AllShipments_Holder) convertView.getTag();
                }
                ImageLoader.getInstance().displayImage("file://" + order_list.get(position).getImage_uri(), mshipments_holder.ItemImage);

                mshipments_holder.Name.setText(order_list.get(position).getDrop_name());
                mshipments_holder.Address.setText(order_list.get(position).getDrop_address());
                SimpleDateFormat sdf = new SimpleDateFormat("d - LLL", Locale.US);
                mshipments_holder.Date.setText(sdf.format(order_list.get(position).getDate()));
                mshipments_holder.Status.setText("Tracking Id: " + order_list.get(position).getTracking_no());
                mAdapter.notifyDataSetChanged();
                return convertView;
            }


        }
    }
}
