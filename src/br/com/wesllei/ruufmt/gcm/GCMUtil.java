package br.com.wesllei.ruufmt.gcm;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.wesllei.ruufmt.util.Json;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class GcmUtil {

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	public static final String PREFS_NAME = "settings";

	String SENDER_ID = "563670348727";
	static final String TAG = "GCMUtil";

	private Context context;
	private GoogleCloudMessaging gcm;

	String regid;

	public GcmUtil(Context context) {
		super();
		this.context = context;
		this.gcm = GoogleCloudMessaging.getInstance(context);
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	public void registerInBackground() {
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected String doInBackground(Object... params) {
				String msg = "";

				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					Log.i(TAG, SENDER_ID);
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;
					sendRegistrationIdToServer(context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				Log.i(TAG, msg);
				return msg;
			}
		}.execute();
		;
	}

	private void sendRegistrationIdToServer(Context context, String regid) {
		JSONObject request;
		try {
			request = Json.getJson("http://wesllei.com.br/ruufmt/user/add/"
					.concat(regid));
			if (request != null && (Boolean) request.get("status")) {
				storeRegistrationId(context, regid);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.i("Server Registration", e.getMessage());
		}
	}

	private void storeRegistrationId(Context context, String regid) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("GCMId", regid);
		editor.commit();
	}
}
