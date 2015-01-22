package com.morning;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.morning.data.AlarmDbHandler;
import com.morning.data.AlarmEntity;

import java.text.DateFormat;
import java.util.Date;

public class AlarmServiceHelper {

    private static AlarmServiceHelper ash;
    private Context context;
    private AlarmManager alarmManager;
    private AlarmDbHandler dbHandler;
    private long currentAlertTime = Long.MAX_VALUE;
    private AlarmEntity currentAlarm;
    private PendingIntent currentOperation;
    private AlarmServiceHelper(Context context) {
        this.context = context;
        this.dbHandler = AlarmDbHandler.getInstance(context);
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static AlarmServiceHelper getInstance(Context context) {
        if (ash == null) {
            ash = new AlarmServiceHelper(context);
        }
        return ash;
    }

    public void updateAlert() {
        AlarmEntity alarm = dbHandler.getEarliestAlarm();
        if (alarm == null) { /* No alarm needs to alert. */
            cancel();
            return;
        }

        if (currentAlarm == null) {
            cancel();
            set(alarm);
        } else if (!alarm.getId().equals(currentAlarm.getId())) {
            cancel();
            set(alarm);
        }
    }

    private void set(AlarmEntity alarm) {
        Intent intentAlarm = new Intent(context, AlarmReciever.class);
        intentAlarm.putExtra(Constants.INTEND_KEY_ALARM, alarm);

        long alertTime = alarm.getNextTime();
        PendingIntent operation = PendingIntent
                .getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, operation);

        currentAlertTime = alertTime;
        currentOperation = operation;
        currentAlarm = alarm;
        Log.i(Constants.TAG, "-- Alarm Set: " + DateFormat.getDateTimeInstance().format(new Date(alertTime)));
    }

    /**
     * Cancel current alarm;
     */
    private void cancel() {
        if (currentOperation == null) {
            return;
        }
        alarmManager.cancel(currentOperation);
        Log.i(Constants.TAG, "-- Alarm Canceled: "
                + DateFormat.getDateTimeInstance().format(new Date(currentAlertTime)));

        currentOperation = null;
        currentAlertTime = Long.MAX_VALUE;
        currentAlarm = null;
    }
}
