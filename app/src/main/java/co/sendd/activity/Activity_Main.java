package co.sendd.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.wizrocket.android.sdk.WizRocketAPI;
import com.wizrocket.android.sdk.exceptions.WizRocketMetaDataNotFoundException;
import com.wizrocket.android.sdk.exceptions.WizRocketPermissionsNotSatisfied;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import co.sendd.R;
import co.sendd.connectiondetector.AlertDialogManager;
import co.sendd.connectiondetector.ConnectionDetector;
import co.sendd.databases.Db_Address_Receiver;
import co.sendd.databases.Db_CompleteOrder;
import co.sendd.databases.Db_Item_List;
import co.sendd.databases.Db_Notifications;
import co.sendd.databases.Db_User;
import co.sendd.fragments.Fragment_Customer_Support;
import co.sendd.fragments.Fragment_FAQ;
import co.sendd.fragments.Fragment_MyShipments;
import co.sendd.fragments.Fragment_Notification;
import co.sendd.fragments.Fragment_Orders;
import co.sendd.fragments.Fragment_Rate_Calculator;
import co.sendd.fragments.Fragment_Tutorial;
import co.sendd.gettersandsetters.RegisterUser;
import co.sendd.helper.NetworkUtils;
import co.sendd.helper.Utils;
import co.sendd.navdrawer.Drawer_Item;
import co.sendd.navdrawer.NavDrawer_Adapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class Activity_Main extends BaseActivity {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static ProgressDialog mProgress, mProgress2;
    public static boolean exit = false;
    public static Toolbar toolbar;
    private WizRocketAPI wr;
    private TextView toolbar_txtview;
    private CharSequence mTitle;
    private String[] reg_nav_drawer_items;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<Drawer_Item> navDrawerItems;
    private Boolean openpreviousBooking = false, openNotifiation = false;
    private NavDrawer_Adapter adapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private Runnable mPendingRunnable;
    private android.os.Handler mHandler = new android.os.Handler();
    private String tag;
    private GoogleCloudMessaging gcm;
    private ImageView AppLogo;
    private PackageInfo pInfo;
    private AlertDialogManager alert = new AlertDialogManager();
    private String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI(findViewById(R.id.drawer_layout));
        final Utils utils = new Utils(this);
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = utils.getvalue("regid");
            if (regid.isEmpty()) {
                new GCMRegistration(Activity_Main.this, "Registering").execute();
            }

        }
        TelephonyManager tm = (TelephonyManager) this.getSystemService(android.content.Context.TELEPHONY_SERVICE);
        String deviceId;
        if (tm.getDeviceId() != null) {
            deviceId = tm.getDeviceId();
        } else {
            deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        if (utils.isRegisterd()) {
            if (!utils.getvalue("Registered_on_server").equals("yes")) {
                try {

                    if (!regid.isEmpty()) {
                        final NetworkUtils mnetworkutils = new NetworkUtils(this);
                        RegisterUser ru = new RegisterUser();
                        ru.setDeviceid(deviceId);
                        ru.setGCMRegId(regid);
                        mnetworkutils.getapi().sendGCMID(utils.getvalue("RegisteredPhone"), ru, new Callback<RegisterUser>() {
                            @Override
                            public void success(RegisterUser promo, Response response) {
                                utils.setvalue("Registered_on_server", "yes");
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.i("Clicked", error.toString());
                            }
                        });
                    }
                } catch (NullPointerException ignored) {


                }
            }
        }


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        try {
            wr = WizRocketAPI.getInstance(getApplicationContext());

        } catch (WizRocketMetaDataNotFoundException | WizRocketPermissionsNotSatisfied e) {
            // handle appropriately
        }
        //CHECK FOR NEW AVAILABLE UPDATE AND WORKING INTERNET CONNECTION
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if (!cd.isConnectingToInternet()) {
            alert.showAlertDialog(Activity_Main.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
        } else {
            new CheckUpdate(Activity_Main.this, "Please Wait..").execute();
        }

        // INITIALIZE VIEWS AND OBJECTS
        toolbar = (Toolbar) findViewById(R.id.Main_toolbar);
        toolbar_txtview = (TextView) findViewById(R.id.main_activity_toolbar_textview);

        setSupportActionBar(toolbar);

        openpreviousBooking = getIntent().getBooleanExtra("openpreviousBooking", false);
        openNotifiation = getIntent().getBooleanExtra("coming_from_notification", false);
        toolbar_txtview.setMovementMethod(new ScrollingMovementMethod());
        AppLogo = (ImageView) findViewById(R.id.appLogo);
        mTitle = getTitle();
        if (utils.isRegisterd()) {
            reg_nav_drawer_items = getResources().getStringArray(R.array.nav_drawer_items_onRgistered);
        } else {
            reg_nav_drawer_items = getResources().getStringArray(R.array.nav_drawer_items);
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mProgress = new ProgressDialog(this);
        mProgress2 = new ProgressDialog(this);
        navDrawerItems = new ArrayList<>();
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[0]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[1]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[2]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[3]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[4]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[5]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[6]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[7]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[8]));

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        adapter = new NavDrawer_Adapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                try {
                    getSupportActionBar().setTitle(mTitle);
                } catch (NullPointerException ignored) {

                }
                invalidateOptionsMenu();

                if (mPendingRunnable != null) {
                    mHandler.post(mPendingRunnable);
                    mPendingRunnable = null;
                }
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
        if (!openpreviousBooking && !openNotifiation) {
            if (savedInstanceState != null) {
                Fragment_Orders orders_fragment = (Fragment_Orders) getSupportFragmentManager().getFragment(savedInstanceState, "Fragment_Orders");
                if (orders_fragment != null)
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, orders_fragment, "Fragment_Orders").commit();
            } else {
                displayView(1);
            }
        } else if (openpreviousBooking) {

            Fragment_MyShipments history_fragment = new Fragment_MyShipments();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, history_fragment, "fragment_history").commit();
            openpreviousBooking = false;
        } else if (openNotifiation) {
            Fragment_Notification notification_fragment = new Fragment_Notification();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, notification_fragment, "notification_fragment").commit();
            openNotifiation = false;
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        openpreviousBooking = getIntent().getBooleanExtra("openpreviousBooking", false);
        openNotifiation = getIntent().getBooleanExtra("coming_from_notification", false);

        if (openpreviousBooking) {
            toolbar_txtview.setText("Previous Shipments");
            Fragment_MyShipments history_fragment = new Fragment_MyShipments();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, history_fragment, "fragment_history").commit();
            openpreviousBooking = false;
        } else if (openNotifiation) {
            toolbar_txtview.setText("Notifications");
            Fragment_Notification notification_fragment = new Fragment_Notification();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, notification_fragment, "notification_fragment").commit();
            openpreviousBooking = false;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (mTitle != null) {
            getSupportActionBar().setTitle(mTitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setActionBarTitle(String title) {
        toolbar_txtview.setText(title);
    }

    public void hideActionBar() {
        toolbar_txtview.setText("");
        AppLogo.setVisibility(View.GONE);
    }

    public void showActionBar() {
        AppLogo.setVisibility(View.VISIBLE);
    }

    //On Activity Resume
    @Override
    protected void onResume() {
        super.onResume();
        //checkPlayServices();
        wr.activityResumed(this);
        navDrawerItems = new ArrayList<>();
        Utils utils = new Utils(Activity_Main.this);
        if (utils.isRegisterd()) {
            reg_nav_drawer_items = getResources().getStringArray(R.array.nav_drawer_items_onRgistered);
        } else {
            reg_nav_drawer_items = getResources().getStringArray(R.array.nav_drawer_items);
        }
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[0]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[1]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[2]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[3]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[4]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[5]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[6]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[7]));
        adapter = new NavDrawer_Adapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);
        if (utils.getvalue("PickupAddress").equals("")) {
            toolbar_txtview.setText("Sendd");
        } else {
            toolbar_txtview.setText(utils.getvalue("PickupAddress"));
        }
        adapter.notifyDataSetChanged();

    }

    //Recreate Menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wr.activityPaused(this);
    }

    //display Fragment
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        Utils utils = new Utils(Activity_Main.this);
        switch (position) {
            case 0:

                break;
            case 1:
                fragment = new Fragment_Orders();
                Bundle address = new Bundle();
                fragment.setArguments(address);
                tag = "Fragment_Orders";
                break;

            case 2:

                if (!utils.isRegisterd()) {
                    Intent i = new Intent(getApplicationContext(), Activity_Reigister_Phone.class);
                    i.putExtra("PreviousShipments", true);
                    startActivity(i);
                    overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

                } else {

                    fragment = new Fragment_MyShipments();
                    tag = "fragment_history";
                }
                break;
            case 3:
                fragment = new Fragment_Rate_Calculator();
                tag = "Fragment_Rate_Calculator";
                wr.event.push("Rate Calculator accessed");

                break;
            case 4:
                fragment = new Fragment_Notification();
                tag = "Fragment_Notification";
                break;
            case 5:
                fragment = new Fragment_FAQ();
                tag = "fragment_faq";
                break;
            case 6:
                fragment = new Fragment_Tutorial();
                tag = "Fragment_Tutorial";
                break;
            case 7:
                fragment = new Fragment_Customer_Support();
                tag = "Fragment_Customer_Support";
                break;
            case 8:

                if (!utils.isRegisterd()) {
                    Intent i = new Intent(getApplicationContext(), Activity_Reigister_Phone.class);
                    i.putExtra("Login", true);
                    startActivity(i);
                    overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

                } else {
                    Db_User userDB = new Db_User();
                    Db_CompleteOrder co = new Db_CompleteOrder();
                    Db_Item_List il = new Db_Item_List();
                    Db_Address_Receiver ar = new Db_Address_Receiver();
                    Db_Notifications no = new Db_Notifications();

                    userDB.deleteRecord();
                    co.deleteAllItems();
                    il.deleteAllItems();
                    ar.deleteAllItems();
                    no.deleteAllItems();
                    utils.clear();
                    utils.UnSync();

                    adapter.notifyDataSetChanged();
                    navDrawerItems = new ArrayList<>();
                    if (utils.isRegisterd()) {
                        reg_nav_drawer_items = getResources().getStringArray(R.array.nav_drawer_items_onRgistered);
                    } else {
                        reg_nav_drawer_items = getResources().getStringArray(R.array.nav_drawer_items);
                    }
                    navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[0]));
                    navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[1]));
                    navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[2]));
                    navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[3]));
                    navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[4]));
                    navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[5]));
                    navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[6]));
                    navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[7]));
                    adapter = new NavDrawer_Adapter(getApplicationContext(), navDrawerItems);
                    mDrawerList.setAdapter(adapter);
                    if (utils.getvalue("PickupAddress") == "") {
                        toolbar_txtview.setText("Sendd");
                    } else {
                        toolbar_txtview.setText(utils.getvalue("PickupAddress"));
                    }
                    adapter.notifyDataSetChanged();
                    Activity_Main.mProgress.dismiss();
                    Toast.makeText(Activity_Main.this, "Successfully logged out",
                            Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), Activity_ViewPager.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                    break;

                }


        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .addToBackStack("fragment_orders")
                    .replace(R.id.frame_container, fragment, tag).commit();
        }

    }

    //On Conf changed
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    //On Back Button Pressed
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
            Activity_Main.this.overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
        } else {
            Fragment_Orders orders_fragment = new Fragment_Orders();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .addToBackStack("fragment_orders")
                    .replace(R.id.frame_container, orders_fragment, "fragment_orders").commit();
            exit = true;

        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position,
                                long id) {

            mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    displayView(position);
                }
            };
            if (position != 10 && position != 0) {
                mProgress.show();
                mProgress.setProgressDrawable(getResources().getDrawable(R.drawable.circular));
                mProgress.setContentView(R.layout.spinner_dialog);
                mDrawerList.setItemChecked(position, true);
                mDrawerList.setSelection(position);
                setTitle(reg_nav_drawer_items[position]);
                mDrawerLayout.closeDrawer(mDrawerList);
            }

        }
    }

    class CheckUpdate extends AsyncTask<String, String, String> {

        Context context;
        String str_mesg;
        String Version;
        private String checkUpdate = "http://sendd.co/app/api/v1/version/";

        public CheckUpdate(Context context, String str_mesg) {
            this.context = context;
            this.str_mesg = str_mesg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(checkUpdate);

            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream is = httpEntity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                is.close();

                JSONArray arr;
                try {
                    Log.i("sb.toString()==", sb.toString());
                    arr = new JSONArray(sb.toString());
                    JSONObject c = (JSONObject) arr.get(0);
                    Version = c.getString("version");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {

            try {
                String str1 = Version;
                String str2 = pInfo.versionName;
                String[] WebVersion, AppVersion;
                if (str2 != null && str1 != null) {
                    if (str1.contains(".") && str2.contains(".")) {
                        WebVersion = str1.split("\\.");
                        AppVersion = str2.split("\\.");

                        if (Integer.parseInt(WebVersion[0]) >= Integer.parseInt(AppVersion[0])) {
                            if (Integer.parseInt(WebVersion[0]) == Integer.parseInt(AppVersion[0])) {
                                if (Integer.parseInt(WebVersion[1]) <= Integer.parseInt(AppVersion[1])) {
                                    if (Integer.parseInt(WebVersion[2]) > Integer.parseInt(AppVersion[2])) {
                                        new AlertDialog.Builder(Activity_Main.this)
                                                .setTitle("New Update Available")
                                                .setCancelable(false)
                                                .setMessage("A new update for this Application is available. Click update to download the latest version.")
                                                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // continue with delete
                                                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                        try {
                                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                        } catch (android.content.ActivityNotFoundException notf) {
                                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                        }
                                                    }
                                                })
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                })
                                                .show();
                                    }
                                } else {
                                    new AlertDialog.Builder(Activity_Main.this)
                                            .setTitle("New Update Available")
                                            .setMessage("A new update for this Application is available. Click update to download the latest version.")
                                            .setCancelable(false)
                                            .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // continue with delete
                                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                    try {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                    } catch (android.content.ActivityNotFoundException notf) {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                    }
                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();
                                }
                            } else {
                                new AlertDialog.Builder(Activity_Main.this)
                                        .setTitle("New Update Available")
                                        .setMessage("This version of app is no longer supported. Please upgrade to continue using our app.")
                                        .setCancelable(false)
                                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // continue with delete
                                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                } catch (android.content.ActivityNotFoundException notf) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                }
                                            }
                                        })
                                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(Activity_Main.this, "Please update the application to continue using our services.", Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        })
                                        .show();
                            }
                        }
                    }
                }
            } catch (Exception ignored) {

            }
        }
    }

    class GCMRegistration extends AsyncTask<String, String, String> {
        Utils utils;
        Context context;
        String str_mesg;

        public GCMRegistration(Context context, String str_mesg) {
            this.context = context;
            this.str_mesg = str_mesg;
            utils = new Utils(context);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... args) {

            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(Activity_Main.this);
                }
                String SENDER_ID = "116763685486";
                regid = gcm.register(SENDER_ID);
            } catch (IOException ignored) {

            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            try {
                utils.setvalue("regid", regid);
            } catch (Exception ignored) {

            }
        }
    }
}