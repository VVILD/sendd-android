package co.sendd.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.sendd.R;
import co.sendd.databases.Db_CompleteOrder;
import co.sendd.gettersandsetters.ShipmentDetails;
import co.sendd.gettersandsetters.TrackingDataList;
import co.sendd.helper.NetworkUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Activity_Shipment_details extends BaseActivity {

    TextView Name, Address, Time, Bill;
    ImageView ItemImage;
    ProgressDialog pd;
    ArrayList<TrackingDataList> list = new ArrayList<>(1);
    ListView TrackingListView;
    Tracking_adapter adapter;
    ScrollView scrollView;

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_shipment_details);
        scrollView = (ScrollView) findViewById(R.id.scroll);
        TrackingListView = (ListView) findViewById(R.id.ItemTrackingList);
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        Name = (TextView) findViewById(R.id.tvName);
        Address = (TextView) findViewById(R.id.tvAddress);
        Time = (TextView) findViewById(R.id.tvTime);
        Bill = (TextView) findViewById(R.id.tvRate);

        ItemImage = (ImageView) findViewById(R.id.ivItem_Image_preview);
        Log.i("TrackingNo",getIntent().getStringExtra("tracking_no"));
        try {
            if (getIntent().getStringExtra("imageURI").equals("http://128.199.159.90/static")) {
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.box_sample_icon, ItemImage);
            } else {
                if (getIntent().getStringExtra("imageURI").contains("sendmates")||getIntent().getStringExtra("imageURI").contains("128.199.159.90")) {
                    ImageLoader.getInstance().displayImage(getIntent().getStringExtra("imageURI"), ItemImage);

                } else {
                    ImageLoader.getInstance().displayImage("file://" + getIntent().getStringExtra("imageURI"), ItemImage);

                }
            }
        } catch (NullPointerException e) {

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.Main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_icon_small);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Activity_Shipment_details.this, Activity_Main.class);
                        i.putExtra("openpreviousBooking", true);
                        Activity_Shipment_details.this.startActivity(i);
                        finish();
                        Activity_Shipment_details.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
                    }
                }
        );
        final NetworkUtils mnetworkutils = new NetworkUtils(Activity_Shipment_details.this);
        if (mnetworkutils.isnetconnected()) {
            pd = new ProgressDialog(Activity_Shipment_details.this);
            pd.setMessage("Loading Details, Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
            mnetworkutils.getapi().getShipmentDetails(getIntent().getStringExtra("tracking_no"), new Callback<ShipmentDetails>() {
                @Override
                public void success(ShipmentDetails shipmentDetails, Response response) {
                    try {
                        if (shipmentDetails.getStatus().equals("C")) {
                            Db_CompleteOrder completeOrder_DB = new Db_CompleteOrder();
                            completeOrder_DB.updateStatus(getIntent().getStringExtra("imageURI"), shipmentDetails.getStatus());
                        }
                        if (shipmentDetails.getStatus().equals("CA")) {
                            Db_CompleteOrder completeOrder_DB = new Db_CompleteOrder();
                            completeOrder_DB.updateStatus(getIntent().getStringExtra("imageURI"), shipmentDetails.getStatus());
                        }
                        if (shipmentDetails.getDrop_name() != null)
                            Name.setText(shipmentDetails.getDrop_name());
                        else
                            Name.setText(" ");
                        if (!shipmentDetails.getDrop_address().contains("None,None"))
                            Address.setText(shipmentDetails.getDrop_address());
                        else
                            Address.setText(" ");

                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa");
                        Date dt = new Date();
                        if (shipmentDetails.getTime() != null) {
                            try {
                                dt = sdf.parse(shipmentDetails.getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        Time.setText("Time: " + sdf2.format(dt) + "  Date: " + shipmentDetails.getDate());
                        if (shipmentDetails.getPrice() == null) {
                            Bill.setText("Total Bill Rs. - Not yet Generated");
                        } else
                            Bill.setText("Total Bill Rs." + shipmentDetails.getPrice());
                        try {
                            if (shipmentDetails.getTracking_data() != null) {
                                JSONArray arr = new JSONArray(shipmentDetails.getTracking_data());
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject c = (JSONObject) arr.get(i);
                                    list.add(updateList(c));
                                }
                            }
                            adapter = new Tracking_adapter(Activity_Shipment_details.this, R.layout.list_item_allshipments, list);
                            TrackingListView.setAdapter(adapter);
                            setListViewHeightBasedOnChildren(TrackingListView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pd.dismiss();

                    } catch (RuntimeException ee) {
                        pd.dismiss();

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("error",error.toString());
                    Toast.makeText(Activity_Shipment_details.this, "Error in connection please try again in some time.", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Activity_Shipment_details.this, Activity_Main.class);
        i.putExtra("openpreviousBooking", true);
        this.startActivity(i);
        finish(); // finish activity
        Activity_Shipment_details.this.overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
    }

    public TrackingDataList updateList(JSONObject c) {
        TrackingDataList trackingDataList = new TrackingDataList();
        try {
            trackingDataList.setDate(c.getString("date"));
            trackingDataList.setStatus(c.getString("status"));
            trackingDataList.setLoaction(c.getString("location"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trackingDataList;
    }

    public class Tracking_holder {
        private TextView Date;
        private TextView Status;
        private TextView Location;
    }

    public class Tracking_adapter extends ArrayAdapter<TrackingDataList> {
        private Context c;
        private List<TrackingDataList> trackingList;

        public Tracking_adapter(Context context, int resource, List<TrackingDataList> objects) {
            super(context, resource, objects);
            this.c = context;
            this.trackingList = objects;
        }

        @Override
        public void add(TrackingDataList object) {
            super.add(object);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            scrollView.fullScroll(View.FOCUS_UP);
        }

        @Override
        public int getCount() {
            return trackingList.size();
        }

        @Override
        public TrackingDataList getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public int getPosition(TrackingDataList item) {
            return super.getPosition(item);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Tracking_holder tracking_holder;
            if (convertView == null) {
                tracking_holder = new Tracking_holder();
                LayoutInflater inflater = (LayoutInflater) c
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_tracking_item, parent, false);
                tracking_holder.Date = (TextView) convertView.findViewById(R.id.tvDate1);
                tracking_holder.Status = (TextView) convertView.findViewById(R.id.tvStatus1);
                tracking_holder.Location = (TextView) convertView.findViewById(R.id.tvLocation1);

                convertView.setTag(tracking_holder);
            } else {
                tracking_holder = (Tracking_holder) convertView.getTag();
            }
            tracking_holder.Date.setText(trackingList.get(position).getDate());
            tracking_holder.Status.setText(trackingList.get(position).getStatus());
            tracking_holder.Location.setText(trackingList.get(position).getLoaction());

            return convertView;
        }
    }

}
