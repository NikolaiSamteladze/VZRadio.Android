package com.samteladze.vzradio.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.samteladze.vzradio.android.R;
import com.samteladze.vzradio.android.common.EnvironmentHelper;

/**
 * Created by nsamteladze on 2/18/14.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY_DEBUG_MODE = "debug_mode";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences_screen);
    }

    @Override
    public void onResume() {
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_DEBUG_MODE)) {
            EnvironmentHelper.sDebugMode = sharedPreferences.getBoolean(KEY_DEBUG_MODE, false);
        }
    }
}