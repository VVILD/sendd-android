package co.sendd.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import co.sendd.activity.Activity_Address_Search_Receiver;
import co.sendd.activity.Activity_Main;
import co.sendd.activity.Activity_Maps;
import co.sendd.activity.Activity_Shipping_Options;
import co.sendd.activity.Activity_ThankYou;
import co.sendd.activity.Activity_UpdateAddress;
import co.sendd.databases.Db_CompleteOrder;
import co.sendd.databases.Db_Item_List;
import co.sendd.gettersandsetters.CompleteOrder;
import co.sendd.gettersandsetters.ItemList;
import co.sendd.gettersandsetters.Orders;
import co.sendd.gettersandsetters.Shipment;
import co.sendd.helper.NetworkUtils;
import co.sendd.helper.Utils;
import co.sendd.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by Kuku on 12/02/15.
 */
public class Fragment_Orders extends Fragment {
    private ListView lv_ItemCart;
    private OrderList_Adapter madapter;
    private ArrayList<ItemList> Item_list;
    private ProgressDialog pd;
    private static
    Uri fileUri;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Utils utils;
    RelativeLayout rl;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_orders, container, false);

    }

    @Override
    public void onResume() {
        super.onResume();
        madapter = new OrderList_Adapter(getActivity(), R.layout.list_item_shipmentitem, ShowItemList());
        lv_ItemCart.setAdapter(madapter);
        if (madapter.getCount() > 0) {
            Activity_Main.noShipmentTv.setVisibility(View.INVISIBLE);
        } else {
            Activity_Main.noShipmentTv.setVisibility(View.VISIBLE);
        }
        utils = new Utils(getActivity());
        if (utils.getvalue("PickupAddress") == null) {
            ((Activity_Main) getActivity()).setActionBarTitle("Sendd");
        } else {
            rl.setVisibility(View.GONE);
            ((Activity_Main) getActivity()).setActionBarTitle(utils.getvalue("PickupFlatNo") + ", " + utils.getvalue("PickupAddress"));
        }
    }

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(final Date date) {

            if (!(date.getHours() < 10) && !(date.getHours() > 18)) {
                Log.i("asdfasdf", String.valueOf(date.getHours()));
                int hour = date.getHours();
                int minute = date.getMinutes();
                final String Time1 = String.valueOf(hour) + ":" + String.valueOf(minute) + ":00";

                pd = new ProgressDialog(getActivity());
                pd.setMessage("Loading, Please wait....");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                final NetworkUtils mnetworkutils = new NetworkUtils(getActivity());
                if (mnetworkutils.isnetconnected()) {
                    if (utils.getvalue("PickupAddress") != null) {
                        if (madapter.getCount() > 0) {
                            Orders orders = new Orders();
                            final Utils utils = new Utils(getActivity());
                            orders.setUser(utils.getvalue("RegisteredPhone"));
                            orders.setDate(date);
                            orders.setTime(Time1);
                            orders.setAddress(utils.getvalue("PickupAddress"));
                            orders.setFlat_no(utils.getvalue("PickupFlatNo"));
                            orders.setLatitude(Double.parseDouble(utils.getvalue("Lat")));
                            orders.setLongitude(Double.parseDouble(utils.getvalue("Longi")));
                            orders.setPincode(utils.getvalue("PickupPincode"));
                            pd.show();
                            // Log.i("qwertyuiopajklzxcvbnm,", orders.getUser() + orders.getDate() + orders.getTime() + orders.getPickup_flat_no() + orders.getPickup_locality() + orders.getPickup_city() + orders.getPickup_pincode() + orders.getPickup_state() + orders.getPickup_country());
                            final CompleteOrder completeOrder = new CompleteOrder();
                            mnetworkutils.getapi().order(orders, new Callback<Orders>() {
                                @Override
                                public void success(final Orders orders, Response response) {
                                    if (pd != null && pd.isShowing()) {

                                        completeOrder.setDate(date);
                                        completeOrder.setPickup_address(orders.getAddress());
                                        completeOrder.setPickup_name(utils.getvalue("S_Name"));
                                        completeOrder.setPickup_phone(orders.getUser());
                                        completeOrder.setPickup_pincode(orders.getPincode());
                                        completeOrder.setTime(Time1);
                                        completeOrder.setOrder_Status(orders.getStatus());
                                        completeOrder.setPaid(orders.getPaid());
                                        completeOrder.setTotal_cost(orders.getCost());

                                        Log.i("OrderNumber Generated =", orders.getOrder_no());
                                        madapter.getCount();
                                        Db_Item_List newItem = new Db_Item_List();
                                        Item_list = new ArrayList<>();
                                        List<Db_Item_List> list = newItem.getAllItems(String.valueOf(utils.getOrderId()));

                                        for (int i = 0; i < madapter.getCount(); i++) {
                                            Db_Item_List addDBReceiver = list.get(i);
                                            String cat = "S";

                                            if (addDBReceiver.setshippingoption.equals("Standard")) {
                                                cat = "S";
                                            } else if (addDBReceiver.setshippingoption.equals("Bulk")) {
                                                cat = "E";
                                            } else if (addDBReceiver.setshippingoption.equals("Set Shipping Option") ||addDBReceiver.setshippingoption.equals("Premium")) {
                                                cat = "P";
                                            }
                                            final Uri imageuri = Uri.parse(addDBReceiver.image_uri);

                                            Log.i("qweqwertyui", imageuri.getPath() + "   " + addDBReceiver.name + "   " + addDBReceiver.phone + "   " + addDBReceiver.flat_no + "   " + addDBReceiver.locality + "   " + addDBReceiver.city + "   " + addDBReceiver.state + "   " + addDBReceiver.country + "   " + addDBReceiver.pincode + "   " + orders.getOrder_no() + "   " + cat);
                                            mnetworkutils.getapi().shipment(new TypedFile("image/jpeg", new File(imageuri.getPath())), addDBReceiver.name, addDBReceiver.phone, addDBReceiver.flat_no, addDBReceiver.locality, addDBReceiver.city, addDBReceiver.state, addDBReceiver.country, addDBReceiver.pincode, orders.getOrder_no(), cat, new Callback<Shipment>() {

                                                @Override
                                                public void success(Shipment shipment, Response response) {

                                                    completeOrder.setDrop_address(shipment.getDrop_flat_no() + ", " + shipment.getDrop_locality() + shipment.getDrop_city() + ", " + shipment.getDrop_state() + ", " + shipment.getDrop_country());
                                                    completeOrder.setDrop_phone(shipment.getDrop_phone());
                                                    completeOrder.setDrop_name(shipment.getDrop_name());
                                                    completeOrder.setDrop_pincode(shipment.getDrop_pincode());
                                                    completeOrder.setCost(shipment.getCost());
                                                    completeOrder.setImage_uri(imageuri.getPath());
                                                    completeOrder.setTracking_no(shipment.getTracking_no());
                                                    completeOrder.setCategory(shipment.getCategory());
                                                    completeOrder.setOrder_id(orders.getOrder_no());
                                                    Db_CompleteOrder DBCO = new Db_CompleteOrder();
                                                    DBCO.AddToDB(completeOrder);
                                                    Log.i("Tracking no.Generated", shipment.getTracking_no());
                                                    Log.i("Category", shipment.getCategory());

                                                    pd.dismiss();
                                                    Intent i = new Intent(getActivity(), Activity_ThankYou.class);
                                                    i.putExtra("PickLater",true);
                                                    startActivity(i);
                                                    getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    Log.i("Error Adding Shipments", error.toString());
                                                    pd.dismiss();
                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    if (pd != null && pd.isShowing()) {
                                        pd.dismiss();
                                        Log.i("Error Adding Order", error.toString());
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Add Item", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Set your Pickup Address", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Connect to a working Internet Connection", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "We currently serve Between 10 AM to 6 PM", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        Activity_Main.exit = true;
        rl = (RelativeLayout) view.findViewById(R.id.setlocationrl);
        utils = new Utils(getActivity());
        if (utils.getvalue("PickupAddress") == null) {
            ((Activity_Main) getActivity()).setActionBarTitle("Sendd");

        } else {
            ((Activity_Main) getActivity()).setActionBarTitle(utils.getvalue("PickupFlatNo") + ", " + utils.getvalue("PickupAddress"));
            rl.setVisibility(View.GONE);
        }
        ((Activity_Main) getActivity()).showActionBar();

        //Initialize Universal ImageLoader
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);


        Button pickupNow = (Button) view.findViewById(R.id.bFragment_Orders_PickNow);
        Button pickupLater = (Button) view.findViewById(R.id.bFragment_Orders_PickLater);

        pickupNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date d = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                if (d.getHours() >= 10 && d.getHours()<= 18) {
                    Log.i("sda", String.valueOf(d.getHours()));
                    pd = new ProgressDialog(getActivity());
                    pd.setMessage("Loading, Please wait....");
                    pd.setCancelable(false);
                    pd.setIndeterminate(true);
                    final NetworkUtils mnetworkutils = new NetworkUtils(getActivity());
                    if (mnetworkutils.isnetconnected()) {
                        if (utils.getvalue("PickupAddress") != null) {
                            if (madapter.getCount() > 0) {
                                Orders orders = new Orders();
                                final Utils utils = new Utils(getActivity());

                                final String date = sdf.format(d); // Get
                                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                final String time = dateFormat.format(d);
                                orders.setUser(utils.getvalue("RegisteredPhone"));
                                orders.setDate(new Date());
                                orders.setTime(time);
                                orders.setAddress(utils.getvalue("PickupAddress"));
                                orders.setFlat_no(utils.getvalue("PickupFlatNo"));
                                orders.setLatitude(Double.parseDouble(utils.getvalue("Lat")));
                                orders.setLongitude(Double.parseDouble(utils.getvalue("Longi")));
                                orders.setPincode(utils.getvalue("PickupPincode"));
                                pd.show();
                                // Log.i("qwertyuiopajklzxcvbnm,", orders.getUser() + orders.getDate() + orders.getTime() + orders.getPickup_flat_no() + orders.getPickup_locality() + orders.getPickup_city() + orders.getPickup_pincode() + orders.getPickup_state() + orders.getPickup_country());
                                final CompleteOrder completeOrder = new CompleteOrder();
                                mnetworkutils.getapi().order(orders, new Callback<Orders>() {
                                    @Override
                                    public void success(final Orders orders, Response response) {
                                        if (pd != null && pd.isShowing()) {
                                            completeOrder.setPickup_address(orders.getAddress());
                                            completeOrder.setPickup_name(utils.getvalue("S_Name"));
                                            completeOrder.setPickup_phone(orders.getUser());
                                            completeOrder.setPickup_pincode(orders.getPincode());
                                            completeOrder.setOrder_Status(orders.getStatus());
                                            completeOrder.setPaid(orders.getPaid());
                                            completeOrder.setTotal_cost(orders.getCost());

                                            completeOrder.setDate(new Date());
                                            completeOrder.setTime(time);

                                            Log.i("OrderNumber Generated =", orders.getOrder_no());
                                            madapter.getCount();
                                            Db_Item_List newItem = new Db_Item_List();
                                            Item_list = new ArrayList<>();
                                            List<Db_Item_List> list = newItem.getAllItems(String.valueOf(utils.getOrderId()));

                                            for (int i = 0; i < madapter.getCount(); i++) {
                                                Db_Item_List addDBReceiver = list.get(i);
                                                String cat = "S";

                                                if ( addDBReceiver.setshippingoption.equals("Standard")) {
                                                    cat = "S";
                                                } else if (addDBReceiver.setshippingoption.equals("Bulk")) {
                                                    cat = "E";
                                                } else if (addDBReceiver.setshippingoption.equals("Set Shipping Option") ||addDBReceiver.setshippingoption.equals("Premium")) {
                                                    cat = "P";
                                                }
                                                final Uri imageuri = Uri.parse(addDBReceiver.image_uri);

                                                Log.i("qweqwertyui", imageuri.getPath() + "   " + addDBReceiver.name + "   " + addDBReceiver.phone + "   " + addDBReceiver.flat_no + "   " + addDBReceiver.locality + "   " + addDBReceiver.city + "   " + addDBReceiver.state + "   " + addDBReceiver.country + "   " + addDBReceiver.pincode + "   " + orders.getOrder_no() + "   " + cat);
                                                mnetworkutils.getapi().shipment(new TypedFile("image/jpeg", new File(imageuri.getPath())), addDBReceiver.name, addDBReceiver.phone, addDBReceiver.flat_no, addDBReceiver.locality, addDBReceiver.city, addDBReceiver.state, addDBReceiver.country, addDBReceiver.pincode, orders.getOrder_no(), cat, new Callback<Shipment>() {

                                                    @Override
                                                    public void success(Shipment shipment, Response response) {

                                                        completeOrder.setDrop_address(shipment.getDrop_flat_no() + ", " + shipment.getDrop_locality() + shipment.getDrop_city() + ", " + shipment.getDrop_state() + ", " + shipment.getDrop_country());
                                                        completeOrder.setDrop_phone(shipment.getDrop_phone());
                                                        completeOrder.setDrop_name(shipment.getDrop_name());
                                                        completeOrder.setDrop_pincode(shipment.getDrop_pincode());
                                                        completeOrder.setCost(shipment.getCost());
                                                        completeOrder.setImage_uri(imageuri.getPath());
                                                        completeOrder.setTracking_no(shipment.getTracking_no());
                                                        completeOrder.setCategory(shipment.getCategory());
                                                        completeOrder.setOrder_id(orders.getOrder_no());
                                                        Db_CompleteOrder DBCO = new Db_CompleteOrder();
                                                        DBCO.AddToDB(completeOrder);
                                                        Log.i("Tracking no.Generated", shipment.getTracking_no());
                                                        Log.i("Category", shipment.getCategory());

                                                        pd.dismiss();
                                                        Intent i = new Intent(getActivity(), Activity_ThankYou.class);
                                                        i.putExtra("PickLater",false);
                                                        startActivity(i);
                                                        getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                                                    }

                                                    @Override
                                                    public void failure(RetrofitError error) {
                                                        Log.i("Error Adding Shipments", error.toString());
                                                        pd.dismiss();
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        if (pd != null && pd.isShowing()) {
                                            pd.dismiss();
                                            Log.i("Error Adding Order", error.toString());
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), "Add Item", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Set your Pickup Address", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please Connect to a working Internet Connection", Toast.LENGTH_LONG).show();
                    }
                } else {
                    for (int i = 0; i < 2; i++) {
                        Toast.makeText(getActivity(), "We currently serve between 10 AM to 6 PM. Kindly use Pick Later option or book tomorrow.", Toast.LENGTH_LONG).show();
                    }
                 }
            }
        });

        pickupLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SlideDateTimePicker.Builder(getFragmentManager())
                        .setListener(listener)
                        .setMinDate(new Date())
                        .setIs24HourTime(true)
                        .setInitialDate(new Date())
                        .build()
                        .show();

            }
        });
        if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
            Activity_Main.mProgress.dismiss();

        }

        Button addItem = (Button) view.findViewById(R.id.bFragment_Orders_AddShipment);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), Activity_Address_Search_Receiver.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
            }
        });
        lv_ItemCart = (ListView) view.findViewById(R.id.lvFragment_Orders_Items_List);
        lv_ItemCart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                madapter.notifyDataSetChanged();
                lv_ItemCart.smoothScrollToPositionFromTop(position, 0);

            }
        });
        madapter = new OrderList_Adapter(getActivity(), R.layout.list_item_shipmentitem, ShowItemList());
        lv_ItemCart.setAdapter(madapter);
    }

    public class OrderCart_holder {
        private TextView reciever_name;
        private ImageView item_image;
        private TextView address_line1;
        private TextView address_line2;
        private ImageView cross;
        private ImageView edit;
        private Button shipping_option;
    }

    public class OrderList_Adapter extends ArrayAdapter<ItemList> {

        private Context c;
        private List<ItemList> Item_list;

        public OrderList_Adapter(Context context, int resource, List<ItemList> objects) {
            super(context, resource, objects);
            this.c = context;
            this.Item_list = objects;
        }


        @Override
        public void add(ItemList object) {
            super.add(object);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            lv_ItemCart.invalidateViews();
        }

        @Override
        public int getCount() {
            return Item_list.size();
        }

        @Override
        public ItemList getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public int getPosition(ItemList item) {
            return super.getPosition(item);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final OrderCart_holder ordercart_holder;
            if (convertView == null) {
                ordercart_holder = new OrderCart_holder();
                LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_shipmentitem, parent, false);
                ordercart_holder.reciever_name = (TextView) convertView.findViewById(R.id.tvShipmentItem_Recievers_Name);
                ordercart_holder.address_line1 = (TextView) convertView.findViewById(R.id.tvShipmentItem_Recievers_AddressLine1);
                ordercart_holder.address_line2 = (TextView) convertView.findViewById(R.id.tvShipmentItem_Recievers_AddressLine2);
                ordercart_holder.shipping_option = (Button) convertView.findViewById(R.id.bShipmentItem_Set_ShippingOptions);
                ordercart_holder.item_image = (ImageView) convertView.findViewById(R.id.ivShipmentItem_Image);
                ordercart_holder.cross = (ImageView) convertView.findViewById(R.id.ivShipmentItem_Delete);
                ordercart_holder.edit = (ImageView) convertView.findViewById(R.id.ivShipmentItem_Edit);

                convertView.setTag(ordercart_holder);
            } else {
                ordercart_holder = (OrderCart_holder) convertView.getTag();
            }
            if (getCount() > 0) {
                Log.i("Inhere", String.valueOf(getCount()));
                Activity_Main.noShipmentTv.setVisibility(View.INVISIBLE);
            } else {
                Log.i("Caladfljshfdskj", String.valueOf(getCount()));
                Activity_Main.noShipmentTv.setVisibility(View.VISIBLE);
            }
            ImageLoader.getInstance().displayImage(Item_list.get(position).getImage_URI(), ordercart_holder.item_image);
            ordercart_holder.reciever_name.setText(Item_list.get(position).getReceiver_Name());
            ordercart_holder.address_line1.setText(Item_list.get(position).getFlat_no() + ", " + Item_list.get(position).getLocality());
            ordercart_holder.address_line2.setText(Item_list.get(position).getCity() + ", " + Item_list.get(position).getState());
            ordercart_holder.shipping_option.setText(Item_list.get(position).getShipping_Option());

            ordercart_holder.item_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_image_preview);
                    dialog.getWindow().setBackgroundDrawable((new ColorDrawable(android.graphics.Color.TRANSPARENT)));
                    dialog.show();
                    ImageView iv = (ImageView) dialog.findViewById(R.id.iv11);
                    dialog.findViewById(R.id.bCancelImage).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.bEditPicture).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            if (isDeviceSupportCamera()) {
                                Utils utils = new Utils(getActivity());
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                utils.setvalue("oldimage", Item_list.get(position).getImage_URI());
                                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                            }

                        }
                    });
                    ImageLoader.getInstance().displayImage(Item_list.get(position).getImage_URI(), iv);
                }
            });
            ordercart_holder.cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are you sure you want to delete this item?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Db_Item_List items = new Db_Item_List();
                            Utils utils = new Utils(getActivity());
                            items.deleteItem(String.valueOf(utils.getOrderId()), Item_list.get(position).getImage_URI());
                            if (getCount() == 1) {
                                Activity_Main.noShipmentTv.setVisibility(View.VISIBLE);

                            }

                            madapter = new OrderList_Adapter(getActivity(), R.layout.list_item_shipmentitem, ShowItemList());
                            lv_ItemCart.setAdapter(madapter);
                            Log.i("Cross CLicked", "Item Deleted");
                            madapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.show();

                    TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.CENTER);
                    dialog.show();

                }
            });
            ordercart_holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), Activity_UpdateAddress.class);
                    i.putExtra("ImageURI", Item_list.get(position).getImage_URI());
                    i.putExtra("U_Name", Item_list.get(position).getReceiver_Name());
                    i.putExtra("U_Flatno", Item_list.get(position).getFlat_no());
                    i.putExtra("U_Number", Item_list.get(position).getPhone());
                    i.putExtra("U_Country", Item_list.get(position).getCountry());
                    i.putExtra("U_Locality", Item_list.get(position).getLocality());
                    i.putExtra("u_City", Item_list.get(position).getCity());
                    i.putExtra("U_Pincode", Item_list.get(position).getPinCode());
                    i.putExtra("U_State", Item_list.get(position).getState());
                    i.putExtra("ID", position);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                }
            });

            ordercart_holder.shipping_option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (utils.getvalue("PickupAddress") != null) {
                        Intent i = new Intent(getActivity(), Activity_Shipping_Options.class);
                        i.putExtra("imageuri", Item_list.get(position).getImage_URI());
                        i.putExtra("shipping value", ordercart_holder.shipping_option.getText());
                        i.putExtra("pincode", Item_list.get(position).getPinCode());
                        startActivity(i);
                        getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                    } else {
                        Toast.makeText(getActivity(), "Set your Pickup Location", Toast.LENGTH_LONG).show();
                    }
                }
            });

            return convertView;
        }


    }

    public ArrayList<ItemList> ShowItemList() {

        Utils utils = new Utils(getActivity());
        Db_Item_List newItem = new Db_Item_List();
        Item_list = new ArrayList<ItemList>();
        List<Db_Item_List> list = newItem.getAllItems(String.valueOf(utils.getOrderId()));
        for (int i = 0; i < newItem.getAllItems(String.valueOf(utils.getOrderId())).size(); i++) {
            Db_Item_List addDBReceiver = list.get(i);
            ItemList itemlist = new ItemList();
            itemlist.setDate(newItem.date);
            itemlist.setImage_URI(addDBReceiver.image_uri);
            itemlist.setOrderId(addDBReceiver.orderid);
            itemlist.setReceiver_Name(addDBReceiver.name);
            itemlist.setPhone(addDBReceiver.phone);
            itemlist.setFlat_no(addDBReceiver.flat_no);
            itemlist.setLocality(addDBReceiver.locality);
            itemlist.setCity(addDBReceiver.city);
            itemlist.setState(addDBReceiver.state);
            itemlist.setCountry(addDBReceiver.country);
            itemlist.setPinCode(addDBReceiver.pincode);
            itemlist.setShipping_Option(addDBReceiver.setshippingoption);
            Item_list.add(itemlist);
        }
        return Item_list;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_Locator:
                Utils utils = new Utils(getActivity());

                if (utils.getvalue("PickupAddress") != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Current Pickup Address");
                    builder.setMessage(utils.getvalue("PickupFlatNo") + ", " + utils.getvalue("PickupAddress"));
                    builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getActivity(), Activity_Maps.class);
                            startActivity(i);
                            getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = builder.show();

                    TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.CENTER);
                    dialog.show();
                } else {
                    Intent i = new Intent(getActivity(), Activity_Maps.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                }
//
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {

                File imgFile = new File(fileUri.getPath());
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                File xxx = new File(fileUri.getPath());
                try {
                    FileOutputStream fOut = new FileOutputStream(xxx);
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 10, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ItemList itemlist = new ItemList();
                Utils utils = new Utils(getActivity());
                Db_Item_List itemlistDB = new Db_Item_List();
                itemlist.setImage_URI(fileUri.toString());
                itemlistDB.updateImage(itemlist, utils.getvalue("oldimage"));
                Intent i = new Intent(getActivity(), Activity_Main.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
            } else {
                Toast.makeText(getActivity(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //check if device supports the camera
    private boolean isDeviceSupportCamera() {
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            Toast.makeText(getActivity(), "Your DEvice is not camera supported", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    //generate file uri
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    //Create a File
    private static File getOutputMediaFile(int type) {

        // Create SD card Location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Sendd");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Sendd", "Failed create Sendd directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpeg");
        } else {
            return null;
        }
        return mediaFile;
    }

}