package co.sendd.helper;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class SaveImageService extends Service {

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.i("Save Image Service", "Started");
        try {
            Uri myIamge = Uri.parse(intent.getStringExtra("ImageUrl"));
            File imgFile = new File(myIamge.getPath());
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            File xxx = new File(myIamge.getPath());
            try {
                FileOutputStream fOut = new FileOutputStream(xxx);
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 10, fOut);
                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        stopSelf();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        Log.i("Save Image Service", "Destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
