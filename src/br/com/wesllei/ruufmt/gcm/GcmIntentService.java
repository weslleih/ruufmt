package br.com.wesllei.ruufmt.gcm;


import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import br.com.wesllei.ruufmt.Cardapio;
import br.com.wesllei.ruufmt.MainActivity;
import br.com.wesllei.ruufmt.R;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class GcmIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;

	NotificationCompat.Builder builder;

	public static final String TAG = "GCMUtil";

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				// Post notification of received message.
				// MappingCardapio mCardapio =
				// MappingCardapio(extras.getString("cardapio"));
				// Cardapio cardapio = mCardapio.getCardapio();

				Gson gson = new Gson();
				Cardapio cardapio = gson.fromJson(extras.getString("cardapio"),
						Cardapio.class);

				if(saveCardapio(cardapio)){
					sendNotification(cardapio.getAlmoco().getPp());
				}

				Log.i(TAG, cardapio.getDate().toString());
				// Log.i(TAG, "Received: " + extras.getString("cardapio"));
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(String msg) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Cardápio novo").setContentText(msg);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, MainActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

	@SuppressWarnings("static-access")
	private boolean saveCardapio(Cardapio cardapio) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		boolean keep = true;
		try {
			fos = this.openFileOutput("cardapio", this.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(cardapio);
		} catch (Exception e) {
			keep = false;
			Log.i("File Out", e.getMessage());
		} finally {
			try {
				if (oos != null)
					oos.close();
				if (fos != null)
					fos.close();
			} catch (Exception e) { /* do nothing */
			}
		}
		return keep;
	}
}
