package com.morning;

import java.text.DateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.morning.data.AlarmDbHandler;
import com.morning.data.AlarmEntity;

public class AlarmServiceHelper {

    public static void init(Context context) {
        ash = new AlarmServiceHelper(context);
    }

    public static AlarmServiceHelper getInstance() {
        if (ash == null) {
            throw new RuntimeException("AlarmServiceHelper has not been initialized.");
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

    private AlarmServiceHelper(Context context) {
        this.context = context;
        this.dbHandler = AlarmDbHandler.getInstance();
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
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

    private static AlarmServiceHelper ash;

    private Context context;
    private AlarmManager alarmManager;
    private AlarmDbHandler dbHandler;

    private long currentAlertTime = Long.MAX_VALUE;
    private AlarmEntity currentAlarm;
    private PendingIntent currentOperation;
}
