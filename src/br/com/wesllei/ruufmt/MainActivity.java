package br.com.wesllei.ruufmt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.analytics.tracking.android.EasyTracker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import br.com.wesllei.ruufmt.R;
import br.com.wesllei.ruufmt.gcm.GcmUtil;

public class MainActivity extends FragmentActivity {
	Context context;

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String PREFS_NAME = "settings";

	public Boolean isRendered = false;

	private String[] drawerListViewItems;
	private ListView drawerListView;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;

	private static ShareActionProvider mShareActionProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_main);

		if (checkPlayServices()) {
			processGDMId();
		}

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		drawerListViewItems = getResources().getStringArray(R.array.items);

		// get ListView defined in activity_main.xml
		drawerListView = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		drawerListView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_listview_item, drawerListViewItems));

		// Icon to option drawer
		drawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		drawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */
		);
		drawerLayout.setDrawerListener(drawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		CardapioFragment fragment = new CardapioFragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		// Create the adView.

		isRendered = true;
		setShareIntent();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		MenuItem item = menu.findItem(R.id.menu_item_share);
		mShareActionProvider = (ShareActionProvider) item.getActionProvider();
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = drawerLayout.isDrawerOpen(drawerListView);
		menu.findItem(R.id.action_update).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	public void setShareIntent() {
		int id = getResources().getIdentifier("content_frame", "id",
				context.getPackageName());
		View view = ((Activity) context).findViewById(id);
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states,
			// but all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		if (mShareActionProvider == null
				|| !(mExternalStorageAvailable && mExternalStorageWriteable)
				|| view == null || view.getWidth() <= 0
				|| view.getHeight() <= 0) {
			return;
		}
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File file = new File(path, "Cardapio.jpg");

		try {

			// Make sure the Pictures directory exists.
			path.mkdirs();

			OutputStream out = new FileOutputStream(file);

			Bitmap bmp = MainActivity.getBitmapFromView(view);

			bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.close();

			// Tell the media scanner about the new file so that it is
			// immediately available to the user.
			MediaScannerConnection.scanFile(context,
					new String[] { file.toString() }, null,
					new MediaScannerConnection.OnScanCompletedListener() {
						@Override
						public void onScanCompleted(String path, Uri uri) {
							Log.i("ExternalStorage", "Scanned " + path + ":");
							Log.i("ExternalStorage", "-> uri=" + uri);
						}
					});

			Intent sendIntent = new Intent(Intent.ACTION_SEND);
			Uri uri = Uri.parse(file.getAbsolutePath());
			sendIntent.putExtra(Intent.EXTRA_STREAM, uri);

			sendIntent.setType("image/*");

			mShareActionProvider.setShareIntent(sendIntent);
		} catch (IOException e) {
			// Unable to create file, likely because external storage is
			// not currently mounted.
			Log.w("ExternalStorage", "Error writing " + file, e);
		}

	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		this.isRendered = false;
		EasyTracker.getInstance().activityStop(this); // Add this method.
	}

	@Override
	public void onPause() {
		super.onPause();
		this.isRendered = false;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		isRendered = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void processGDMId() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String GCMId = settings.getString("GCMId", "");
		if (GCMId.isEmpty()) {
			GcmUtil gcm = new GcmUtil(context);
			gcm.registerInBackground();
		} else {

		}
	}

	// You need to do the Play Services APK check here too.
	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i("GCM", "This device is not supported.");
				// finish();
			}
			return false;
		}
		return true;
	}

	public static Bitmap getBitmapFromView(View view) {
		// Define a bitmap with the same size as the view
		Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),
				view.getHeight(), Bitmap.Config.ARGB_8888);
		// Bind a canvas to it
		Canvas canvas = new Canvas(returnedBitmap);
		// Get the view's background
		Drawable bgDrawable = view.getBackground();
		if (bgDrawable != null)
			// has background drawable, then draw it on the canvas
			bgDrawable.draw(canvas);
		else
			// does not have background drawable, then draw white background on
			// the canvas
			canvas.drawColor(Color.WHITE);
		// draw the view on the canvas
		view.draw(canvas);
		// return the bitmap
		return returnedBitmap;
	}
}
