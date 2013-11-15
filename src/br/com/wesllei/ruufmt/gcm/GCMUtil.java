package br.com.wesllei.ruufmt.gcm;

import java.io.IOException;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class GCMUtil {
	
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000; 
    public static final String PREFS_NAME = "settings";
    
    String SENDER_ID = "Your-Sender-ID";
    static final String TAG = "GCMUtil";
    
    private Context context;
    private GoogleCloudMessaging gcm;
	
	public GCMUtil(Context context) {
		super();
		this.context = context;
		this.gcm = gcm = GoogleCloudMessaging.getInstance(context);
	}
	public void setGCMIdInBackground() {
		new AsyncTask() {

			@Override
			protected String doInBackground(Object... params) {
				String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                String regid = gcm.register(SENDER_ID);
	                msg = "Device registered, registration ID=" + regid;

	                // You should send the registration ID to your server over HTTP,
	                // so it can use GCM/HTTP or CCS to send messages to your app.
	                // The request to your server should be authenticated if your app
	                // is using accounts.
	                sendRegistrationIdToBackend();

	                // For this demo: we don't need to send it because the device
	                // will send upstream messages to a server that echo back the
	                // message using the 'from' address in the message.

	                // Persist the regID - no need to register again.
	                storeRegistrationId(context, regid);
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
	            }
	            return msg;
			}

			private void storeRegistrationId(Context context, String regid) {
				SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
			    SharedPreferences.Editor editor = settings.edit();
			    editor.putString("GCMId", regid);
			    editor.commit();
			}

			private void sendRegistrationIdToBackend() {
				// TODO Auto-generated method stub
				
			}
			
		};
	}


	
}