package co.sendd.helper;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {
    private static final String TAG = "fetch-address-intent-service";
    protected ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";
        mReceiver = intent.getParcelableExtra("com.google.android.gms.location.sample.locationaddress.RECEIVER");
        if (mReceiver == null) {
            return;
        }

        Location location = intent.getParcelableExtra("com.google.android.gms.location.sample.locationaddress.LOCATION_DATA_EXTRA");
        if (location == null) {
            errorMessage = " Error Getting Location";
            Log.wtf(TAG, errorMessage);
            deliverResultToReceiver(1, errorMessage, "", null, null);
            return;
        }
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {

            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {

            errorMessage = "Service not available";
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = " Error Getting Location";

        }
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = " Noaddress Found";
            }
            deliverResultToReceiver(1, errorMessage, "", null, null);

        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();
            if (address.getMaxAddressLineIndex() >= 4) {
                for (int i = 1; i <= address.getMaxAddressLineIndex() - 2; i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
            } else {
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
            }
            if (address.getMaxAddressLineIndex() >= 1) {
                deliverResultToReceiver(0, TextUtils.join(System.getProperty("line.separator"), addressFragments), address.getAddressLine(address.getMaxAddressLineIndex() - 1), location.getLatitude(), location.getLongitude());
            } else {
                deliverResultToReceiver(0, TextUtils.join(System.getProperty("line.separator"), addressFragments), "", location.getLatitude(), location.getLongitude());

            }
        }
    }

    private void deliverResultToReceiver(int resultCode, String message, String pincode, Double Lat, Double Long) {
        Bundle bundle = new Bundle();
        if (message != null)
            bundle.putString("com.google.android.gms.location.sample.locationaddress.RESULT_DATA_KEY", message);
        if (pincode != null)
            bundle.putString("Pincode", pincode);
        if (Lat != null)
            bundle.putDouble("Lat", Lat);
        if (Long != null)
            bundle.putDouble("Long", Long);

        mReceiver.send(resultCode, bundle);
    }
}
