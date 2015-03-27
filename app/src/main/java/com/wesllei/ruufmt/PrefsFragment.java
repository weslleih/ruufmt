package com.wesllei.ruufmt;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by wesllei on 25/03/15.
 */
public class PrefsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
