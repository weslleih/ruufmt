package com.wesllei.ruufmt.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.wesllei.ruufmt.about.AboutActivity;
import com.wesllei.ruufmt.chat.ChatFragment;
import com.wesllei.ruufmt.interfaces.OnAsyncTaskEndJson;
import com.wesllei.ruufmt.navigation.NavigationFragment;
import com.wesllei.ruufmt.navigation.NavigationItem;
import com.wesllei.ruufmt.util.Communicator;
import com.wesllei.ruufmt.event.EventFragment;
import com.wesllei.ruufmt.interfaces.OnNavigationClick;
import com.wesllei.ruufmt.R;
import com.wesllei.ruufmt.settings.SettingsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.google.android.gms.analytics.GoogleAnalytics;

public class MainActivity extends AppCompatActivity implements OnNavigationClick {
    public final static String EVENT_ID = "com.wesllei.ruufmt.MESSAGE";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private GoogleCloudMessaging gcm;
    private String regid;


    //Google Play
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static String SENDER_ID = "563670348727";

    //Fragment
    FragmentManager manager;

    private static GoogleAnalytics tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = getSupportFragmentManager();

        //Configure toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Configure navigation
        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        NavigationFragment navigationFragment = new NavigationFragment();
        navigationFragment.setItems(getNavigationList());
        navigationFragment.setNavigationClikCallBack(this);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.left_drawer, navigationFragment);
        transaction.commit();


        //Configure navigation and toolbar arrow sync
        actionBarDrawerToggle.syncState();

        if (checkPlayServices()) {
            regid = getRegistrationId(this);
            Log.d("RegId", regid);
            if (regid.isEmpty()) {
                registerInBackground();
            }
            viewHome();
        }

    }

    public void viewHome() {
        getSupportActionBar().setTitle(R.string.home);
        Fragment fragment = new MainFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

    public void viewEvent() {
        getSupportActionBar().setTitle(R.string.event);
        Fragment fragment = new EventFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

    public void viewChat() {
        getSupportActionBar().setTitle(R.string.chat);
        Fragment fragment = new ChatFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check device for Play Services APK.
        checkPlayServices();
        // Remove image generate from share
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "ruufmt_temp.jpg");
        f.delete();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("GPS: ", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("GCM: ", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("GCM", "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i("GCM: ", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        final Context context = this;
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                String msg = "";
                try {
                    gcm = GoogleCloudMessaging.getInstance(context);
                    regid = gcm.register(SENDER_ID);
                    Log.d("GCM RegId", regid);
                    msg = "Device registered, registration ID=" + regid;
                    Communicator communicator = new Communicator(context, "/user/add/".concat(regid), new OnAsyncTaskEndJson() {
                        @Override
                        public void onAsyncTaskEndJson(JSONObject jsonObject) {
                            String message = new String();
                            try {
                                message = jsonObject.getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (message.equals("sucess")) {
                                storeRegistrationId(context, regid);
                            }
                        }
                    });
                    communicator.getJson();
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }
        }.execute(null, null, null);
    }

    @Override
    public void OnNavigationClick(NavigationItem item) {
        if (item.getType() == NavigationItem.TYPE_MAIN) {
            findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(item.getColor()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(item.getColorDark()));
            }
        }
        switch (item.geTitle()) {
            case R.string.home:
                viewHome();
                break;
            case R.string.event:
                viewEvent();
                break;
            case R.string.chat:
                viewChat();
                break;
            case R.string.vnsmes:
                viewHome();
                break;
            case R.string.settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;

            case R.string.about:
                Intent Aboutintent = new Intent(this, AboutActivity.class);
                startActivity(Aboutintent);
                break;
            default:
                viewHome();
        }

        drawerLayout.closeDrawers();
    }

    public ArrayList<NavigationItem> getNavigationList() {
        ArrayList navigationList = new ArrayList();
        navigationList.add(new NavigationItem(R.string.home, R.color.primaryColor, R.color.primaryDarkColor, R.drawable.ic_home, NavigationItem.TYPE_MAIN));
        navigationList.add(new NavigationItem(R.string.event, R.color.event, R.color.eventDark, R.drawable.ic_event, NavigationItem.TYPE_MAIN));
        navigationList.add(new NavigationItem(R.string.chat, R.color.chat, R.color.chatDark, R.drawable.ic_action_communication_chat, NavigationItem.TYPE_MAIN));
        //navigationList.add(new NavigationItem(R.string.chat, R.color.vnsmes, R.color.vnsmesDark, R.drawable.ic_vnsmes, NavigationItem.TYPE_MAIN));
        navigationList.add(new NavigationItem(R.string.settings, R.color.primaryColor, R.color.primaryDarkColor, R.drawable.ic_settings, NavigationItem.TYPE_FOOTER));
        navigationList.add(new NavigationItem(R.string.about, R.color.primaryColor, R.color.primaryDarkColor, R.drawable.ic_about, NavigationItem.TYPE_FOOTER));
        return navigationList;
    }
}