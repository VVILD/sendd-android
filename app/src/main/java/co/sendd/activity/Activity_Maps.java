/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.sendd.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import co.sendd.helper.Utils;
import co.sendd.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location. To
 * track changes to the users location on the map, we request updates from the
 * {@link com.google.android.gms.location.FusedLocationProviderApi}.
 */
public class Activity_Maps extends BaseActivity
        implements TextWatcher,
         OnMapReadyCallback,GoogleMap.OnCameraChangeListener,LocationListener,GoogleMap.OnMyLocationButtonClickListener {
    LatLng ll;
    Marker marker;
    MarkerOptions Mo =new MarkerOptions();
    GoogleMap Map2;
    TextView address11;
    EditText Pincode,FlatNo;
    Handler handler;
    MapFragment mMapFragment;
    Button Save;
    ImageView ivlocate;
    Double Lat,Longi;
    String FormattedAddress;
    private static final String MAP_FRAGMENT_TAG = "map";
    Geocoder geocoder;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mapstoolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_icon_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     finish();
                                                     Activity_Maps.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
                                                 }
                                             }
        );
        geocoder = new Geocoder(this, Locale.getDefault());
        address11= (TextView)findViewById(R.id.tvAddress1);
        ivlocate =(ImageView)findViewById(R.id.ivlocate);
        ivlocate.setVisibility(View.INVISIBLE);
        Pincode =(EditText)findViewById(R.id.Pincode);
        Pincode.addTextChangedListener(this);
        FlatNo =(EditText)findViewById(R.id.Flatnobldno);
        FlatNo.addTextChangedListener(this);
        Save =(Button)findViewById(R.id.bSaveaddress);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!FlatNo.getText().toString().isEmpty()){
                    if(!Pincode.getText().toString().isEmpty()){
                        if(TextUtils.isDigitsOnly(Pincode.getText().toString())){
                            Utils utils =new Utils(Activity_Maps.this);
                            utils.setvalue("Lat",String.valueOf(Lat));
                            utils.setvalue("Longi",String.valueOf(Longi));
                            utils.setvalue("PickupAddress",FormattedAddress);
                            utils.setvalue("PickupFlatNo",FlatNo.getText().toString());
                            utils.setvalue("PickupPincode",Pincode.getText().toString());
                            Intent i = new Intent(Activity_Maps.this, Activity_Main.class);
                            startActivity(i);
                            finish();
                            Activity_Maps.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
                        }else {
                            Pincode.setError("Please enter a correct Pincode");
                        }
                    }else {
                        Pincode.setError("Please enter a Pincode");
                    }
                }else{
                    FlatNo.setError("Enter Flat/Building Number");
                }

            }
        });
    //        MapFragment mapFragment =
//                (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);
                }
            }
        };
        timer.start();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                mMapFragment = MapFragment.newInstance();
                FragmentTransaction fragmentTransaction =  getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.map, mMapFragment);
                fragmentTransaction.commit();
                mMapFragment.getMapAsync(Activity_Maps.this);


            }
        };
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Map2 =map;
        map.setMyLocationEnabled(true);
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Current Location"));
        map.setOnCameraChangeListener(this);
        ll =map.getCameraPosition().target;
        Mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.transparent_graphic));
        Mo.position(new LatLng(ll.latitude, ll.longitude)).title("ss");
        map.addMarker(Mo);
        Log.i("Latitude",String.valueOf(ll.latitude)+","+String.valueOf(ll.longitude));
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        ll =cameraPosition.target;
        Mo.position(new LatLng(ll.latitude, ll.longitude));
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    ll.latitude,
                    ll.longitude,
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
         } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {

        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            address11.setText(TextUtils.join(", ",
                    addressFragments));
            Pincode.setText(address11.getText().toString().substring(address11.getText().toString().lastIndexOf(" ")+1));

            Lat = ll.latitude;
            Longi =ll.longitude;
            FormattedAddress= TextUtils.join(System.getProperty("line.separator"),
                    addressFragments);

        }
        Map2.clear();
        Mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.transparent_graphic));
        Map2.addMarker(Mo);
        Log.i("Latitude", String.valueOf(ll.latitude) + "," + String.valueOf(ll.longitude));

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        Map2.animateCamera(cameraUpdate);
        ivlocate.setVisibility(View.VISIBLE);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }
    //On Back Button Pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Activity_Maps.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!FlatNo.getText().toString().isEmpty()) FlatNo.setError(null);
        if (!Pincode.getText().toString().isEmpty()) Pincode.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

        if (!FlatNo.getText().toString().isEmpty()) FlatNo.setError(null);
        if (!Pincode.getText().toString().isEmpty()) Pincode.setError(null);

    }

}

