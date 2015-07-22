package co.sendd.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import co.sendd.R;
import co.sendd.databases.Db_Item_List;
import co.sendd.gettersandsetters.ShippingDateTime;
import co.sendd.gettersandsetters.ShippingPrice;
import co.sendd.helper.NetworkUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Kuku on 05/03/15.
 */
public class Activity_Shipping_Options extends BaseActivity {
    Button continuebtn, getEstimate;
    EditText height, length, width, weight;
    ProgressDialog pd;
    private RadioButton premium, express, standard;
    private TextView economy1, express1, premium1, ecocost, expcost, premcost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_options);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        economy1 = (TextView) findViewById(R.id.economydate);
        express1 = (TextView) findViewById(R.id.expressdate);
        premium1 = (TextView) findViewById(R.id.premiumdate);
        ecocost = (TextView) findViewById(R.id.tvcostEconomy);
        expcost = (TextView) findViewById(R.id.tvcostExpress);
        premcost = (TextView) findViewById(R.id.tvcostPremimum);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_shipping_options_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_icon_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     finish();
                                                     Activity_Shipping_Options.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
                                                 }
                                             }
        );

        continuebtn = (Button) findViewById(R.id.Continue);
        getEstimate = (Button) findViewById(R.id.getEstimate);

        premium = (RadioButton) findViewById(R.id.radio_premium);
        standard = (RadioButton) findViewById(R.id.radio_regular);
        express = (RadioButton) findViewById(R.id.radio_express);
        if (getIntent().getStringExtra("shipping value") != null) {
            if (getIntent().getStringExtra("shipping value").equalsIgnoreCase("Premium")) {
                premium.setChecked(true);
            } else if (getIntent().getStringExtra("shipping value").equalsIgnoreCase("Standard")) {
                standard.setChecked(true);

            } else if (getIntent().getStringExtra("shipping value").equalsIgnoreCase("Bulk")) {
                express.setChecked(true);

            }
        }
        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Activity_Main.class);
                Db_Item_List itemListDB = new Db_Item_List();
                if (premium.isChecked()) {
                    itemListDB.updateShippingItem("Premium", getIntent().getStringExtra("imageuri"));
                } else if (standard.isChecked()) {
                    itemListDB.updateShippingItem("Standard", getIntent().getStringExtra("imageuri"));
                } else if (express.isChecked()) {
                    itemListDB.updateShippingItem("Bulk", getIntent().getStringExtra("imageuri"));
                }
                startActivity(i);
                Activity_Shipping_Options.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);

            }
        });
        getEstimate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(Activity_Shipping_Options.this);
                        dialog.setTitle("Enter the details");
                        dialog.setContentView(R.layout.dialog_getestimate);
                        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(Color.WHITE)));
                        dialog.show();

                        height = (EditText) dialog.findViewById(R.id.etHeight);
                        length = (EditText) dialog.findViewById(R.id.etLength);
                        width = (EditText) dialog.findViewById(R.id.etWidth);
                        weight = (EditText) dialog.findViewById(R.id.etWeight);
                        dialog.findViewById(R.id.bEstimate).setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (!(TextUtils.isEmpty(height.getText().toString()))) {
                                            if (!TextUtils.isEmpty(length.getText().toString())) {
                                                if (!TextUtils.isEmpty(width.getText().toString())) {
                                                    if (!TextUtils.isEmpty(weight.getText().toString())) {
                                                        pd = new ProgressDialog(Activity_Shipping_Options.this);
                                                        pd.setMessage("Loading, Please wait...");
                                                        pd.setCancelable(false);
                                                        pd.setIndeterminate(true);
                                                        pd.show();

                                                        final NetworkUtils mnetworkutils = new NetworkUtils(Activity_Shipping_Options.this);
                                                        if (mnetworkutils.isnetconnected()) {
                                                            ShippingPrice shippingPrice = new ShippingPrice();
                                                            shippingPrice.setPincode(getIntent().getStringExtra("pincode"));
                                                            shippingPrice.setB(width.getText().toString());
                                                            shippingPrice.setH(height.getText().toString());
                                                            shippingPrice.setL(length.getText().toString());
                                                            shippingPrice.setWeight(weight.getText().toString());
                                                            mnetworkutils.getapi().getPrice(shippingPrice, new Callback<ShippingPrice>() {
                                                                @Override
                                                                public void success(ShippingPrice shippingPrice1, Response response) {
                                                                    ecocost.setVisibility(View.VISIBLE);
                                                                    expcost.setVisibility(View.VISIBLE);
                                                                    premcost.setVisibility(View.VISIBLE);
                                                                    ecocost.setText(shippingPrice1.getStandard());
                                                                    expcost.setText(shippingPrice1.getEconomy());
                                                                    premcost.setText(shippingPrice1.getPremium());
                                                                    dialog.dismiss();
                                                                    pd.dismiss();
                                                                }

                                                                @Override
                                                                public void failure(RetrofitError error) {
                                                                    Log.i("error", error.toString());

                                                                    pd.dismiss();
                                                                    dialog.dismiss();

                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                        );
                        dialog.setCancelable(true);
                    }
                }
        );
        final NetworkUtils mnetworkutils = new NetworkUtils(Activity_Shipping_Options.this);
        if (mnetworkutils.isnetconnected()) {
            ShippingDateTime shippingDateTime = new ShippingDateTime();
            shippingDateTime.setPincode(getIntent().getStringExtra("pincode"));
            mnetworkutils.getapi().getDate(shippingDateTime, new Callback<ShippingDateTime>() {
                @Override
                public void success(ShippingDateTime shippingDateTime1, Response response) {
                    economy1.setText(shippingDateTime1.getStandard());
                    express1.setText(shippingDateTime1.getEconomy());
                    premium1.setText(shippingDateTime1.getPremium());
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("error", error.toString());
                }

            });
        }
    }

    //if Back button is pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Activity_Shipping_Options.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
    }
}