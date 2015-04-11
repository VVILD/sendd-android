package co.sendd.helper;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

import co.sendd.databases.Db_User;
import co.sendd.gettersandsetters.Otp;
import co.sendd.gettersandsetters.Users;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Kuku on 19/01/15.
 */
public class OTP_Check extends Service {
    private String Name, Phonenumber, Email;
    public Incomingsms mreceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Service Started", "True");
        if (intent != null && intent.getExtras() != null) {
            Name = intent.getStringExtra("NAME");
            Phonenumber = intent.getStringExtra("NUMBER");
            if (intent.getStringExtra("EMAIL") != null)
                Email = intent.getStringExtra("EMAIL");
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        mreceiver = new Incomingsms();

        registerReceiver(mreceiver, filter);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(1000 * 60 * 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    stopSelf();
                }
            }
        };
        timer.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mreceiver);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        Log.i("Service Destroyed", "True");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class Incomingsms extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Message Received", "True");

            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            SmsMessage shortMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);

            Log.d("SMSReceiver", "SMS message sender: " + shortMessage.getOriginatingAddress());
            Log.d("SMSReceiver", "SMS message text: " + shortMessage.getDisplayMessageBody());

            if (shortMessage.getOriginatingAddress().contains("iSENDD")) {
                final NetworkUtils mnetworkutils = new NetworkUtils(OTP_Check.this);
                if (mnetworkutils.isnetconnected()) {
                    Otp new_otp = new Otp();
                    Pattern pattern = Pattern.compile("\\d\\d\\d\\d");
                    Matcher matcher = pattern.matcher(shortMessage.getDisplayMessageBody());
                     while (matcher.find()) {
                        System.out.print("Start index: " + matcher.start());
                        System.out.print(" End index: " + matcher.end());
                        System.out.println(" Found: " + matcher.group());
                        try {
                            new_otp.setOtp1(String.valueOf(matcher.group()));
                         } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                    new_otp.setNumber(Phonenumber);
                    mnetworkutils.getapi().verify(Phonenumber, new_otp, new Callback<Users>() {
                        @Override
                        public void success(Users user, Response response) {

                            if (user.getApikey() != null && !user.getApikey().isEmpty()) {
                                Db_User add = new Db_User();
                                add.update(user);
                            }
                            stopSelf();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("the error from server", error.toString());
                            stopSelf();
                        }
                    });
                } else {
                    Log.i("Not connected network", "Not connected to network");
                 }
            }
        }
    }
}