package co.sendd.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
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

import co.sendd.fragments.Fragment_Rate_Calculator;
import co.sendd.helper.Utils;
import co.sendd.navdrawer.NavDrawer_Adapter;
import co.sendd.R;
import co.sendd.fragments.Fragment_Customer_Support;
import co.sendd.fragments.Fragment_FAQ;
import co.sendd.fragments.Fragment_MyShipments;
import co.sendd.fragments.Fragment_Orders;
import co.sendd.fragments.Fragment_Profile;
import co.sendd.navdrawer.Drawer_Item;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class Activity_Main extends BaseActivity {

    private TextView toolbar_txtview;
    private CharSequence mTitle;
    private String[] reg_nav_drawer_items;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<Drawer_Item> navDrawerItems;
    public static ProgressDialog mProgress, mProgress2;
    private Boolean openpreviousBooking = false;
    private NavDrawer_Adapter adapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private Runnable mPendingRunnable;
    private android.os.Handler mHandler = new android.os.Handler(), handler = new android.os.Handler();
    public static boolean exit = false;
    private String tag;
    private ImageView AppLogo;
    public static TextView noShipmentTv;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI(findViewById(R.id.drawer_layout));
        Toolbar toolbar = (Toolbar) findViewById(R.id.Main_toolbar);
        setSupportActionBar(toolbar);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        noShipmentTv = (TextView) findViewById(R.id.noshiptv);
        openpreviousBooking = getIntent().getBooleanExtra("openpreviousBooking", false);
        toolbar_txtview = (TextView) findViewById(R.id.main_activity_toolbar_textview);
        toolbar_txtview.setMovementMethod(new ScrollingMovementMethod());

        AppLogo = (ImageView) findViewById(R.id.appLogo);
        mTitle = getTitle();
        reg_nav_drawer_items = getResources().getStringArray(R.array.nav_drawer_items);
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


        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        adapter = new NavDrawer_Adapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
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
        if (openpreviousBooking == false) {
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
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        openpreviousBooking = getIntent().getBooleanExtra("openpreviousBooking", false);

        Log.d("inside the ", "new intent method");
        if (openpreviousBooking) {
            toolbar_txtview.setText("Previous Shipments");

            Fragment_MyShipments history_fragment = new Fragment_MyShipments();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, history_fragment, "fragment_history").commit();
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
            if (position != 8) {
                mProgress.show();
                mProgress.setProgressDrawable(getResources().getDrawable(R.drawable.circular));
                mProgress.setContentView(R.layout.spinner_dialog);
            }
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(reg_nav_drawer_items[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

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
        navDrawerItems = new ArrayList<>();
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[0]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[1]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[2]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[3]));
        navDrawerItems.add(new Drawer_Item(reg_nav_drawer_items[4]));
        adapter = new NavDrawer_Adapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);
        Utils utils = new Utils(this);
        if (utils.getvalue("PickupAddress") == null) {
            toolbar_txtview.setText("Sendd");
        } else {
            toolbar_txtview.setText(utils.getvalue("PickupAddress"));
        }
    }

    //Recreate Menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        return super.onPrepareOptionsMenu(menu);
    }

    //display Fragment
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new Fragment_Profile();
                tag = "Fragment_Profile";
                break;
            case 1:
                fragment = new Fragment_Orders();
                Bundle address = new Bundle();
                fragment.setArguments(address);
                tag = "Fragment_Orders";
                break;

            case 2:
                fragment = new Fragment_MyShipments();
                tag = "fragment_history";
                break;
            case 4:
                fragment = new Fragment_FAQ();
                tag = "fragment_faq";
                break;
            case 5:
                fragment = new Fragment_Customer_Support();
                tag = "Fragment_Customer_Support";
                break;
            case 3:
                fragment = new Fragment_Rate_Calculator();
                tag = "Fragment_Rate_Calculator";
                break;

        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .addToBackStack("fragment_orders")
                    .replace(R.id.frame_container, fragment, tag).commit();

        } else {
            Log.e("MainActivity", "Error in creating fragment");
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
            ImageLoader.getInstance().destroy();
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

}