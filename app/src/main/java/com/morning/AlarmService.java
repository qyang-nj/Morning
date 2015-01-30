package com.morning;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.morning.model.Alarm;
import com.morning.model.AlarmDbHelper;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Qing Yang on 1/26/15.
 */
public class AlarmService extends IntentService {
    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";
    public static final String SNOOZE = "SNOOZE";

    private IntentFilter matcher;
    private AlarmDbHelper databaseHelper = null;

    public static void createAlarm(Context context, Alarm alarm) {
        if (alarm == null) {
            return;
        }
        Intent i = new Intent(context, AlarmService.class);
        i.setAction(AlarmService.CREATE);
        i.putExtra(Alarm.KEY_ALARM_ID, alarm.id);
        context.startService(i);
    }

    public static void cancelAlarm(Context context, Alarm alarm) {
        Intent i = new Intent(context, AlarmService.class);
        i.setAction(AlarmService.CANCEL);
        i.putExtra(Alarm.KEY_ALARM_ID, alarm.id);
        context.startService(i);
    }

    public static void snoozeAlarm(Context context, Alarm alarm) {
        Intent i = new Intent(context, AlarmService.class);
        i.setAction(AlarmService.SNOOZE);
        i.putExtra(Alarm.KEY_ALARM_ID, alarm.id);
        context.startService(i);
    }

    public static void update(Context context, List<Alarm> alarms) {
        if (alarms.size() < 0) {
            return;
        }
        createAlarm(context, Alarm.findEarliestAlarm(alarms));
    }

    public static void update(Context context) {
        OrmLiteBaseActivity<AlarmDbHelper> activity = (OrmLiteBaseActivity<AlarmDbHelper>) context;
        List<Alarm> alarms = activity.getHelper().getAlarmDao().queryForAll();
        update(context, alarms);
    }

    public AlarmService() {
        super(AlarmService.class.getName());

        matcher = new IntentFilter();
        matcher.addAction(CREATE);
        matcher.addAction(CANCEL);
        matcher.addAction(SNOOZE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        int alarmId = intent.getIntExtra(Alarm.KEY_ALARM_ID, -1);

        if (alarmId < 0) {
            return;
        }

        if (matcher.matchAction(action)) {
            execute(action, alarmId);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    private AlarmDbHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, AlarmDbHelper.class);
        }
        return databaseHelper;
    }

    private void execute(String action, int alarmId) {

        Alarm alarm = getHelper().getAlarmDao().queryForId(alarmId);

        Intent i = new Intent(this, AlarmReceiver.class);
        i.putExtra(Alarm.KEY_ALARM_ID, alarmId);

        if (CREATE.equals(action)) {
            PendingIntent pi = PendingIntent.getBroadcast(this, 0 /* requestCode */, i, PendingIntent.FLAG_UPDATE_CURRENT);
            long time  = alarm.getNextTime();
            setAlarm(pi, time);
            Log.i(getClass().getName(), alarm.toString());
        } else if (SNOOZE.equals(action)) {
            PendingIntent pi = PendingIntent.getBroadcast(this, alarmId + 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
            long time = new Date().getTime() + Constants.DEFAULT_SNOOZE_TIME * 60 * 1000;
            setAlarm(pi, time);
            Log.i(getClass().getName(), alarm.toString());
        } else if (CANCEL.equals(action)) {
            PendingIntent pi = PendingIntent.getBroadcast(this, 0 /* requestCode */, i, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am.cancel(pi);
        }
    }

    private void setAlarm(PendingIntent pi, long time) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (time == Long.MAX_VALUE) {
            am.cancel(pi);
        } else {
            if (android.os.Build.VERSION.SDK_INT < 19) {
                am.set(AlarmManager.RTC_WAKEUP, time, pi);
            } else {
                /* If the stated trigger time is in the past, the alarm will be triggered immediately. */
                am.setExact(AlarmManager.RTC_WAKEUP, time, pi);
            }
            Log.i(getClass().getName(), "--- Set alarm: " + DateFormat.getDateTimeInstance().format(new Date(time)));
        }
    }
}
