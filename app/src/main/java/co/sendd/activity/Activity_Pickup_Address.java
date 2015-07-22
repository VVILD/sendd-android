package co.sendd.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.sendd.R;
import co.sendd.gettersandsetters.Pincode;
import co.sendd.helper.FetchAddressIntentService;
import co.sendd.helper.NetworkUtils;
import co.sendd.helper.PlacesAdapter;
import co.sendd.helper.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by Kuku on 19/02/15.
 */
public class Activity_Pickup_Address extends BaseActivity implements AdapterView.OnItemClickListener, TextWatcher, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected static final String TAG = "main-activity";
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected boolean mAddressRequested;
    protected String mLocality, mPincode;
    protected Double mLat, mLong;
    ProgressDialog pd;
    private Button SaveAddress;
    private EditText etName, etEmail, etAddress, etPincode;
    private AutoCompleteTextView etLocality;
    private AddressResultReceiver mResultReceiver;

    public void Init() {

        etName = (EditText) findViewById(R.id.etNamePickup);
        etName.addTextChangedListener(this);
        etEmail = (EditText) findViewById(R.id.etEmailPickup);
        etEmail.addTextChangedListener(this);
        etAddress = (EditText) findViewById(R.id.etAddressPickup);
        etAddress.addTextChangedListener(this);
        etLocality = (AutoCompleteTextView) findViewById(R.id.etLocalityPickup);
        etLocality.addTextChangedListener(this);
        etLocality.setAdapter(new PlacesAdapter(Activity_Pickup_Address.this, R.layout.autocomplete_list));
        etLocality.setOnItemClickListener(this);
        etPincode = (EditText) findViewById(R.id.etPincodePickup);
        etPincode.addTextChangedListener(this);

        if (getIntent() != null) {
            etAddress.setText(getIntent().getStringExtra("Country"));
            etPincode.setText(getIntent().getStringExtra("Locality"));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.mapstoolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_icon_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     finish();
                                                     Activity_Pickup_Address.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
                                                 }
                                             }
        );
        SaveAddress = (Button) findViewById(R.id.bSavePickup);

    }

    //On Activity Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickupaddress);
        setupUI(findViewById(R.id.main_parent));

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //Initialize EditTexts
        Init();
        mResultReceiver = new AddressResultReceiver(new Handler());
        Utils utils1 = new Utils(Activity_Pickup_Address.this);
        mAddressRequested = false;
        mLocality = "";
        if (utils1.getvalue("PickupAddress") == "") {
            updateValuesFromBundle(savedInstanceState);
            buildGoogleApiClient();
            fetchAddressButtonHandler();
        } else {
            updateValuesFromBundle(savedInstanceState);
            buildGoogleApiClient();
            etName.setText(utils1.getvalue("Sender_Name"));
            etEmail.setText(utils1.getvalue("Sender_Email"));
            etAddress.setText(utils1.getvalue("PickupFlatNo"));
            etLocality.setText(utils1.getvalue("PickupAddress"));
            etPincode.setText(utils1.getvalue("PickupPincode"));

        }
        //Save New Address And Open Camera
        SaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd = new ProgressDialog(Activity_Pickup_Address.this);
                pd.setMessage("Please wait.");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
                final NetworkUtils mnetworkutils = new NetworkUtils(Activity_Pickup_Address.this);
                Pincode mPincode = new Pincode();
                mPincode.setPincode(etPincode.getText().toString());
                if (mnetworkutils.isnetconnected()) {
                    //VERIFY OTP
                    mnetworkutils.getapi().checkPincode(mPincode, new Callback<Pincode>() {
                        @Override
                        public void success(Pincode mPincode, Response response) {
                            pd.dismiss();
                            if (mPincode.getValid().equals("0")) {
                                Toast.makeText(Activity_Pickup_Address.this, mPincode.getMsg(), Toast.LENGTH_LONG).show();
                            }
                            if (mPincode.getValid().equals("1")) {
                                if (!TextUtils.isEmpty(etName.getText().toString())) {
                                    if (!TextUtils.isEmpty(etEmail.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
                                        if (!TextUtils.isEmpty(etAddress.getText().toString())) {
                                            if (!TextUtils.isEmpty(etLocality.getText().toString())) {
                                                if (!TextUtils.isEmpty(etPincode.getText().toString())) {
                                                    if (TextUtils.isDigitsOnly(etPincode.getText().toString())) {
                                                        Utils utils = new Utils(Activity_Pickup_Address.this);
                                                        utils.setvalue("Sender_Name", etName.getText().toString());
                                                        utils.setvalue("Sender_Email", etEmail.getText().toString().trim());
                                                        if (mLat != null && mLong != null) {
                                                            utils.setvalue("Lat", Double.toString(mLat));
                                                            utils.setvalue("Longi", Double.toString(mLong));
                                                        }
                                                        utils.setvalue("PickupAddress", etLocality.getText().toString());
                                                        utils.setvalue("PickupFlatNo", etAddress.getText().toString());
                                                        utils.setvalue("PickupPincode", etPincode.getText().toString());
                                                        Intent i = new Intent(Activity_Pickup_Address.this, Activity_Main.class);
                                                        startActivity(i);
                                                        finish();
                                                        Activity_Pickup_Address.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);

                                                    } else {
                                                        etPincode.setError("* Incorrect Pincode is required");
                                                    }
                                                } else {
                                                    etPincode.setError("* Pincode is required");
                                                }
                                            } else {
                                                etLocality.setError("* Locality is required");
                                            }
                                        } else {
                                            etAddress.setError("* Address is required");
                                        }
                                    } else {
                                        etEmail.setError("* Enter a Valid Email");
                                    }
                                } else {
                                    etName.setError("* Name is required");
                                }
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            pd.dismiss();
                            Log.i("Clicked", error.toString());
                        }
                    });
                } else {
                    Toast.makeText(Activity_Pickup_Address.this, "Not connected to network", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        });
    }


    //if Back button is pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Activity_Pickup_Address.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
    }


    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
        setupUI(findViewById(R.id.main_parent));
    }

    //TEXT WATCHER options
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!etName.getText().toString().isEmpty()) etName.setError(null, null);
        if (!etEmail.getText().toString().isEmpty()) etEmail.setError(null, null);
        if (!etAddress.getText().toString().isEmpty()) etAddress.setError(null, null);
        if (!etPincode.getText().toString().isEmpty()) etPincode.setError(null, null);
        if (!etLocality.getText().toString().isEmpty()) etLocality.setError(null, null);

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }

            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mLocality = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    public void fetchAddressButtonHandler() {
        // We only start the service to fetch the address if GoogleApiClient is connected.
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }

        mAddressRequested = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            if (!Geocoder.isPresent()) {

                return;
            }
            if (mAddressRequested) {
                startIntentService();
            }
        }
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra("com.google.android.gms.location.sample.locationaddress.RECEIVER", mResultReceiver);
        intent.putExtra("com.google.android.gms.location.sample.locationaddress.LOCATION_DATA_EXTRA", mLastLocation);
        startService(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    protected void displayAddressOutput() {
        etLocality.setText(mLocality);
        etPincode.setText(mPincode);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mLocality);
        super.onSaveInstanceState(savedInstanceState);
    }

    // On clicking any suggestions form AUto Text View
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        etLocality.setText(str);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String Pincode = "";
            Pattern pattern = Pattern.compile("\\d\\d\\d\\d\\d\\d");
            Matcher matcher = pattern.matcher(resultData.getString("Pincode"));
            while (matcher.find()) {
                try {
                    Pincode = String.valueOf(matcher.group());
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }

            mLocality = resultData.getString("com.google.android.gms.location.sample.locationaddress.RESULT_DATA_KEY");
            mPincode = Pincode;
            mLat = resultData.getDouble("Lat");
            mLong = resultData.getDouble("Long");
            displayAddressOutput();
            mAddressRequested = false;
        }
    }
}