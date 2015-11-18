package co.sendd.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wizrocket.android.sdk.WizRocketAPI;
import com.wizrocket.android.sdk.exceptions.WizRocketMetaDataNotFoundException;
import com.wizrocket.android.sdk.exceptions.WizRocketPermissionsNotSatisfied;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.sendd.R;
import co.sendd.activity.Activity_Address_Search_Receiver;
import co.sendd.activity.Activity_Main;
import co.sendd.activity.Activity_Pickup_Address;
import co.sendd.activity.Activity_Reigister_Phone;
import co.sendd.activity.Activity_Shipping_Options;
import co.sendd.activity.Activity_ThankYou;
import co.sendd.activity.Activity_UpdateAddress;
import co.sendd.databases.Db_CompleteOrder;
import co.sendd.databases.Db_Item_List;
import co.sendd.gettersandsetters.CompleteOrder;
import co.sendd.gettersandsetters.Drop_address;
import co.sendd.gettersandsetters.ItemList;
import co.sendd.gettersandsetters.NameEmailObject;
import co.sendd.gettersandsetters.NewOrderObject;
import co.sendd.gettersandsetters.NewShipment;
import co.sendd.gettersandsetters.Orders;
import co.sendd.gettersandsetters.Promo;
import co.sendd.gettersandsetters.Shipment;
import co.sendd.helper.NetworkUtils;
import co.sendd.helper.SaveImageService;
import co.sendd.helper.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;

public class Fragment_Orders extends Fragment {
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 101;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_New_Image = 100;
    private static final int MEDIA_TYPE_IMAGE = 1;
    public static TextView noShipmentTv, tvErrorMsg, etPromocode, tvPromomessage;
    private static Uri fileUri, fileUri_NewImage;
    LinearLayout rlPromoCode;
    Date PickupLaterDate;
    String PickupLaterTime;
    ImageView crossicon;
    Calendar c;
    DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();
    ImageLoaderConfiguration config;
    private WizRocketAPI wr;
    private ListView lv_ItemCart;
    private OrderList_Adapter madapter;
    private ArrayList<ItemList> Item_list;
    private ProgressDialog pd;
    private Utils utils;
    private RadioGroup radioGroup1;
    private RelativeLayout rl;
    private LinearLayout bottomButtons;
    private NumberPicker PickLater_Date;
    private Date mDate;
    private Button pickupNow, pickupLater;

