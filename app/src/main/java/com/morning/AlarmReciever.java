package com.morning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent in) {
        Log.d(Constants.TAG, "AlarmReciever.onReceive()");
        Intent intent = new Intent(context, AlertActivity.class);
        intent.putExtras(in);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
