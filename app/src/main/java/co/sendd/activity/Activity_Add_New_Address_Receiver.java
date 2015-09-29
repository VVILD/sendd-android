package co.sendd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import co.sendd.R;
import co.sendd.databases.Db_Address_Receiver;
import co.sendd.databases.Db_Item_List;
import co.sendd.gettersandsetters.Address;
import co.sendd.gettersandsetters.ItemList;
import co.sendd.helper.Utils;


public class Activity_Add_New_Address_Receiver extends BaseActivity implements TextWatcher {
    String[] pincodes;
    boolean matched = false;
    private Button SaveAddress;
    private EditText etReceiverName, etReceiverFlatno, etReceiverNumber, etReceiverCountry, etReceiverLocality, etReceiverCity, etReceiverState;
    private AutoCompleteTextView etReceiverPincode;

    public void Init() {

        etReceiverName = (EditText) findViewById(R.id.etactivity_add_new_address_receiver_Name);
        etReceiverName.addTextChangedListener(this);

        etReceiverFlatno = (EditText) findViewById(R.id.etactivity_add_new_address_receiver_flatno);
        etReceiverFlatno.addTextChangedListener(this);

        etReceiverNumber = (EditText) findViewById(R.id.etactivity_add_new_address_receiver_Phone);
        etReceiverNumber.addTextChangedListener(this);

        etReceiverCountry = (EditText) findViewById(R.id.etactivity_add_new_address_receiver_Country);
        etReceiverCountry.addTextChangedListener(this);

        etReceiverLocality = (EditText) findViewById(R.id.etactivity_add_new_address_receiver_Locality);
        etReceiverLocality.addTextChangedListener(this);

        etReceiverCity = (EditText) findViewById(R.id.etactivity_add_new_address_receiver_City);
        etReceiverCity.addTextChangedListener(this);

        etReceiverState = (EditText) findViewById(R.id.etactivity_add_new_address_receiver_State);
        etReceiverState.addTextChangedListener(this);

        etReceiverPincode = (AutoCompleteTextView) findViewById(R.id.etactivity_add_new_address_receiver_Pincode);
        etReceiverPincode.addTextChangedListener(this);
        pincodes = getResources().getStringArray(R.array.pincode);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pincodes);
        etReceiverPincode.setAdapter(adapter);

