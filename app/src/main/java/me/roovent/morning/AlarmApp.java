package me.roovent.morning;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by Qing Yang on 2/13/15.
 */
public class AlarmApp extends Application {
    private static final String TAG = AlarmApp.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "SDK Version: " + android.os.Build.VERSION.SDK_INT);

        SharedPreferences prefs = getSharedPreferences("pref_sys", MODE_PRIVATE);
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            long versionCode = prefs.getLong("lastRunVersionCode", 0);
            if (versionCode == 0) { /* First launch after install */
                Log.v(TAG, "-- First run after install.");
                new RingingImageProvider(this).init();
            }

            if (versionCode < pInfo.versionCode) { /* First run for this version */
                prefs.edit().putLong("lastRunVersionCode", pInfo.versionCode).apply();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error reading versionCode");
            e.printStackTrace();
        }
    }
}
