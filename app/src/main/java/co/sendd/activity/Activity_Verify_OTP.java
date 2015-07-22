package co.sendd.activity;

/**
 * Created by harshkaranpuria on 4/27/15.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.sendd.R;
import co.sendd.databases.Db_CompleteOrder;
import co.sendd.databases.Db_Item_List;
import co.sendd.gettersandsetters.CompleteOrder;
import co.sendd.gettersandsetters.ItemList;
import co.sendd.gettersandsetters.Orders;
import co.sendd.gettersandsetters.Otp;
import co.sendd.gettersandsetters.Shipment;
import co.sendd.gettersandsetters.Users;
import co.sendd.helper.NetworkUtils;
import co.sendd.helper.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class Activity_Verify_OTP extends Activity {

    public static EditText otp;
    public Incomingsms mreceiver;
    public ProgressDialog pd;
    private Button register;
    private TextView tvphone;
    private ArrayList<ItemList> Item_list;
    private Utils utils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        utils = new Utils(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.verifytoolbar);
        if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
            Activity_Main.mProgress.dismiss();
        }
        toolbar.setNavigationIcon(R.drawable.arrow_back_icon_small);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Activity_Verify_OTP.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
                    }
                }
        );

        tvphone = (TextView) findViewById(R.id.tvPhone);
        if (getIntent().getStringExtra("NUMBER") != null)
            tvphone.setText(getIntent().getStringExtra("NUMBER"));
        otp = (EditText) findViewById(R.id.otp_edit);
        register = (Button) findViewById(R.id.bregister);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        mreceiver = new Incomingsms();
        registerReceiver(mreceiver, filter);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register.setEnabled(false);
                pd = new ProgressDialog(Activity_Verify_OTP.this);
                pd.setMessage("Verifying Phone number, Please wait.");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
                if (!otp.getText().toString().isEmpty()) {
                    final NetworkUtils mnetworkutils = new NetworkUtils(Activity_Verify_OTP.this);
                    String Phonenumber = getIntent().getStringExtra("NUMBER");
                    Otp new_otp = new Otp();
                    new_otp.setOtp1(otp.getText().toString());
                    new_otp.setNumber(Phonenumber);
                    if (mnetworkutils.isnetconnected()) {
                        //VERIFY OTP
                        mnetworkutils.getapi().verify(Phonenumber, new_otp, new Callback<Users>() {
                            @Override
                            public void success(Users user, Response response) {
                                register.setEnabled(true);
                                if (user.getValid().equals("0")) {
                                    Toast.makeText(Activity_Verify_OTP.this, "Please enter a correct verification code.", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                } else if (user.getValid().equals("1")) {
                                    Activity_Reigister_Phone.regPhone.finish();
                                    if (getIntent().getBooleanExtra("Login", false)) {
                                        //Coming form drawer Login Button
                                        Utils mUtils = new Utils(Activity_Verify_OTP.this);
                                        mUtils.Registered();
                                        mUtils.setvalue("RegisteredPhone", getIntent().getStringExtra("NUMBER"));
                                        pd.dismiss();
                                        Intent i = new Intent(getApplicationContext(), Activity_Main.class);
                                        startActivity(i);
                                        finish();
                                        overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

                                    } else if (getIntent().getBooleanExtra("PreviousShipments", false)) {
                                        //Coming from drawer Myshipments
                                        Utils mUtils = new Utils(Activity_Verify_OTP.this);
                                        mUtils.Registered();
                                        mUtils.setvalue("RegisteredPhone", getIntent().getStringExtra("NUMBER"));
                                        pd.dismiss();
                                        Intent i = new Intent(getApplicationContext(), Activity_Main.class);
                                        i.putExtra("openpreviousBooking", true);
                                        startActivity(i);
                                        finish();
                                        overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

                                    } else {
                                        Utils mUtils = new Utils(Activity_Verify_OTP.this);
                                        mUtils.Registered();
                                        mUtils.setvalue("RegisteredPhone", getIntent().getStringExtra("NUMBER"));
                                        //Coming from drawer PickupNOW OPTION
                                        if (getIntent().getStringExtra("PickupOption").equals("PickUpNow")) {
                                            pd.dismiss();
                                            Date d = Calendar.getInstance().getTime();
                                            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                            final String time = dateFormat.format(d);
                                            BookService(new Date(), time, "Y");

                                            //Coming from drawer PickupLATER OPTION
                                        } else if (getIntent().getStringExtra("PickupOption").equals("PickUpLater")) {
                                            final NetworkUtils mnetworkutils = new NetworkUtils(Activity_Verify_OTP.this);
                                            pd.dismiss();
                                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                            Date PickupLaterDate = new Date();
                                            try {
                                                PickupLaterDate = formatter.parse(getIntent().getExtras().getString("DATE"));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            BookService(PickupLaterDate, getIntent().getStringExtra("TIME"), "N");

                                        }
                                    }
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                register.setEnabled(true);
                                pd.dismiss();
                                Log.d("the error from server", error.toString());
                            }
                        });

                    } else {
                        register.setEnabled(true);
                        Toast.makeText(Activity_Verify_OTP.this, "Not connected to network", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                } else {
                    register.setEnabled(true);
                    Toast.makeText(Activity_Verify_OTP.this, "Please enter a verification code", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!otp.getText().toString().isEmpty()) {
            outState.putString("otp", otp.getText().toString());
        }
    }

    public boolean DisplayCode(String code) {
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rlWait);
        rl.setVisibility(View.GONE);
        if (otp != null) {
            otp.setText(code);
            return true;
        } else return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mreceiver);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Activity_Verify_OTP.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
    }

    public void BookService(final Date date1, final String time1, final String from) {


        final NetworkUtils mnetworkutils = new NetworkUtils(this);
        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Scheduling your pickup, Please wait.");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        final Orders orders = new Orders();
        orders.setUser(utils.getvalue("RegisteredPhone"));
        orders.setDate(date1);
        orders.setTime(time1);
        orders.setPick_now(from);
        orders.setCode(utils.getvalue("coupon_code"));
        orders.setEmail(utils.getvalue("Sender_Email"));
        orders.setName(utils.getvalue("Sender_Name"));
        orders.setAddress(utils.getvalue("PickupAddress"));
        orders.setFlat_no(utils.getvalue("PickupFlatNo"));
        try {
            if (!utils.getvalue("Lat").equals("null"))
                orders.setLatitude(Double.parseDouble(utils.getvalue("Lat")));
            if (!utils.getvalue("Longi").equals("null"))
                orders.setLongitude(Double.parseDouble(utils.getvalue("Longi")));
        } catch (NumberFormatException e) {

        }
        orders.setPincode(utils.getvalue("PickupPincode"));
        // Log.i("qwertyuiopajklzxcvbnm,", orders.getUser() + orders.getDate() + orders.getTime() + orders.getPickup_flat_no() + orders.getPickup_locality() + orders.getPickup_city() + orders.getPickup_pincode() + orders.getPickup_state() + orders.getPickup_country());
        final CompleteOrder completeOrder = new CompleteOrder();
        mnetworkutils.getapi().order(orders, new Callback<Orders>() {
            @Override
            public void success(final Orders orders, Response response) {
                if (mProgressDialog.isShowing()) {
                    completeOrder.setPickup_address(orders.getAddress());
                    completeOrder.setPickup_name(orders.getName());
                    completeOrder.setPickup_phone(orders.getUser());
                    completeOrder.setEmail(orders.getEmail());
                    completeOrder.setPickup_pincode(orders.getPincode());
                    completeOrder.setOrder_Status(orders.getStatus());
                    completeOrder.setPaid(orders.getPaid());
                    completeOrder.setTotal_cost(orders.getCost());
                    completeOrder.setDate(date1);
                    completeOrder.setTime(time1);
                    // Log.i("orders.getStatus() =", orders.getStatus());
                    // Log.i("OrderNumber Generated =", orders.getOrder_no());
                    Db_Item_List newItem = new Db_Item_List();
                    Item_list = new ArrayList<>();
                    List<Db_Item_List> list = newItem.getAllItems(String.valueOf(utils.getOrderId()));

                    for (int i = 0; i < getIntent().getIntExtra("adapterCount", 0); i++) {
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

                        //  Log.i("qweqwertyui", imageuri.getPath() + "   " + addDBReceiver.name + "   " + addDBReceiver.phone + "   " + addDBReceiver.flat_no + "   " + addDBReceiver.locality + "   " + addDBReceiver.city + "   " + addDBReceiver.state + "   " + addDBReceiver.country + "   " + addDBReceiver.pincode + "   " + orders.getOrder_no() + "   " + cat);
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
                                if (utils.isSynced()) {
                                    Db_CompleteOrder DBCO = new Db_CompleteOrder();
                                    DBCO.AddToDB(completeOrder);
                                }
//                                                            Log.i("Tracking no.Generated", shipment.getTracking_no());
//                                                            Log.i("Category", shipment.getCategory());
                                mProgressDialog.dismiss();
                                Intent i = new Intent(getApplicationContext(), Activity_ThankYou.class);
                                utils.setvalue("coupon_code_msg", "");
                                utils.setvalue("coupon_code", "");

                                if (from.equals("Y"))
                                    i.putExtra("PickLater", false);
                                else
                                    i.putExtra("PickLater", true);

                                startActivity(i);
                                overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                            }

                            @Override
                            public void failure(RetrofitError error) {

                                Log.i("Error Adding Shipments", error.toString());
                                mProgressDialog.dismiss();
                            }
                        });
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();

                    Log.i("Error Adding Order", error.toString());
                }
            }
        });
    }

    public class Incomingsms extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            SmsMessage shortMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
            if (shortMessage.getOriginatingAddress().contains("iSENDD")) {
                Otp new_otp = new Otp();
                Pattern pattern = Pattern.compile("\\d\\d\\d\\d");
                Matcher matcher = pattern.matcher(shortMessage.getDisplayMessageBody());
                while (matcher.find()) {
                    try {
                        new_otp.setOtp1(String.valueOf(matcher.group()));
                        DisplayCode(String.valueOf(matcher.group()));
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.i("Network Error", "Please connect to a working internet connection.");
            }
        }
    }
}