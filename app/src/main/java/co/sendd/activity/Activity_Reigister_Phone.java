package co.sendd.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.sendd.R;
import co.sendd.gettersandsetters.RegisterUser;
import co.sendd.gettersandsetters.Users;
import co.sendd.helper.NetworkUtils;
import co.sendd.helper.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by harshkaranpuria on 4/27/15.
 */
public class Activity_Reigister_Phone extends BaseActivity {
    public static Activity regPhone;
    EditText Phonenumber;
    Button Register;
    ProgressDialog pd;
    Boolean isLoginTrue, isPreviousShipmentTrue;
    Utils utils;
    String DeviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);
        regPhone = this;
        utils = new Utils(this);
        TelephonyManager tm = (TelephonyManager) this.getSystemService(android.content.Context.TELEPHONY_SERVICE);
        if (tm.getDeviceId() != null) {
            DeviceId = tm.getDeviceId();
        } else {
            DeviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.registerToolbar);
        setSupportActionBar(toolbar);
        isLoginTrue = getIntent().getBooleanExtra("Login", false);
        isPreviousShipmentTrue = getIntent().getBooleanExtra("PreviousShipments", false);

        toolbar.setNavigationIcon(R.drawable.arrow_back_icon_small);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Activity_Reigister_Phone.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
                    }
                }
        );
        if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
            Activity_Main.mProgress.dismiss();

        }
        Phonenumber = (EditText) findViewById(R.id.etphonenumber);
        Register = (Button) findViewById(R.id.bRegistrationBtn);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd = new ProgressDialog(Activity_Reigister_Phone.this);
                pd.setMessage("Loading, Please wait...");
                pd.setCancelable(false);
                pd.setIndeterminate(true);

                if (!TextUtils.isEmpty(Phonenumber.getText().toString())) {
                    if (Phonenumber.getText().length() == 10) {
                        final NetworkUtils mnetworkutils = new NetworkUtils(Activity_Reigister_Phone.this);
                        if (mnetworkutils.isnetconnected()) {
                            RegisterUser userdetails = new RegisterUser();
                            userdetails.setPhone(Phonenumber.getText().toString());
                            userdetails.setGCMRegId(utils.getvalue("regid"));
                            userdetails.setDeviceid(DeviceId);
                            Log.i("DeviceIdDeviceId", DeviceId);
                            pd.show();
                            mnetworkutils.getapi().register(userdetails, new Callback<Users>() {
                                @Override
                                public void success(Users user1, Response response) {
                                    if (pd != null && pd.isShowing()) {
                                        pd.dismiss();
                                    }
                                    utils.setvalue("Registered_on_server", "yes");
                                    Intent i = new Intent(getApplicationContext(), Activity_Verify_OTP.class);
                                    i.putExtra("PickupOption", getIntent().getStringExtra("PickupOption"));
                                    i.putExtra("NUMBER", Phonenumber.getText().toString());
                                    i.putExtra("DATE", getIntent().getStringExtra("DATE"));
                                    i.putExtra("TIME", getIntent().getStringExtra("TIME"));
                                    i.putExtra("PreviousShipments", isPreviousShipmentTrue);
                                    i.putExtra("Login", isLoginTrue);
                                    i.putExtra("adapterCount", getIntent().getIntExtra("adapterCount", 0));
                                    startActivity(i);
                                    overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    if (pd != null && pd.isShowing()) {
                                        pd.dismiss();
                                        Log.i("Error:",error.toString());
                                        Toast.makeText(Activity_Reigister_Phone.this, "Error in registering your phone number. Plese try again in some time.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Activity_Reigister_Phone.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
    }

}