package com.wesllei.ruufmt;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * Created by wesllei on 16/05/15.
 */
public class Analytics extends Application {
    public enum TrackerName {
        APP_TRACKER,
        GLOBAL_TRACKER
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public Analytics() {
        super();
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {

        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

            if (trackerId == TrackerName.GLOBAL_TRACKER) {
                mTrackers.put(trackerId, analytics.newTracker(R.xml.global_tracker));
            }else{
                mTrackers.put(trackerId, analytics.newTracker(R.xml.app_tracker));
            }

        }

        return mTrackers.get(trackerId);
    }
}
