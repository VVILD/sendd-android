package co.sendd.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.sendd.gettersandsetters.ItemList;
import co.sendd.R;
import co.sendd.databases.Db_Address_Receiver;
import co.sendd.databases.Db_Item_List;
import co.sendd.gettersandsetters.Address;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Kuku on 19/02/15.
 */
public class Activity_UpdateAddress extends ActionBarActivity {
 
    private Button SaveAddress;
    private EditText etReceiverName, etReceiverFlatno, etReceiverNumber, etReceiverCountry, etReceiverLocality, etReceiverCity, etReceiverState;
    int id;
    String[] pincodes;
    AutoCompleteTextView etReceiverPincode;
    boolean matched;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_address);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        pincodes = getResources().getStringArray(R.array.pincode);
        ArrayAdapter adapter = new ArrayAdapter
                (this, android.R.layout.simple_list_item_1, pincodes);

        etReceiverName = (EditText) findViewById(R.id.etactivity_update_address_receiver_Name);
        etReceiverFlatno = (EditText) findViewById(R.id.etactivity_update_address_receiver_flatno);
        etReceiverNumber = (EditText) findViewById(R.id.etactivity_update_address_receiver_Phone);
        etReceiverCountry = (EditText) findViewById(R.id.etactivity_update_address_Country);
        etReceiverLocality = (EditText) findViewById(R.id.etactivity_update_address_Locality);
        etReceiverCity = (EditText) findViewById(R.id.etactivity_update_address_City);
        etReceiverState = (EditText) findViewById(R.id.etactivity_update_address_State);
        etReceiverPincode = (AutoCompleteTextView) findViewById(R.id.etactivity_update_address_Pincode);
        etReceiverPincode.setAdapter(adapter);

        if (getIntent() != null) {
            etReceiverName.setText(getIntent().getStringExtra("U_Name"));
            etReceiverFlatno.setText(getIntent().getStringExtra("U_Flatno"));
            etReceiverNumber.setText(getIntent().getStringExtra("U_Number"));
            etReceiverCountry.setText(getIntent().getStringExtra("U_Country"));
            etReceiverLocality.setText(getIntent().getStringExtra("U_Locality"));
            etReceiverCity.setText(getIntent().getStringExtra("u_City"));
            etReceiverPincode.setText(getIntent().getStringExtra("U_Pincode"));
            etReceiverState.setText(getIntent().getStringExtra("U_State"));
            id=getIntent().getExtras().getInt("ID");

        }
        etReceiverName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //do job here owhen Edittext lose focus
                    if (!TextUtils.isEmpty(etReceiverName.getText().toString())) {
                        etReceiverName.setError(null);
                    }
                }
            }
        });
        etReceiverFlatno.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //do job here owhen Edittext lose focus
                    if (!TextUtils.isEmpty(etReceiverFlatno.getText().toString())) {
                        etReceiverFlatno.setError(null);
                    }
                }
            }
        });
        etReceiverNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //do job here owhen Edittext lose focus
                    if (!TextUtils.isEmpty(etReceiverNumber.getText().toString())) {
                        etReceiverNumber.setError(null);
                    }
                }
            }
        });
        etReceiverCountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //do job here owhen Edittext lose focus
                    if (!TextUtils.isEmpty(etReceiverCountry.getText().toString())) {
                        etReceiverCountry.setError(null);
                    }
                }
            }
        });
        etReceiverLocality.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //do job here owhen Edittext lose focus
                    if (!TextUtils.isEmpty(etReceiverLocality.getText().toString())) {
                        etReceiverLocality.setError(null);
                    }
                }
            }
        });
        etReceiverCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //do job here owhen Edittext lose focus
                    if (!TextUtils.isEmpty(etReceiverCity.getText().toString())) {
                        etReceiverCity.setError(null);
                    }
                }
            }
        });
        etReceiverState.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //do job here owhen Edittext lose focus
                    if (!TextUtils.isEmpty(etReceiverState.getText().toString())) {
                        etReceiverState.setError(null);
                    }
                }
            }
        });
        etReceiverPincode.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //do job here owhen Edittext lose focus
                    if (!TextUtils.isEmpty(etReceiverPincode.getText().toString())) {
                        etReceiverPincode.setError(null);
                    }
                }
            }
        });
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

}