    //Create a File
    private static File getOutputMediaFile(int type) {

        // Create SD card Location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Sendd");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpeg");
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("file_uri", fileUri);
        outState.putParcelable("fileUri_NewImage", fileUri_NewImage);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            wr = WizRocketAPI.getInstance(getActivity().getApplicationContext());
        } catch (WizRocketMetaDataNotFoundException | WizRocketPermissionsNotSatisfied e) {

        }
        if (savedInstanceState != null) {
            fileUri = savedInstanceState.getParcelable("file_uri");
            fileUri_NewImage = savedInstanceState.getParcelable("fileUri_NewImage");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        wr.activityResumed(getActivity());
        madapter = new OrderList_Adapter(getActivity(), R.layout.list_item_shipmentitem, ShowItemList());
        lv_ItemCart.setAdapter(madapter);
        if (madapter.getCount() > 0) {
            bottomButtons.setVisibility(View.VISIBLE);
            noShipmentTv.setVisibility(View.INVISIBLE);
            rlPromoCode.setVisibility(View.VISIBLE);

        } else {
            rlPromoCode.setVisibility(View.INVISIBLE);
            bottomButtons.setVisibility(View.INVISIBLE);
            noShipmentTv.setVisibility(View.VISIBLE);
        }
        utils = new Utils(getActivity());
        if (utils.getvalue("PickupAddress").equals("")) {
            ((Activity_Main) getActivity()).setActionBarTitle("Sendd");
        } else {
            rl.setVisibility(View.GONE);
            ((Activity_Main) getActivity()).setActionBarTitle(utils.getvalue("PickupFlatNo") + ", " + utils.getvalue("PickupAddress"));
        }
        if (utils.getvalue("coupon_code_msg").equals("")) {
            crossicon.setVisibility(View.GONE);
            tvPromomessage.setText("Do you have a Promocode?");
            tvPromomessage.setTextColor(Color.BLACK);
            rlPromoCode.setEnabled(true);
        } else {
            tvPromomessage.setText(utils.getvalue("coupon_code_msg"));
            tvPromomessage.setTextColor(Color.parseColor("#00aa00"));
            rlPromoCode.setEnabled(false);
            crossicon.setVisibility(View.VISIBLE);
        }
        if (!ImageLoader.getInstance().isInited())
            ImageLoader.getInstance().init(config);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        mDate = new Date();
        config = new ImageLoaderConfiguration.Builder(getActivity()).defaultDisplayImageOptions(defaultOptions).build();
        c = Calendar.getInstance();
        rl = (RelativeLayout) view.findViewById(R.id.setlocationrl);
        rlPromoCode = (LinearLayout) view.findViewById(R.id.rlPromoCode);
        tvPromomessage = (TextView) view.findViewById(R.id.tvPromomessage);
        crossicon = (ImageView) view.findViewById(R.id.crossicon);
        crossicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crossicon.setVisibility(View.GONE);
                tvPromomessage.setText("Do you have a Promocode?");
                tvPromomessage.setTextColor(Color.BLACK);
                rlPromoCode.setEnabled(true);
                utils.setvalue("coupon_code_msg", "");
                utils.setvalue("coupon_code", "");


            }
        });
        rlPromoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!utils.isRegisterd()) {
                    Intent i = new Intent(getActivity(), Activity_Reigister_Phone.class);
                    i.putExtra("Login", true);
                    getActivity().startActivity(i);
                    getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                } else {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setBackgroundDrawable((new ColorDrawable(android.graphics.Color.TRANSPARENT)));
                    dialog.setContentView(R.layout.dialog_promo);
                    dialog.show();
                    tvErrorMsg = (TextView) dialog.findViewById(R.id.tvErrorMsg);
                    etPromocode = (TextView) dialog.findViewById(R.id.etPromocode);
                    LinearLayout Cancel = (LinearLayout) dialog.findViewById(R.id.Cancel);
                    LinearLayout Submit = (LinearLayout) dialog.findViewById(R.id.apply);
                    radioGroup1 = (RadioGroup) dialog.findViewById(R.id.radioGroup1);
                    Cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Submit.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pd = new ProgressDialog(getActivity());
                                    pd.setMessage("Verifying Promo Code, Please wait.");
                                    pd.setCancelable(false);
                                    pd.setIndeterminate(true);
                                    pd.show();
                                    if (!etPromocode.getText().toString().isEmpty()) {
                                        final NetworkUtils mnetworkutils = new NetworkUtils(getActivity());
                                        Promo mPromo = new Promo();
                                        mPromo.setPhone(utils.getvalue("RegisteredPhone"));
                                        mPromo.setCode(etPromocode.getText().toString());
                                        if (mnetworkutils.isnetconnected()) {
                                            //VERIFY OTP
                                            mnetworkutils.getapi().checkPromo(mPromo, new Callback<Promo>() {
                                                @Override
                                                public void success(Promo promo, Response response) {
                                                    pd.dismiss();
                                                    if (promo.getValid().equals("N")) {
                                                        tvErrorMsg.setText(promo.getPromomsg());
                                                    }
                                                    if (promo.getValid().equals("Y")) {
                                                        dialog.dismiss();
                                                        tvPromomessage.setText(promo.getPromomsg());
                                                        tvPromomessage.setTextColor(Color.parseColor("#00aa00"));
                                                        rlPromoCode.setEnabled(false);
                                                        crossicon.setVisibility(View.VISIBLE);
                                                        utils.setvalue("coupon_code_msg", promo.getPromomsg());
                                                        utils.setvalue("coupon_code", etPromocode.getText().toString());
                                                    }
                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    pd.dismiss();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getActivity(), "Not connected to network", Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "Please enter a verification code", Toast.LENGTH_SHORT).show();
                                        pd.dismiss();
                                    }
                                }
                            }
                    );
                }
            }
        });

        bottomButtons = (LinearLayout) view.findViewById(R.id.bottomButtons);
        Activity_Main.exit = true;
        noShipmentTv = (TextView) view.findViewById(R.id.noshiptv);
        utils = new Utils(getActivity());
        if (utils.getvalue("PickupAddress").equals("")) {
            ((Activity_Main) getActivity()).setActionBarTitle("Sendd");
        } else {
            ((Activity_Main) getActivity()).setActionBarTitle(utils.getvalue("PickupFlatNo") + ", " + utils.getvalue("PickupAddress"));
            rl.setVisibility(View.GONE);
        }
        ((Activity_Main) getActivity()).showActionBar();

        if (!ImageLoader.getInstance().isInited())
            ImageLoader.getInstance().init(config);


        pickupNow = (Button) view.findViewById(R.id.bFragment_Orders_PickNow);
        pickupLater = (Button) view.findViewById(R.id.bFragment_Orders_PickLater);

        pickupNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                if (Calendar.SUNDAY != cal.get(Calendar.DAY_OF_WEEK)) {
                    wr.event.push("PickUp now Clicked");
                    final NetworkUtils mnetworkutils = new NetworkUtils(getActivity());
                    Date d = Calendar.getInstance().getTime();
                    DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    final String time = dateFormat.format(d);
                    if (mnetworkutils.isnetconnected()) {
                        if (d.getHours() >= 10 && d.getHours() < 18) {
                            if (!(utils.getvalue("PickupAddress").equals(""))) {
                                if (madapter.getCount() > 0) {
                                    if (!utils.isRegisterd()) {
                                        final Dialog dialog = new Dialog(getActivity());
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setContentView(R.layout.dialog_confirm_address);
                                        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(android.graphics.Color.TRANSPARENT)));
                                        dialog.show();
                                        TextView tv = (TextView) dialog.findViewById(R.id.textView4);
                                        TextView tv2 = (TextView) dialog.findViewById(R.id.tvButtonconfirm);
                                        ImageView iv = (ImageView) dialog.findViewById(R.id.ivButtonconfirm);
                                        tv2.setText("Confirm");
                                        iv.setImageResource(R.drawable.dialog_righticon);
                                        tv.setText(utils.getvalue("PickupFlatNo") + ", " + utils.getvalue("PickupAddress") + ", " + utils.getvalue("PickupPincode"));
                                        dialog.findViewById(R.id.bCancel).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                                Intent i = new Intent(getActivity(), Activity_Reigister_Phone.class);
                                                i.putExtra("PickupOption", "PickUpNow");
                                                i.putExtra("adapterCount", madapter.getCount());
                                                getActivity().startActivity(i);
                                                getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

                                            }
                                        });
                                        dialog.findViewById(R.id.bChange).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                                Intent i = new Intent(getActivity(), Activity_Pickup_Address.class);
                                                startActivity(i);
                                                getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

                                            }
                                        });
                                    } else {
                                        BookService(new Date(), time, "Y");
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Add Parcel to continue booking", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Intent i = new Intent(getActivity(), Activity_Pickup_Address.class);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                            }
                        } else {
                            for (int i = 0; i < 2; i++) {
                                Toast.makeText(getActivity(), "We currently serve between 10 AM to 6 PM. Kindly use Pick Later option or book tomorrow.", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please Connect to a working Internet Connection", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Sorry we are closed on Sundays.", Toast.LENGTH_LONG).show();
                }
            }


        });

        pickupLater.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wr.event.push("PickUp Later Clicked");
                        final Dialog dialog = new Dialog(getActivity());
                        dialog.setTitle("Choose a date and time");
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(android.graphics.Color.TRANSPARENT)));
                        dialog.setContentView(R.layout.dialog_pickup_later_new);
                        dialog.show();
                        LinearLayout Cancel = (LinearLayout) dialog.findViewById(R.id.Cancel);
                        LinearLayout Submit = (LinearLayout) dialog.findViewById(R.id.Submit);
                        radioGroup1 = (RadioGroup) dialog.findViewById(R.id.radioGroup1);
                        Cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        Submit.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        final NetworkUtils mnetworkutils = new NetworkUtils(getActivity());
                                        final Date d = Calendar.getInstance().getTime();
                                        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                                        if (mnetworkutils.isnetconnected()) {
                                            if (!(utils.getvalue("PickupAddress").equals(""))) {

                                                if (madapter.getCount() > 0) {
                                                    if (!utils.isRegisterd()) {
                                                        final Dialog dialog = new Dialog(getActivity());
                                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        dialog.setContentView(R.layout.dialog_confirm_address);
                                                        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(android.graphics.Color.TRANSPARENT)));
                                                        dialog.show();
                                                        TextView tv = (TextView) dialog.findViewById(R.id.textView4);
                                                        TextView tv2 = (TextView) dialog.findViewById(R.id.tvButtonconfirm);
                                                        ImageView iv = (ImageView) dialog.findViewById(R.id.ivButtonconfirm);
                                                        tv2.setText("Confirm");
                                                        iv.setImageResource(R.drawable.dialog_righticon);
                                                        tv.setText(utils.getvalue("PickupFlatNo") + ", " + utils.getvalue("PickupAddress") + ", " + utils.getvalue("PickupPincode"));
                                                        dialog.findViewById(R.id.bCancel).setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                dialog.dismiss();
                                                                Intent i = new Intent(getActivity(), Activity_Reigister_Phone.class);
                                                                i.putExtra("PickupOption", "PickUpLater");
                                                                i.putExtra("DATE", sdf.format(PickupLaterDate));
                                                                i.putExtra("TIME", PickupLaterTime);
                                                                i.putExtra("adapterCount", madapter.getCount());
                                                                getActivity().startActivity(i);
                                                                getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                                                            }
                                                        });
                                                        dialog.findViewById(R.id.bChange).setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                dialog.dismiss();
                                                                Intent i = new Intent(getActivity(), Activity_Pickup_Address.class);
                                                                startActivity(i);
                                                                getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

                                                            }
                                                        });
                                                    } else {
                                                        BookService(PickupLaterDate, PickupLaterTime, "N");
                                                    }
                                                } else {
                                                    Toast.makeText(getActivity(), "Add Parcel to continue booking", Toast.LENGTH_LONG).show();
                                                }

                                            } else {
                                                Intent i = new Intent(getActivity(), Activity_Pickup_Address.class);
                                                startActivity(i);
                                                getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "Please Connect to a working Internet Connection", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                        );
                        RadioButton rToday = (RadioButton) dialog.findViewById(R.id.radioToday);
                        RadioButton rTomorrow = (RadioButton) dialog.findViewById(R.id.radioTomorrow);
                        RadioButton radioDayAfter = (RadioButton) dialog.findViewById(R.id.radioDayAfter);

                        PickLater_Date = (NumberPicker) dialog.findViewById(R.id.timeslots);
                        final String[] timeSlots = new String[]{"10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM", "01:00 PM", "01:30 PM", "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM", "05:00 PM", "05:30 PM", "06:00 PM"};
                        if (mDate.getHours() < 17) {
                            ArrayList<String> time = new ArrayList<>();
                            int x = 0;
                            if (mDate.getHours() < 9) {
                                x = 0;
                            } else if (mDate.getHours() >= 9 && mDate.getHours() < 17) {
                                if (mDate.getMinutes() > 30) {
                                    x = (mDate.getHours() - 9) * 2 + 2;
                                } else {
                                    x = (mDate.getHours() - 9) * 2 + 1;
                                }
                            } else {
                                x = 0;
                            }
                            for (int i = x; i < 17; i++) {
                                time.add(timeSlots[i]);
                            }
                            final String[] times = time.toArray(new String[time.size()]);
                            PickLater_Date.setDisplayedValues(null);
                            PickLater_Date.setMinValue(0);
                            PickLater_Date.setMaxValue(times.length - 1);
                            PickLater_Date.setDisplayedValues(times);
                            PickupLaterDate = new Date();
                            PickupLaterTime = times[PickLater_Date.getValue()];
                            PickLater_Date.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                                @Override
                                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                    PickupLaterTime = times[PickLater_Date.getValue()];
                                }
                            });
                        } else {
                            rToday.setVisibility(View.GONE);
                            rTomorrow.setChecked(true);
                            PickLater_Date = (NumberPicker) dialog.findViewById(R.id.timeslots);
                            PickLater_Date.setDisplayedValues(null);
                            PickLater_Date.setMinValue(0);
                            PickLater_Date.setMaxValue(timeSlots.length - 1);
                            PickLater_Date.setDisplayedValues(timeSlots);
                            c.setTime(new Date());
                            c.add(Calendar.DATE, 1);
                            PickupLaterDate = c.getTime();
                            PickupLaterTime = timeSlots[PickLater_Date.getValue()];
                            PickLater_Date.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                                @Override
                                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                    PickupLaterTime = timeSlots[PickLater_Date.getValue()];
                                }
                            });
                        }
                        Calendar cal = Calendar.getInstance();
                        if (Calendar.SUNDAY == cal.get(Calendar.DAY_OF_WEEK)) {
                            rToday.setVisibility(View.GONE);
                            rTomorrow.setChecked(true);
                            PickLater_Date = (NumberPicker) dialog.findViewById(R.id.timeslots);
                            PickLater_Date.setDisplayedValues(null);
                            PickLater_Date.setMinValue(0);
                            PickLater_Date.setMaxValue(timeSlots.length - 1);
                            PickLater_Date.setDisplayedValues(timeSlots);
                            c.setTime(new Date());
                            c.add(Calendar.DATE, 1);
                            PickupLaterDate = c.getTime();
                            PickupLaterTime = timeSlots[PickLater_Date.getValue()];
                            PickLater_Date.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                                @Override
                                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                    PickupLaterTime = timeSlots[PickLater_Date.getValue()];
                                }
                            });
                        } else if (Calendar.SATURDAY == cal.get(Calendar.DAY_OF_WEEK)) {
                            rTomorrow.setVisibility(View.GONE);
                        } else if (Calendar.FRIDAY == cal.get(Calendar.DAY_OF_WEEK)) {
                            radioDayAfter.setVisibility(View.GONE);
                        }
                        radioGroup1.setOnCheckedChangeListener(
                                new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                                        PickLater_Date = (NumberPicker) dialog.findViewById(R.id.timeslots);
                                        switch (checkedId) {
                                            case R.id.radioToday:
                                                mDate.getHours();
                                                ArrayList<String> time = new ArrayList<String>();
                                                int x = 0;
                                                if (mDate.getHours() < 9) {
                                                    x = 0;
                                                } else if (mDate.getHours() >= 9 && mDate.getHours() < 17) {
                                                    if (mDate.getMinutes() > 30) {
                                                        x = (mDate.getHours() - 9) * 2 + 2;

                                                    } else {
                                                        x = (mDate.getHours() - 9) * 2 + 1;

                                                    }
                                                } else {
                                                    x = 0;
                                                }
                                                time.addAll(Arrays.asList(timeSlots).subList(x, 17));
                                                final String[] times = time.toArray(new String[time.size()]);
                                                PickLater_Date.setDisplayedValues(null);
                                                PickLater_Date.setMinValue(0);
                                                PickLater_Date.setMaxValue(times.length - 1);
                                                PickLater_Date.setDisplayedValues(times);
                                                c.setTime(new Date());
                                                c.add(Calendar.DATE, 0);
                                                PickupLaterDate = c.getTime();
                                                PickupLaterTime = times[PickLater_Date.getValue()];
                                                PickLater_Date.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                                                    @Override
                                                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                                        PickupLaterTime = times[PickLater_Date.getValue()];
                                                    }
                                                });
                                                break;
                                            case R.id.radioTomorrow:
                                                PickLater_Date.setDisplayedValues(null);
                                                PickLater_Date.setMinValue(0);
                                                PickLater_Date.setMaxValue(timeSlots.length - 1);
                                                PickLater_Date.setDisplayedValues(timeSlots);

                                                PickLater_Date.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                                                    @Override
                                                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                                        PickupLaterTime = timeSlots[PickLater_Date.getValue()];
                                                    }
                                                });
                                                c.setTime(new Date());
                                                c.add(Calendar.DATE, 1);
                                                PickupLaterDate = c.getTime();
                                                PickupLaterTime = timeSlots[PickLater_Date.getValue()];

                                                break;
                                            case R.id.radioDayAfter:
                                                PickLater_Date.setDisplayedValues(null);
                                                PickLater_Date.setMinValue(0);
                                                PickLater_Date.setMaxValue(timeSlots.length - 1);
                                                PickLater_Date.setDisplayedValues(timeSlots);
                                                c.setTime(new Date());
                                                c.add(Calendar.DATE, 2);
                                                PickupLaterDate = c.getTime();
                                                PickupLaterTime = timeSlots[PickLater_Date.getValue()];
                                                PickLater_Date.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                                                    @Override
                                                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                                        PickupLaterTime = timeSlots[PickLater_Date.getValue()];
                                                    }
                                                });

                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                        );

                    }
                }
        );
        if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
            Activity_Main.mProgress.dismiss();
        }
        Button addItem = (Button) view.findViewById(R.id.bFragment_Orders_AddShipment);
        addItem.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Dialog dialog = new Dialog(getActivity());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_camera);
                        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(android.graphics.Color.TRANSPARENT)));
                        dialog.show();
                        dialog.findViewById(R.id.bDialogContinue).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                wr.event.push("Adding Shipment");
                                captureImage();
                            }
                        });
                        dialog.setCancelable(true);

                    }
                }

        );
        lv_ItemCart = (ListView) view.findViewById(R.id.lvFragment_Orders_Items_List);
        lv_ItemCart.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        madapter.notifyDataSetChanged();
                        lv_ItemCart.smoothScrollToPositionFromTop(position, 0);
                    }
                }

        );
        madapter = new OrderList_Adapter(getActivity(), R.layout.list_item_shipmentitem, ShowItemList());
        lv_ItemCart.setAdapter(madapter);
