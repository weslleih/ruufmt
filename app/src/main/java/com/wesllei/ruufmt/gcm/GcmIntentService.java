package com.wesllei.ruufmt.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.wesllei.ruufmt.MainActivity;
import com.wesllei.ruufmt.R;

import static com.google.android.gms.gcm.GoogleCloudMessaging.MESSAGE_TYPE_DELETED;
import static com.google.android.gms.gcm.GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE;
import static com.google.android.gms.gcm.GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR;
import static com.google.android.gms.gcm.GoogleCloudMessaging.getInstance;

/**
 * Created by wesllei on 27/03/15.
 */
public class GcmIntentService extends IntentService {

    private NotificationManager mNotificationManager;
    private int NOTIFICATION_ID = 1;
    private String TAG = "GCM: ";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("mealNotification", true)){
            return;
        }
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) { // has effect of unparcelling Bundle
            /*
            * Filter messages based on message type. Since it is likely that GCM will be
            * extended in the future with new message types, just ignore any message types you're
            * not interested in, or that you don't recognize.
            */
            if (MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                // sendNotification("Send error: " + extras.toString());
            } else if (MESSAGE_TYPE_DELETED.equals(messageType)) {
                // sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                // Post notification of received message.
                sendNotification(extras.getString("message"));
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setColor(getResources().getColor(R.color.primaryColor))
                        .setAutoCancel(true)
                        .setContentText(msg);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
