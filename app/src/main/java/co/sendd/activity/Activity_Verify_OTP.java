package co.sendd.activity;

/**
 * Created by harshkaranpuria on 4/27/15.
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import co.sendd.gettersandsetters.Drop_address;
import co.sendd.gettersandsetters.ItemList;
import co.sendd.gettersandsetters.NameEmailObject;
import co.sendd.gettersandsetters.NewOrderObject;
import co.sendd.gettersandsetters.NewShipment;
import co.sendd.gettersandsetters.Orders;
import co.sendd.gettersandsetters.Otp;
import co.sendd.gettersandsetters.Shipment;
import co.sendd.gettersandsetters.Users;
import co.sendd.helper.NetworkUtils;
import co.sendd.helper.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
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


        final NetworkUtils mnetworkutils = new NetworkUtils(Activity_Verify_OTP.this);
        final ProgressDialog mprogress;
        mprogress = new ProgressDialog(Activity_Verify_OTP.this);
        mprogress.setMessage("Scheduling your pickup, Please wait.");
        mprogress.setCancelable(false);
        mprogress.setIndeterminate(true);
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
         Item_list = new ArrayList<>();
        final List<Db_Item_List> list = Db_Item_List.getAllItems(String.valueOf(utils.getOrderId()));
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
                        completeOrder.setDrop_address(addDBReceiver.flat_no + ", " + addDBReceiver.locality + addDBReceiver.city + ", " + addDBReceiver.state + ", " + addDBReceiver.country);
                        completeOrder.setDrop_phone(addDBReceiver.phone);
                        completeOrder.setDrop_name(addDBReceiver.name);
                        completeOrder.setDrop_pincode(addDBReceiver.pincode);
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
                Intent i = new Intent(Activity_Verify_OTP.this, Activity_ThankYou.class);
                if (from.equals("Y"))
                    i.putExtra("PickLater", false);
                else
                    i.putExtra("PickLater", true);
                startActivity(i);
                overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
            }

            @Override
            public void failure(RetrofitError error) {
                if (mprogress.isShowing()) {
                    mprogress.dismiss();
                }
                try {
                    String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                    JSONObject jobj;

                    jobj = new JSONObject(new JSONObject(json).get("error").toString());
                    if (jobj.getString("message") != null) {
                        final Dialog dialog = new Dialog(Activity_Verify_OTP.this);
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
                        Toast.makeText(Activity_Verify_OTP.this, "Error Occurred. Please try again in some time.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
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