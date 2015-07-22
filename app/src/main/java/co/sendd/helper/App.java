package co.sendd.helper;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.crashlytics.android.Crashlytics;

import java.io.File;

import co.sendd.databases.Db_Address_Receiver;
import co.sendd.databases.Db_CompleteOrder;
import co.sendd.databases.Db_Item_List;
import co.sendd.databases.Db_Notifications;
import co.sendd.databases.Db_User;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Kuku on 26/12/14.
 */
public class App extends Application {

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            int i = 0;
            while (i < children.length) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
                i++;
            }
        }
        return dir.delete();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());
        initializeDB();
        //        CalligraphyConfig.initDefault("fonts/Robotsdo-Light.ttf", R.attr.fontPath);
        instance = this;
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

    protected void initializeDB() {
        Configuration.Builder configurationBuilder = new Configuration.Builder(this);
        configurationBuilder.addModelClasses(Db_Address_Receiver.class);
        configurationBuilder.addModelClasses(Db_CompleteOrder.class);
        configurationBuilder.addModelClasses(Db_Item_List.class);
        configurationBuilder.addModelClasses(Db_User.class);
        configurationBuilder.addModelClasses(Db_Notifications.class);

        ActiveAndroid.initialize(configurationBuilder.create());
    }
}