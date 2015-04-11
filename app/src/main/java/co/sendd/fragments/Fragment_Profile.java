package co.sendd.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import co.sendd.activity.Activity_Main;
import co.sendd.activity.Activity_ViewPager;
import co.sendd.databases.Db_Address_Sender;
import co.sendd.databases.Db_CompleteOrder;
import co.sendd.databases.Db_Item_List;
import co.sendd.databases.Db_User;
import co.sendd.gettersandsetters.RegisterUser;
import co.sendd.gettersandsetters.Users;
import co.sendd.helper.NetworkUtils;
import co.sendd.helper.Utils;
import co.sendd.R;
import co.sendd.databases.Db_Address_Receiver;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Kuku on 19/02/15.
 */
public class Fragment_Profile extends Fragment {
    EditText name, phonenumber, email;
    TextView NameTitle;
    Button Logout, Update;
    ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((Activity_Main) getActivity()).hideActionBar();
        Activity_Main.exit = false;
        Activity_Main.noShipmentTv.setVisibility(View.GONE);
        if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
            Activity_Main.mProgress.dismiss();
        }
        NameTitle = (TextView) view.findViewById(R.id.tvFragment_Profile_Title_Name);
        name = (EditText) view.findViewById(R.id.etFragment_Profile_Name);
        phonenumber = (EditText) view.findViewById(R.id.etFragment_Profile_Phone);
        email = (EditText) view.findViewById(R.id.etFragment_Profile_Email);
        phonenumber.setEnabled(false);
        Logout = (Button) view.findViewById(R.id.bFragment_Profile_Logout);
        Update = (Button) view.findViewById(R.id.UpdateProfile);

        Db_User accessDB = new Db_User();
        if (accessDB.getName() == null) {
            NameTitle.setText("John Doe");

        } else {
            NameTitle.setText(accessDB.getName());
        }
        if (accessDB.getName() == null) {
            name.setText("Hello");

        } else {
            name.setText(accessDB.getName());
        }

        if (accessDB.getPhoneNumber() == null) {
            phonenumber.setText("99 99 99 99 99");

        } else {
            phonenumber.setText(accessDB.getPhoneNumber());
        }
        if (accessDB.getEmail() == null) {
            email.setText("email");

        } else {
            email.setText(accessDB.getEmail());
        }

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Update.getText().toString().equals("Edit Profile")) {
                    Update.setText("Update Profile");
                    name.setEnabled(true);
                    email.setEnabled(true);
                } else {
                    if (!TextUtils.isEmpty(name.getText().toString())) {
                        if (!TextUtils.isEmpty(email.getText().toString())) {
                            pd = new ProgressDialog(getActivity());
                            pd.setMessage("Loading, Please wait...");
                            pd.setCancelable(false);
                            pd.setIndeterminate(true);

                            final NetworkUtils mnetworkutils = new NetworkUtils(getActivity());
                            if (mnetworkutils.isnetconnected()) {
                                RegisterUser userdetails = new RegisterUser();
                                userdetails.setName(name.getText().toString());
                                userdetails.setEmail(email.getText().toString());
                                pd.show();
                                mnetworkutils.getapi().updateprofile(phonenumber.getText().toString(), userdetails, new Callback<Users>() {
                                    @Override
                                    public void success(Users user1, Response response) {
                                        if (pd != null && pd.isShowing()) {
                                            pd.dismiss();
                                        }
                                        Toast.makeText(getActivity(), "Profile updated successfully ", Toast.LENGTH_SHORT).show();

                                        Users user = new Users();
                                        user.setName(user1.getName());
                                        user.setEmail(user1.getEmail());
                                        Db_User userDB = new Db_User();
                                        userDB.update(user);
                                        Update.setText("Edit Profile");
                                        name.setEnabled(false);
                                        email.setEnabled(false);
                                        Log.i("Signup User", "UserAdded");

                                    }


                                    @Override
                                    public void failure(RetrofitError error) {
                                        if (pd != null && pd.isShowing()) {
                                            pd.dismiss();
                                        }
                                        Log.i("Retrofit Error", error.toString());
                                        // Toast.makeText(Activity_Signup.this, "Error with registration please try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } else {
                            email.setError("* Email is required");
                        }
                    } else {
                        name.setError("* Name is required");
                    }
                }
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Db_User userDB = new Db_User();
                Db_CompleteOrder co = new Db_CompleteOrder();
                Db_Item_List il = new Db_Item_List();
                Db_Address_Receiver ar = new Db_Address_Receiver();
                Db_Address_Sender as = new Db_Address_Sender();
                Utils utils = new Utils(getActivity());

                userDB.deleteRecord();
                co.deleteAllItems();
                il.deleteAllItems();
                ar.deleteAllItems();
                as.deleteAllItems();
                utils.clear();

                Toast.makeText(getActivity(), "Successfully logged out",
                        Toast.LENGTH_LONG).show();
                Intent i = new Intent(getActivity(), Activity_ViewPager.class);
                startActivity(i);
                getActivity().finish();
            }
        });
    }

    public void onResume() {
        super.onResume();
        ((Activity_Main) getActivity()).hideActionBar();
        Activity_Main.exit = false;
    }

}
