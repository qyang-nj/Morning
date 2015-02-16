package me.roovent.morning;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Qing Yang on 2/14/15.
 *
 * Download images as soon as network is connected. Only when needed, it will be triggered.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkChangeReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "-- NetworkChangeReceiver triggered.");

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = false;
        boolean isWiFi = false;

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
             isConnected = activeNetwork.isConnectedOrConnecting();
             isWiFi = (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);
        }

        if (isConnected && isWiFi) {
            disable(context);
            new RingingImageProvider(context).init();
        }
    }

    public static void enable(Context context) {
        Log.v(TAG, "Enable NetworkChangeReceiver");
        ComponentName receiver = new ComponentName(context, NetworkChangeReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void disable(Context context) {
        Log.v(TAG, "Disable NetworkChangeReceiver");
        ComponentName receiver = new ComponentName(context, NetworkChangeReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
