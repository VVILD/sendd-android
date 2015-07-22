package co.sendd.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import co.sendd.R;
import co.sendd.activity.Activity_Main;
import co.sendd.gettersandsetters.ShippingDateTime;
import co.sendd.gettersandsetters.ShippingPrice;
import co.sendd.helper.NetworkUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static co.sendd.helper.NetworkUtils.hideSoftKeyboard;

/**
 * Created by harshkaranpuria on 3/30/15.
 */

/**
 * Created by Kuku on 18/02/15.
 */
public class Fragment_Rate_Calculator extends Fragment {
    EditText weight;
    AutoCompleteTextView Pincode;
    String[] pincodes;
    Button estimate;
    Boolean matched = false;
    ProgressDialog pd;
    TableLayout rl;
    TextView call;
    private TextView standardtime, bulktime, premiumtime, standardcost, bulkcost, premcost;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_rate_calculator, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((Activity_Main) getActivity()).setActionBarTitle("Rate Calculator");
        Activity_Main.exit = false;
        if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
            Activity_Main.mProgress.dismiss();
        }
        pincodes = getResources().getStringArray(R.array.pincode);
        call = (TextView) view.findViewById(R.id.tv11111);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:+" + "918080028081"));
                startActivity(callIntent);
                getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

            }
        });
        ArrayAdapter adapter = new ArrayAdapter
                (getActivity(), android.R.layout.simple_list_item_1, pincodes);
        estimate = (Button) view.findViewById(R.id.bEstimate);
        rl = (TableLayout) view.findViewById(R.id.hideRl);
        standardtime = (TextView) view.findViewById(R.id.tvtimeStandard);
        bulktime = (TextView) view.findViewById(R.id.tvtimeBulk);
        premiumtime = (TextView) view.findViewById(R.id.tvtimePremimum);
        standardcost = (TextView) view.findViewById(R.id.tvcostStandard);
        bulkcost = (TextView) view.findViewById(R.id.tvcostBulk);
        premcost = (TextView) view.findViewById(R.id.tvcostPremimum);
        weight = (EditText) view.findViewById(R.id.etWeight);
        Pincode = (AutoCompleteTextView) view.findViewById(R.id.etPincode);
        Pincode.setAdapter(adapter);
        estimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(getActivity());


                if (!TextUtils.isEmpty(Pincode.getText().toString())) {
                    for (String pincode : pincodes) {
                        if (pincode.equals(Pincode.getText().toString())) {
                            matched = true;
                            break;
                        }
                    }
                    if (matched) {
                        final NetworkUtils mnetworkutils = new NetworkUtils(getActivity());
                        if (mnetworkutils.isnetconnected()) {
                            ShippingDateTime shippingDateTime = new ShippingDateTime();
                            shippingDateTime.setPincode(Pincode.getText().toString());
                            mnetworkutils.getapi().getDate(shippingDateTime, new Callback<ShippingDateTime>() {
                                @Override
                                public void success(ShippingDateTime shippingDateTime1, Response response) {
                                    rl.setVisibility(View.VISIBLE);
                                    standardtime.setText(shippingDateTime1.getStandard());
                                    bulktime.setText(shippingDateTime1.getEconomy());
                                    premiumtime.setText(shippingDateTime1.getPremium());
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Toast.makeText(getActivity(), "Error please try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if (!TextUtils.isEmpty(weight.getText().toString())) {
                            pd = new ProgressDialog(getActivity());
                            pd.setMessage("Checking Rates, Please wait...");
                            pd.setCancelable(false);
                            pd.setIndeterminate(true);
                            pd.show();

                            if (mnetworkutils.isnetconnected()) {
                                ShippingPrice shippingPrice = new ShippingPrice();
                                shippingPrice.setPincode(Pincode.getText().toString());
                                shippingPrice.setWeight(weight.getText().toString());
                                mnetworkutils.getapi().getPrice(shippingPrice, new Callback<ShippingPrice>() {
                                    @Override
                                    public void success(ShippingPrice shippingPrice1, Response response) {
                                        standardcost.setVisibility(View.VISIBLE);
                                        bulkcost.setVisibility(View.VISIBLE);
                                        premcost.setVisibility(View.VISIBLE);
                                        standardcost.setText(shippingPrice1.getStandard());
                                        bulkcost.setText(shippingPrice1.getEconomy());
                                        premcost.setText(shippingPrice1.getPremium());
                                        pd.dismiss();
                                        rl.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        pd.dismiss();

                                    }
                                });

                            }
                        }
                    } else {
                        rl.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Currently we Don't serve in the provided location", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Pincode.setError("* Pincode is required");
                }

            }
        });

    }

    public void onResume() {
        super.onResume();
        ((Activity_Main) getActivity()).setActionBarTitle("Rate Calculator");
        Activity_Main.exit = false;
        if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
            Activity_Main.mProgress.dismiss();
        }
    }
}
