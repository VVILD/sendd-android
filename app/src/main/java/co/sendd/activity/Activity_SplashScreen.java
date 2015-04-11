package co.sendd.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import co.sendd.helper.Utils;
import co.sendd.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public  class Activity_SplashScreen extends Activity {
    private Handler handler;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Utils utils = new Utils(Activity_SplashScreen.this);
                if (utils.isRegisterd()) {
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
            }
        };
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
    }

    protected void onPause() {
        super.onPause();
        finish();
    }
}