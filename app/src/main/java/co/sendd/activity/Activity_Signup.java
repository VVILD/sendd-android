package co.sendd.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import co.sendd.databases.Db_User;
import co.sendd.gettersandsetters.RegisterUser;
import co.sendd.gettersandsetters.Users;
import co.sendd.helper.NetworkUtils;
import co.sendd.helper.OTP_Check;
import co.sendd.helper.Utils;
import co.sendd.R;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Kuku on 15/02/15.
 */
public class Activity_Signup extends BaseActivity implements TextWatcher {
    private Button bASSignup, bASLogin;
    private EditText etName, etEmail, etPassword, etPhone;
    ProgressDialog pd;
    TextView tnc;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        tnc =(TextView) findViewById(R.id.terms_and_condition);
        tnc.setText(
                Html.fromHtml("By creating sendd account, you agree\\nto the "+"<a href=\"http://sendd.co/terms.html\">Terms of Service</a> "+"and "+"<a href=\"http://sendd.co/privacy.html\">Privacy Policy</a>"));
        tnc.setMovementMethod(LinkMovementMethod.getInstance());
        etEmail = (EditText) findViewById(R.id.etSignupPgEmail);
        etName = (EditText) findViewById(R.id.etSignupPgName);
        etPassword = (EditText) findViewById(R.id.etSignupPgPassword);
        etPhone = (EditText) findViewById(R.id.etSignupPgPhoneNumber);
        bASLogin = (Button) findViewById(R.id.bSignupPgLogin);
        bASSignup = (Button) findViewById(R.id.bSignupPgSignup);
        etName.addTextChangedListener(this);
        etEmail.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);
        etPhone.addTextChangedListener(this);
        bASLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Activity_Login.class);
                startActivity(i);
                Activity_Signup.this.overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                finish();

            }
        });
        bASSignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(Activity_Signup.this);
                pd.setMessage("Loading, Please wait...");
                pd.setCancelable(false);
                pd.setIndeterminate(true);

                if (!TextUtils.isEmpty(etName.getText().toString())) {
                    if (!TextUtils.isEmpty(etEmail.getText().toString())) {
                        if (!TextUtils.isEmpty(etPhone.getText().toString())) {
                            if (!TextUtils.isEmpty(etPassword.getText().toString())) {
                                if (android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
                                    if (etPhone.getText().length() == 10) {

                                        final NetworkUtils mnetworkutils = new NetworkUtils(Activity_Signup.this);
                                        if (mnetworkutils.isnetconnected()) {
                                            RegisterUser userdetails = new RegisterUser();
                                            userdetails.setPhone(etPhone.getText().toString());
                                            userdetails.setPassword(etPassword.getText().toString());
                                            userdetails.setName(etName.getText().toString());
                                            userdetails.setEmail(etEmail.getText().toString());
                                            pd.show();
                                            mnetworkutils.getapi().register(userdetails, new Callback<Users>() {
                                                @Override
                                                public void success(Users user1, Response response) {
                                                    if (pd != null && pd.isShowing()) {
                                                        pd.dismiss();
                                                    }
                                                    if (user1.getMsg().equals("olduser")) {
                                                        Toast.makeText(Activity_Signup.this, "Your Number is already registered with us", Toast.LENGTH_SHORT).show();

                                                    } else if (user1.getMsg().equals("newuser")) {
                                                        Users user = new Users();
                                                        user.setName(user1.getName());
                                                        user.setPhone(user1.getPhone());
                                                        user.setEmail(user1.getEmail());
                                                        user.setApikey(user1.getApikey());
                                                        Db_User userDB = new Db_User();
                                                        userDB.deleteRecord();
                                                        userDB.AddToDB(user);
                                                        Log.i("Signup User", "UserAdded");
                                                        Utils utils = new Utils(Activity_Signup.this);
                                                        utils.Registered();
                                                        utils.setvalue("RegisteredPhone", user1.getPhone());
                                                        Activity_ViewPager.ViewPager.finish();
                                                        Intent f = new Intent(Activity_Signup.this.getBaseContext(), OTP_Check.class);
                                                        f.putExtra("NAME", etName.getText().toString());
                                                        if (!etEmail.getText().toString().isEmpty()) {
                                                            f.putExtra("EMAIL", etEmail.getText().toString());
                                                        }
                                                        f.putExtra("NUMBER", etPhone.getText().toString());
                                                        Activity_Signup.this.startService(f);

                                                        Intent i = new Intent(getApplicationContext(), Activity_Main.class);
                                                        startActivity(i);
                                                        Activity_Signup.this.overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                                                        finish();
                                                    }
                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    if (pd != null && pd.isShowing()) {
                                                        pd.dismiss();
                                                    }
                                                    Log.i("Retrofit Error",error.toString());
                                                    // Toast.makeText(Activity_Signup.this, "Error with registration please try again", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    } else {
                                        etPhone.setError("* Please enter a correct Phone Number");
                                    }
                                } else {
                                    etEmail.setError("* Please enter a correct EmailId");
                                }
                            } else {
                                etPassword.setError("* Password is required");
                            }
                        } else {
                            etPhone.setError("* Number is required");
                        }
                    } else {
                        etEmail.setError("* Email is required");
                    }
                } else {
                    etName.setError("* Name is required");
                }
            }
        });

    }

    public void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!etEmail.getText().toString().isEmpty()) etEmail.setError(null);
        if (!etName.getText().toString().isEmpty()) etName.setError(null);
        if (!etPhone.getText().toString().isEmpty()) etPhone.setError(null);
        if (!etPassword.getText().toString().isEmpty()) etPassword.setError(null);
    }
}
