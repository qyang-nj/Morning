package com.morning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.morning.model.Alarm;
import com.morning.model.AlarmDbHelper;

import java.util.List;

/**
 * Created by Qing on 1/27/15.
 *
 * Set alarm after reboot.
 */
public class AlarmSetter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmDbHelper dbHelper = OpenHelperManager.getHelper(context, AlarmDbHelper.class);
        List<Alarm> alarms = dbHelper.getAlarmDao().queryForAll();
        OpenHelperManager.releaseHelper();

        AlarmService.update(context, alarms);
    }
}
