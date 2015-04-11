package co.sendd.helper;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.crashlytics.android.Crashlytics;
import co.sendd.R;

import io.fabric.sdk.android.Fabric;
import java.io.File;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


/**
 * Created by Kuku on 26/12/14.
 */
public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        ActiveAndroid.initialize(this);
        CalligraphyConfig.initDefault("fonts/OpenSans_Regular.ttf", R.attr.fontPath);
        instance = this;
    }
    public static App getInstance() {
        return instance;
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }


}
