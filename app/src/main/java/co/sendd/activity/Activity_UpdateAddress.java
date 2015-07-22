package co.sendd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.sendd.R;
import co.sendd.databases.Db_Address_Receiver;
import co.sendd.databases.Db_Item_List;
import co.sendd.gettersandsetters.Address;
import co.sendd.gettersandsetters.ItemList;


/**
 * Created by Kuku on 19/02/15.
 */
public class Activity_UpdateAddress extends ActionBarActivity implements TextWatcher {

    String[] pincodes;
    AutoCompleteTextView etReceiverPincode;
    boolean matched;
    private Button SaveAddress;
    private EditText etReceiverName, etReceiverFlatno, etReceiverNumber, etReceiverCountry, etReceiverLocality, etReceiverCity, etReceiverState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_address);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        pincodes = getResources().getStringArray(R.array.pincode);
        ArrayAdapter adapter = new ArrayAdapter
                (this, android.R.layout.simple_list_item_1, pincodes);

        etReceiverName = (EditText) findViewById(R.id.etactivity_update_address_receiver_Name);
        etReceiverName.addTextChangedListener(this);
        etReceiverFlatno = (EditText) findViewById(R.id.etactivity_update_address_receiver_flatno);
        etReceiverFlatno.addTextChangedListener(this);
        etReceiverNumber = (EditText) findViewById(R.id.etactivity_update_address_receiver_Phone);
        etReceiverNumber.addTextChangedListener(this);
        etReceiverCountry = (EditText) findViewById(R.id.etactivity_update_address_Country);
        etReceiverCountry.addTextChangedListener(this);
        etReceiverLocality = (EditText) findViewById(R.id.etactivity_update_address_Locality);
        etReceiverLocality.addTextChangedListener(this);
        etReceiverCity = (EditText) findViewById(R.id.etactivity_update_address_City);
        etReceiverCity.addTextChangedListener(this);
        etReceiverState = (EditText) findViewById(R.id.etactivity_update_address_State);
        etReceiverState.addTextChangedListener(this);
        etReceiverPincode = (AutoCompleteTextView) findViewById(R.id.etactivity_update_address_Pincode);
        etReceiverPincode.setAdapter(adapter);
        etReceiverPincode.addTextChangedListener(this);


        if (getIntent() != null) {
            etReceiverName.setText(getIntent().getStringExtra("U_Name"));
            etReceiverFlatno.setText(getIntent().getStringExtra("U_Flatno"));
            etReceiverNumber.setText(getIntent().getStringExtra("U_Number"));
            etReceiverCountry.setText(getIntent().getStringExtra("U_Country"));
            etReceiverLocality.setText(getIntent().getStringExtra("U_Locality"));
            etReceiverCity.setText(getIntent().getStringExtra("u_City"));
            etReceiverPincode.setText(getIntent().getStringExtra("U_Pincode"));
            etReceiverState.setText(getIntent().getStringExtra("U_State"));

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_update_address_receiver_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_icon_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     finish();
                                                     Activity_UpdateAddress.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
                                                 }
                                             }
        );
        SaveAddress = (Button) findViewById(R.id.bactivity_update_address_SaveAddress);
        SaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etReceiverName.getText().toString())) {
                    if (!TextUtils.isEmpty(etReceiverNumber.getText().toString())) {
                        if (etReceiverNumber.getText().length() == 10) {
                            if (!TextUtils.isEmpty(etReceiverCountry.getText().toString())) {
                            if (!TextUtils.isEmpty(etReceiverLocality.getText().toString())) {
                                if (!TextUtils.isEmpty(etReceiverFlatno.getText().toString())) {
                                    if (!TextUtils.isEmpty(etReceiverCity.getText().toString())) {
                                        if (!TextUtils.isEmpty(etReceiverState.getText().toString())) {
                                            if (!TextUtils.isEmpty(etReceiverPincode.getText().toString())) {
                                                for (int i=0;i<  pincodes.length;i++) {
                                                    if(pincodes[i].equals(etReceiverPincode.getText().toString())){
                                                        Log.i("dkjhlas",pincodes[i]);
                                                        matched = true;
                                                        break;
                                                    }
                                                }
                                                if (matched) {
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
                                                    address_receiverDB.update(address);
                                                    ItemList itemlist = new ItemList();
                                                    Db_Item_List itemlistDB = new Db_Item_List();

                                                    itemlist.setImage_URI(getIntent().getStringExtra("ImageURI"));
                                                    itemlist.setReceiver_Name(etReceiverName.getText().toString());
                                                    itemlist.setPhone(etReceiverNumber.getText().toString());
                                                    itemlist.setFlat_no(etReceiverFlatno.getText().toString());
                                                    itemlist.setLocality(etReceiverLocality.getText().toString());
                                                    itemlist.setCity(etReceiverCity.getText().toString());
                                                    itemlist.setState(etReceiverState.getText().toString());
                                                    itemlist.setCountry(etReceiverCountry.getText().toString());
                                                    itemlist.setPinCode(etReceiverPincode.getText().toString());
                                                    itemlistDB.update(itemlist);
                                                    Log.i("Db_Item_List Updated ", "Item Updated:-)");
                                                    Intent i =new Intent(getApplicationContext(),Activity_Main.class);
                                                    startActivity(i);
                                                    Activity_UpdateAddress.this.overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

                                                } else {
                                                    Toast.makeText(Activity_UpdateAddress.this, "Currently we Don't serve in the provided location", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                etReceiverPincode.setError("* Pincode is required");
                                            }
                                        } else {
                                            etReceiverState.setError("* State is required");
                                        }
                                    } else {
                                        etReceiverCity.setError("* City is required");
                                    }
                                } else {
                                    etReceiverFlatno.setError("* Flatno is required");
                                }
                            } else {
                                etReceiverLocality.setError("* Locality is required");
                            }
                        } else {
                            etReceiverCountry.setError("* Country is required");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Activity_UpdateAddress.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
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