        if (getIntent() != null) {
            etReceiverCountry.setText(getIntent().getStringExtra("Country"));
            etReceiverLocality.setText(getIntent().getStringExtra("Locality"));
            etReceiverCity.setText(getIntent().getStringExtra("City"));
            etReceiverState.setText(getIntent().getStringExtra("State"));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_add_new_address_receiver_toolbar);
        SaveAddress = (Button) findViewById(R.id.bactivity_add_new_address_receiver_SaveAddress);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_icon_small);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Activity_Add_New_Address_Receiver.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
                    }
                }
        );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address_receiver);
        setupUI(findViewById(R.id.main_parent));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Init();
        SaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etReceiverName.getText().toString())) {
                    if (!TextUtils.isEmpty(etReceiverNumber.getText().toString())) {
                        if (etReceiverNumber.getText().length() == 10) {
                            if (!TextUtils.isEmpty(etReceiverFlatno.getText().toString())) {
                                if (!TextUtils.isEmpty(etReceiverLocality.getText().toString())) {
                                    if (!TextUtils.isEmpty(etReceiverCity.getText().toString())) {
                                        if (!TextUtils.isEmpty(etReceiverState.getText().toString())) {
                                            if (!TextUtils.isEmpty(etReceiverCountry.getText().toString())) {
                                                if (!TextUtils.isEmpty(etReceiverPincode.getText().toString())) {

                                                    for (String pincode : pincodes) {
                                                        if (pincode.equals(etReceiverPincode.getText().toString())) {
                                                            matched = true;
                                                            break;
                                                        }
                                                    }
                                                    if (matched) {
                                                        //Save Drop Address in DB
                                                        Address address = new Address();
                                                        address.setName(etReceiverName.getText().toString());
                                                        address.setPhone(etReceiverNumber.getText().toString());
                                                        address.setFlat_no(etReceiverFlatno.getText().toString());
                                                        address.setLocality(etReceiverLocality.getText().toString());
                                                        address.setCity(etReceiverCity.getText().toString());
                                                        address.setState(etReceiverState.getText().toString());
                                                        address.setCountry(etReceiverCountry.getText().toString());
                                                        address.setPincode(etReceiverPincode.getText().toString());
                                                        Db_Address_Receiver address_receiverDB = new Db_Address_Receiver();
                                                        address_receiverDB.AddToDB(address);
                                                        //Address Saved

                                                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                                        Utils utils = new Utils(Activity_Add_New_Address_Receiver.this);

                                                        //Create an Item In item list
                                                        ItemList itemlist = new ItemList();
                                                        itemlist.setOrderId(String.valueOf(utils.getOrderId()));
                                                        itemlist.setImage_URI(getIntent().getStringExtra("ImageURI"));
                                                        itemlist.setShipping_Option("Set Shipping Option");
                                                        itemlist.setDate(date);
                                                        itemlist.setReceiver_Name(etReceiverName.getText().toString());
                                                        itemlist.setPhone(etReceiverNumber.getText().toString());
                                                        itemlist.setFlat_no(etReceiverFlatno.getText().toString());
                                                        itemlist.setLocality(etReceiverLocality.getText().toString());
                                                        itemlist.setCity(etReceiverCity.getText().toString());
                                                        itemlist.setState(etReceiverState.getText().toString());
                                                        itemlist.setCountry(etReceiverCountry.getText().toString());
                                                        itemlist.setPinCode(etReceiverPincode.getText().toString());
                                                        Db_Item_List itemlistDB = new Db_Item_List();
                                                        itemlistDB.AddToDB(itemlist);
                                                        //Item Created

                                                        //Open Main Activity(Orders page)
                                                        Intent i = new Intent(getApplicationContext(), Activity_Main.class);
                                                        startActivity(i);
                                                        overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                                                        finish();


                                                    } else {
                                                        Toast.makeText(Activity_Add_New_Address_Receiver.this, "Currently we don't serve in the provided location", Toast.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    etReceiverPincode.setError("* Pincode is required");
                                                }
                                            } else {
                                                etReceiverCountry.setError("* Country is required");
                                            }
                                        } else {
                                            etReceiverState.setError("* State is required");
                                        }
                                    } else {
                                        etReceiverCity.setError("* City is required");
                                    }
                                } else {
                                    etReceiverLocality.setError("* Locality is required");
                                }
                            } else {
                                etReceiverFlatno.setError("* Flatno is required");
                            }
                        } else {
                            etReceiverNumber.setError("* Enter your 10 digit Mobile number");
                        }
                    } else {
                        etReceiverNumber.setError("* Number is required");
                    }
                } else {
                    etReceiverName.setError("* Name is required");
                }
            }
        });
    }
    //if Back button is pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Activity_Add_New_Address_Receiver.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
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
        //Set Error On Null input
        if (!etReceiverName.getText().toString().isEmpty()) etReceiverName.setError(null, null);
        if (!etReceiverFlatno.getText().toString().isEmpty()) etReceiverFlatno.setError(null, null);
        if (!etReceiverNumber.getText().toString().isEmpty()) etReceiverNumber.setError(null, null);
        if (!etReceiverLocality.getText().toString().isEmpty())
            etReceiverLocality.setError(null, null);
        if (!etReceiverCity.getText().toString().isEmpty()) etReceiverCity.setError(null, null);
        if (!etReceiverState.getText().toString().isEmpty()) etReceiverState.setError(null, null);
        if (!etReceiverPincode.getText().toString().isEmpty())
            etReceiverPincode.setError(null, null);
        if (!etReceiverCountry.getText().toString().isEmpty())
            etReceiverCountry.setError(null, null);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}