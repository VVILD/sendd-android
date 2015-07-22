package co.sendd.GCMClasses;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import co.sendd.R;
import co.sendd.activity.Activity_Main;
import co.sendd.activity.Activity_Shipment_details;
import co.sendd.databases.Db_Notifications;

public class GcmIntentService extends IntentService {
    PowerManager.WakeLock screenOn;

    public GcmIntentService() {
        super("GcmIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        screenOn = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "example");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i("Notification added", "Notification added");
                if (extras.getString("tracking_no") != null) {
                    sendNotification(extras.getString("message"), extras.getString("tracking_no"), extras.getString("url"), extras.getString("title"));
                } else if (extras.getString("message") != null) {
                    co.sendd.gettersandsetters.Notification notif = new co.sendd.gettersandsetters.Notification();
                    notif.setMessage(extras.getString("message"));
                    notif.setTitle(extras.getString("title"));
                    Db_Notifications notif_db = new Db_Notifications();
                    notif_db.AddToDB(notif);
                    sendNotification(extras.getString("message"), "", "", extras.getString("title"));
                }
                screenOn.acquire();
                screenOn.release();
            }
        }
    }

    private void sendNotification(String msg, String Tracking, String image, String title) {
        Intent notificationIntent;
        if (Tracking.equals("")) {
            notificationIntent = new Intent(getApplication(), Activity_Main.class);
            notificationIntent.putExtra("coming_from_notification", true);
        } else {
            notificationIntent = new Intent(getApplication(), Activity_Shipment_details.class);
            notificationIntent.putExtra("tracking_no", Tracking);
            notificationIntent.putExtra("imageURI", image);
        }
        int icon = R.drawable.logo_blue_notif;
        int mNotificationId = 001;

        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getApplicationContext());
        Notification notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentIntent(resultPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo_blue_notif))
                .setContentText(msg).build();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationId, notification);
    }
}
