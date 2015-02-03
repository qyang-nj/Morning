package com.morning;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Qing Yang on 2/2/15.
 */
public class SettingsActivity extends AlarmAbstractActivity {

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            /* Load the preferences from an XML resource */
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, getString(R.string.action_settings));

        /* Display the fragment as the main content. */
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();
    }
}
