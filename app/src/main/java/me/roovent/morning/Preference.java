package me.roovent.morning;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Qing Yang on 2/27/15.
 */
public class Preference {
    Context mContext;

    public Preference(Context context) {
        mContext = context;
    }

    /* Get snooze duration from shared preferences */
    public int getSnoozeDuration() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        int duration = 5;
        try {
            duration = Integer.parseInt(sharedPref.getString("pref_snooze_duration", "5"));
        } catch (NumberFormatException e) {
            Log.e(getClass().getName(), e.getMessage());
            duration = 5;
        }
        return duration;
    }
}
