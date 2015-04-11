package co.sendd.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import co.sendd.R;
import co.sendd.databases.Db_User;
import co.sendd.gettersandsetters.ForgotPass;
import co.sendd.gettersandsetters.Register;
import co.sendd.gettersandsetters.Users;
import co.sendd.helper.NetworkUtils;
import co.sendd.helper.Utils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static co.sendd.helper.Utils.hideSoftKeyboard;

/**
 * Created by Kuku on 15/02/15.
 */
public class Activity_Login extends BaseActivity implements TextWatcher {
    private Button bALSignup, bALLogin;
    private EditText etPassword, etPhone;
    ProgressDialog pd;
    private TextView ForgotPassword;

    //Set Font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    //On Activity Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ForgotPassword = (TextView) findViewById(R.id.tvLoginPgForgetYourPassword);
        etPassword = (EditText) findViewById(R.id.etLoginPgPassword);

        etPhone = (EditText) findViewById(R.id.etLoginPgPhoneNumber);
        bALLogin = (Button) findViewById(R.id.bLoginPgSubmit);
        bALSignup = (Button) findViewById(R.id.bLoginPgSignup);
        etPassword.addTextChangedListener(this);
        etPhone.addTextChangedListener(this);
        bALLogin.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            hideSoftKeyboard(Activity_Login.this);
                                            if (!TextUtils.isEmpty(etPhone.getText().toString())) {
                                                if (!TextUtils.isEmpty(etPassword.getText().toString())) {
                                                    pd = new ProgressDialog(Activity_Login.this);
                                                    pd.setMessage("Loading, Please wait...");
                                                    pd.setCancelable(false);
                                                    pd.setIndeterminate(true);
                                                    pd.show();

                                                    final NetworkUtils mnetworkutils = new NetworkUtils(Activity_Login.this);
                                                    if (mnetworkutils.isnetconnected()) {
                                                        Register register = new Register();
                                                        register.setPhone(etPhone.getText().toString());
                                                        register.setPassword(etPassword.getText().toString());

                                                        pd.show();
                                                        mnetworkutils.getapi().login(register, new Callback<Register>() {
                                                            @Override
                                                            public void success(Register user1, Response response) {
                                                                if (pd != null && pd.isShowing()) {
                                                                    pd.dismiss();
                                                                }
                                                                if (user1.getSuccess().equals("notregistered")) {
                                                                    Toast.makeText(Activity_Login.this, "Please register to continue", Toast.LENGTH_SHORT).show();
                                                                } else if (user1.getSuccess().equals("wrongpassword")) {
                                                                    Toast.makeText(Activity_Login.this, "Invalid details entered", Toast.LENGTH_SHORT).show();
                                                                } else if (user1.getSuccess().equals("success")) {
                                                                    Log.i("asdfsdf", user1.getName() + user1.getApikey() + user1.getEmail());
                                                                    Users user = new Users();
                                                                    user.setName(user1.getName());
                                                                    user.setPhone(user1.getPhone());
                                                                    user.setEmail(user1.getEmail());
                                                                    user.setApikey(user1.getApikey());
                                                                    Db_User userDB = new Db_User();
                                                                    userDB.AddToDB(user);
                                                                    Log.i("User LoggedIn", "UserAdded");
                                                                    Utils utils = new Utils(Activity_Login.this);
                                                                    utils.Registered();
                                                                    utils.setvalue("RegisteredPhone", user1.getPhone());
                                                                    Activity_ViewPager.ViewPager.finish();

                                                                    Intent i = new Intent(getApplicationContext(), Activity_Main.class);
                                                                    startActivity(i);
                                                                    Activity_Login.this.overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                                                                    finish();
                                                                }
                                                            }

                                                            @Override
                                                            public void failure(RetrofitError error) {
                                                                if (pd != null && pd.isShowing()) {
                                                                    pd.dismiss();
                                                                }
                                                                Toast.makeText(Activity_Login.this, "Error with Login please try again", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }

                                                } else {
                                                    etPassword.setError("* Password is required");
                                                }
                                            } else

                                            {
                                                etPhone.setError("* Number is required");
                                            }

                                        }
                                    }

        );
        bALSignup.setOnClickListener(new View.OnClickListener()

                                     {
                                         @Override
                                         public void onClick(View v) {
                                             Intent i = new Intent(getApplicationContext(), Activity_Signup.class);
                                             startActivity(i);
                                             Activity_Login.this.overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                                             finish();
                                         }
                                     }

        );
        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(Activity_Login.this);
                if (!TextUtils.isEmpty(etPhone.getText().toString())) {
                    final ProgressDialog pd = new ProgressDialog(Activity_Login.this);
                    pd.setMessage("Loading, Please wait...");
                    pd.setCancelable(false);
                    pd.setIndeterminate(true);
                    pd.show();

                    final NetworkUtils mnetworkutils = new NetworkUtils(Activity_Login.this);
                    if (mnetworkutils.isnetconnected()) {
                        ForgotPass fg = new ForgotPass();
                        fg.setPhone(etPhone.getText().toString());
                        mnetworkutils.getapi().forgotPassword(fg, new Callback<ForgotPass>() {
                            @Override
                            public void success(ForgotPass user1, Response response) {
                                if (pd != null && pd.isShowing()) {
                                    pd.dismiss();
                                }
                                if (user1.getMsg().equals("user not exist")) {
                                    Toast.makeText(Activity_Login.this, "Entered number is not registered", Toast.LENGTH_SHORT).show();
                                } else if (user1.getMsg().equals("user exists")) {
                                    Toast.makeText(Activity_Login.this, "A link is sent to your phone to change your password.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                if (pd != null && pd.isShowing()) {
                                    pd.dismiss();
                                }
                                Toast.makeText(Activity_Login.this, "Error please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } else {
                    etPhone.setError("Enter the phone number registered with us.");
                }
            }
        });

    }

    //On Pause
    public void onPause() {
        super.onPause();
        finish();
    }

    //TEXT WATCHER
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {

        if (!etPhone.getText().toString().isEmpty()) etPhone.setError(null);
        if (!etPassword.getText().toString().isEmpty()) etPassword.setError(null);
    }
}
