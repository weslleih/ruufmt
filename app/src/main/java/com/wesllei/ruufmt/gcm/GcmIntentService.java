package com.wesllei.ruufmt.gcm;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.wesllei.ruufmt.data.Event;
import com.wesllei.ruufmt.data.EventList;
import com.wesllei.ruufmt.data.Meal;
import com.wesllei.ruufmt.data.MealList;
import com.wesllei.ruufmt.event.EventActivity;
import com.wesllei.ruufmt.main.MainActivity;
import com.wesllei.ruufmt.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

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
    private PendingIntent contentIntent;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        Log.i(TAG, "Received: " + extras.toString());
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
                //sendNotification(extras.getString("message"));
                Log.i(TAG, "Received: " + extras.toString());
                String type = extras.getString("collapse_key");

                switch (type) {
                    case "mealUpdate":
                        processMeal(extras.getString("message"), MainActivity.class);
                        break;
                    case "newEvent":
                        processEvent(extras.getString("message"), EventActivity.class);
                        break;
                }
                //if()
            }
        } else {
            Log.d(TAG, "Message is empty");
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg, String title, Class activity) {
        if (msg == null || msg == "") {
            return;
        }
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setColor(this.getResources().getColor(R.color.primaryColor))
                        .setAutoCancel(true)
                        .setContentText(msg);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void processMeal(String message, Class activity) {
        JSONObject jsonObject;
        MealList mealList = new MealList(this);
        Calendar lunchLimit = Calendar.getInstance();
        lunchLimit.set(Calendar.HOUR_OF_DAY, 13);
        lunchLimit.set(Calendar.MINUTE, 30);
        Date now = new Date();
        Meal meal;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String msg, title;
        try {
            jsonObject = new JSONObject(message);
            mealList.processJson(jsonObject);
            mealList.save();
            if (preferences.getBoolean("notify_menu", true)) {
                if (now.before(lunchLimit.getTime()) && preferences.getInt("notify_meal  ", 0) == 0) {
                    meal = mealList.getMealByType(0);
                    title = "Almoço";
                } else {
                    meal = mealList.getMealByType(1);
                    title = "Jantar";
                }
                switch (preferences.getString("notify_dish", "pp")) {
                    case "pp":
                        msg = meal.getPp();
                        break;
                    case "ov":
                        msg = meal.getOv();
                        break;
                    case "sa":
                        msg = meal.getSa();
                        break;
                    case "gu":
                        msg = meal.getGu();
                        break;
                    case "ac":
                        msg = meal.getAc();
                        break;
                    case "so":
                        msg = meal.getSo();
                        break;
                    case "su":
                        msg = meal.getSu();
                        break;
                    default:
                        msg = "Novo cardápio";
                }
                contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, activity).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP), Intent.FILL_IN_ACTION);
                sendNotification(msg, title, activity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processEvent(String message, Class activity) {
        if (message == null || message == "") {
            return;
        }
        JSONObject jsonObject;
        EventList eventList = new EventList(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            jsonObject = new JSONObject(message);
            Event event = new Event(jsonObject.getJSONObject("event"));
            eventList.addIfNoExist(event);
            eventList.save();
            if (preferences.getBoolean("notify_event", true)) {
                Intent intent = new Intent(this, activity);
                intent.putExtra("Event", event);
                contentIntent = PendingIntent.getActivity(this.getApplicationContext(), (int)(Math.random() * 100), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                sendNotification(event.getName(), "Evento", activity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
