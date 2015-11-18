package co.sendd.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.appsflyer.AppsFlyerLib;

import co.sendd.R;
import co.sendd.helper.Utils;



public  class Activity_SplashScreen extends Activity {
    private Handler handler;
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        AppsFlyerLib.setAppsFlyerKey("vtZdaj5bzy69VaefYrkeC9");
        AppsFlyerLib.sendTracking(getApplicationContext());
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Utils utils = new Utils(Activity_SplashScreen.this);
                if (utils.getvalue("Tutorial_Displayed").equals("true")) {
                    Intent MainActivity = new Intent(Activity_SplashScreen.this, Activity_Main.class);
                    startActivity(MainActivity);
                    Activity_SplashScreen.this.overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                    finish();
                } else {
                    Intent ViewPagerActivity = new Intent(Activity_SplashScreen.this, Activity_ViewPager.class);
                    startActivity(ViewPagerActivity);
                    Activity_SplashScreen.this.overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
                    finish();
                }
                return true;
            }
        });
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);
                }
            }
        };
        timer.start();
    }
    protected void onPause() {
        super.onPause();
        finish();
    }
}