//        Target viewTarget = new ViewTarget(R.id.bFragment_Orders_AddShipment, getActivity());
//        new ShowcaseView.Builder(getActivity())
//                .setContentText("Hello")
//                .setTarget(viewTarget)
//                .build();
    }

    public ArrayList<ItemList> ShowItemList() {

        Utils utils = new Utils(getActivity());
        Item_list = new ArrayList<>();
        List<Db_Item_List> list = Db_Item_List.getAllItems(String.valueOf(utils.getOrderId()));
        for (int i = 0; i < Db_Item_List.getAllItems(String.valueOf(utils.getOrderId())).size(); i++) {
            Db_Item_List addDBReceiver = list.get(i);
            ItemList itemlist = new ItemList();
            itemlist.setDate(addDBReceiver.date);
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
                if (!(utils.getvalue("PickupAddress").equals(""))) {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_confirm_address);
                    dialog.getWindow().setBackgroundDrawable((new ColorDrawable(android.graphics.Color.TRANSPARENT)));
                    dialog.show();
                    TextView tv = (TextView) dialog.findViewById(R.id.textView4);
                    tv.setText(utils.getvalue("PickupFlatNo") + ", " + utils.getvalue("PickupAddress") + ", " + utils.getvalue("PickupPincode"));
                    dialog.findViewById(R.id.bCancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.bChange).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent i = new Intent(getActivity(), Activity_Pickup_Address.class);
                            startActivity(i);
                            getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                        }
                    });


                } else {
                    Intent i = new Intent(getActivity(), Activity_Pickup_Address.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                }

        }
        return super.onOptionsItemSelected(item);
    }

    //Open Camera
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri_NewImage = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri_NewImage);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_New_Image);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File imgFile = new File(fileUri.getPath());
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                File xxx = new File(fileUri.getPath());
                try {
                    FileOutputStream fOut = new FileOutputStream(xxx);
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 10, fOut);
                    fOut.flush();
                    fOut.close();
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
            }
        }
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_New_Image) {
            if (resultCode == Activity.RESULT_OK) {
                Intent f = new Intent(getActivity(), SaveImageService.class);
                if (fileUri_NewImage != null)
                    f.putExtra("ImageUrl", fileUri_NewImage.toString());
                getActivity().startService(f);
                Intent i = new Intent(getActivity(), Activity_Address_Search_Receiver.class);
                if (fileUri_NewImage != null)
                    i.putExtra("ImageURI", fileUri_NewImage.toString());
                startActivity(i);
                getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

            }
        }
    }

    //check if device supports the camera
    private boolean isDeviceSupportCamera() {
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            Toast.makeText(getActivity(), "Your Device is not camera supported", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    //generate file uri
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    @Override
    public void onPause() {
        super.onPause();
        wr.activityPaused(getActivity());
    }

    public void BookService(final Date date1, final String time1, final String from) {

        final NetworkUtils mnetworkutils = new NetworkUtils(getActivity());
        final ProgressDialog mprogress;
        mprogress = new ProgressDialog(getActivity());
        mprogress.setMessage("Scheduling your pickup, Please wait.");
        mprogress.setCancelable(false);
        mprogress.setIndeterminate(true);
        if (mnetworkutils.isnetconnected()) {
            if (!(utils.getvalue("PickupAddress").equals(""))) {
                if (madapter.getCount() > 0) {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_confirm_address);
                    dialog.getWindow().setBackgroundDrawable((new ColorDrawable(android.graphics.Color.TRANSPARENT)));
                    dialog.show();
                    TextView tv = (TextView) dialog.findViewById(R.id.textView4);
                    TextView tv2 = (TextView) dialog.findViewById(R.id.tvButtonconfirm);
                    ImageView iv = (ImageView) dialog.findViewById(R.id.ivButtonconfirm);
                    tv2.setText("Confirm");
                    iv.setImageResource(R.drawable.dialog_righticon);
                    String Address = utils.getvalue("PickupFlatNo") + ", " + utils.getvalue("PickupAddress") + ", " + utils.getvalue("PickupPincode");
                    tv.setText(Address);
                    dialog.findViewById(R.id.bCancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            NewOrderObject newOrderObject = new NewOrderObject();
                            newOrderObject.setAddress(utils.getvalue("PickupAddress"));
                            newOrderObject.setCode(utils.getvalue("coupon_code"));
                            newOrderObject.setDate(date1);
                            newOrderObject.setFlat_no(utils.getvalue("PickupFlatNo"));
                            try {
                                if (!utils.getvalue("Lat").equals("null"))
                                    newOrderObject.setLatitude(Double.parseDouble(utils.getvalue("Lat")));
                                if (!utils.getvalue("Longi").equals("null"))
                                    newOrderObject.setLongitude(Double.parseDouble(utils.getvalue("Longi")));
                            } catch (NumberFormatException | NullPointerException ignored) {

                            }
                            newOrderObject.setPick_now(from);
                            newOrderObject.setUser("/api/v3/user/" + utils.getvalue("RegisteredPhone") + "/");
                            newOrderObject.setTime(time1);
                            newOrderObject.setPincode(utils.getvalue("PickupPincode"));
                            newOrderObject.setWay("A");
                            NameEmailObject nameEmailObject = new NameEmailObject();
                            nameEmailObject.setUser("/api/v3/user/" + utils.getvalue("RegisteredPhone") + "/");
                            nameEmailObject.setEmail(utils.getvalue("Sender_Email"));
                            nameEmailObject.setName(utils.getvalue("Sender_Name"));
                            newOrderObject.setNamemail(nameEmailObject);
                            ArrayList<NewShipment> newShipmentArray = new ArrayList<>();
                            madapter.getCount();
                            Item_list = new ArrayList<>();
                            final List<Db_Item_List> list = Db_Item_List.getAllItems(String.valueOf(utils.getOrderId()));
                            for (int i = 0; i < madapter.getCount(); i++) {
                                final Db_Item_List addDBReceiver = list.get(i);
                                String cat = "S";
                                switch (addDBReceiver.setshippingoption) {
                                    case "Standard":
                                        cat = "S";
                                        break;
                                    case "Bulk":
                                        cat = "E";
                                        break;
                                    case "Set Shipping Option":
                                    case "Premium":
                                        cat = "P";
                                        break;
                                }

                                Drop_address drop_address = new Drop_address();
                                drop_address.setFlat_no(addDBReceiver.flat_no);
                                drop_address.setPincode(addDBReceiver.pincode);
                                drop_address.setCity(addDBReceiver.city);
                                drop_address.setLocality(addDBReceiver.locality);
                                drop_address.setCountry(addDBReceiver.country);
                                drop_address.setState(addDBReceiver.state);
                                NewShipment newShipment = new NewShipment();
                                newShipment.setCategory(cat);
                                newShipment.setDrop_address(drop_address);
                                newShipment.setDrop_name(addDBReceiver.name);
                                newShipment.setDrop_phone(addDBReceiver.phone);
                                final Uri imageuri = Uri.parse(addDBReceiver.image_uri);

                                File file = new File(imageuri.getPath());
                                byte[] data = new byte[(int) file.length()];
                                try {
                                    new FileInputStream(file).read(data);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                newShipment.setImg(Base64.encodeToString(data, Base64.DEFAULT));
                                newShipmentArray.add(newShipment);
                            }
                            newOrderObject.setShipments(newShipmentArray);
                            mprogress.show();
                            final CompleteOrder completeOrder = new CompleteOrder();
                            mnetworkutils.getapi().order(newOrderObject, new Callback<Response>() {
                                @Override
                                public void success(final Response orders, Response response) {
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
                                    Log.i("result", result);
                                    try {
                                        JSONObject jObj = new JSONObject(result);
                                        completeOrder.setPickup_address(utils.getvalue("PickupAddress"));
                                        completeOrder.setPickup_name(utils.getvalue("Sender_Name"));
                                        completeOrder.setPickup_phone(utils.getvalue("RegisteredPhone"));
                                        completeOrder.setEmail(utils.getvalue("Sender_Email"));
                                        completeOrder.setPickup_pincode(utils.getvalue("PickupPincode"));
                                        completeOrder.setDate(date1);
                                        completeOrder.setTime(time1);
                                        completeOrder.setOrder_Status(jObj.getString("status"));

                                        for (int i = 0; i < madapter.getCount(); i++) {
                                            final Db_Item_List addDBReceiver = list.get(i);
                                            String cat = "S";
                                            switch (addDBReceiver.setshippingoption) {
                                                case "Standard":
                                                    cat = "S";
                                                    break;
                                                case "Bulk":
                                                    cat = "E";
                                                    break;
                                                case "Set Shipping Option":
                                                case "Premium":
                                                    cat = "P";
                                                    break;
                                            }

                                            final Uri imageuri = Uri.parse(addDBReceiver.image_uri);
                                            completeOrder.setDrop_address(addDBReceiver.flat_no + ", " + addDBReceiver.locality + addDBReceiver.city + ", " + addDBReceiver.state + ", " + addDBReceiver.country);
                                            completeOrder.setDrop_phone(addDBReceiver.phone);
                                            completeOrder.setDrop_name(addDBReceiver.name);
                                            completeOrder.setDrop_pincode(addDBReceiver.pincode);
                                            Log.i("imageuri.getPath()", jObj.getJSONArray("shipments").getJSONObject(i).getString("img"));
                                            String imagepath = jObj.getJSONArray("shipments").getJSONObject(i).getString("img");
                                            completeOrder.setImage_uri(imageuri.getPath());
                                            completeOrder.setTracking_no(jObj.getJSONArray("shipments").getJSONObject(i).getString("real_tracking_no"));
                                            completeOrder.setCategory(cat);
                                            completeOrder.setOrder_id(jObj.getString("order_no"));
                                            if (utils.isSynced()) {
                                                Db_CompleteOrder DBCO = new Db_CompleteOrder();
                                                DBCO.AddToDB(completeOrder);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    mprogress.dismiss();
                                    utils.setvalue("coupon_code_msg", "");
                                    utils.setvalue("coupon_code", "");
                                    Intent i = new Intent(getActivity(), Activity_ThankYou.class);
                                    if (from.equals("Y"))
                                        i.putExtra("PickLater", false);
                                    else
                                        i.putExtra("PickLater", true);
                                    startActivity(i);
                                    getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    if (mprogress.isShowing()) {
                                        mprogress.dismiss();
                                    }
                                    try {

                                        String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                                        Log.i("error", json);
                                        JSONObject jobj;

                                        jobj = new JSONObject(new JSONObject(json).get("error").toString());
                                        if (jobj.getString("message") != null) {
                                            final Dialog dialog = new Dialog(getActivity());
                                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialog.setContentView(R.layout.dialog_error_message);
                                            dialog.getWindow().setBackgroundDrawable((new ColorDrawable(Color.TRANSPARENT)));
                                            dialog.show();
                                            TextView tv = (TextView) dialog.findViewById(R.id.textView4);
                                            tv.setText(jobj.getString("message"));
                                            dialog.findViewById(R.id.bCancel).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                }
                                            });

                                            Log.i("Message", jobj.getString("message"));
                                        } else {
                                            Toast.makeText(getActivity(), "Error Occurred. Please try again in some time.", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException | NullPointerException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                    });

                    dialog.findViewById(R.id.bChange).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent i = new Intent(getActivity(), Activity_Pickup_Address.class);
                            startActivity(i);
                            getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Add Item", Toast.LENGTH_LONG).show();
                }

            } else {
                Intent i = new Intent(getActivity(), Activity_Pickup_Address.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
            }
        } else {
            Toast.makeText(getActivity(), "Please Connect to a working Internet Connection", Toast.LENGTH_LONG).show();
        }

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
                rlPromoCode.setVisibility(View.VISIBLE);
                bottomButtons.setVisibility(View.VISIBLE);
                noShipmentTv.setVisibility(View.INVISIBLE);
            } else {
                rlPromoCode.setVisibility(View.INVISIBLE);
                bottomButtons.setVisibility(View.INVISIBLE);
                noShipmentTv.setVisibility(View.VISIBLE);
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
                                noShipmentTv.setVisibility(View.VISIBLE);
                                bottomButtons.setVisibility(View.INVISIBLE);
                                rlPromoCode.setVisibility(View.INVISIBLE);
                            }
                            madapter = new OrderList_Adapter(getActivity(), R.layout.list_item_shipmentitem, ShowItemList());
                            lv_ItemCart.setAdapter(madapter);
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
                    if (!(utils.getvalue("PickupAddress").equals(""))) {
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
}