package com.morning;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.morning.model.Alarm;
import com.morning.model.AlarmDbHelper;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by qing on 1/26/15.
 */
public class AlarmService extends IntentService {
    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";

    private IntentFilter matcher;
    private AlarmDbHelper databaseHelper = null;

    public AlarmService() {
        super(AlarmService.class.getName());

        matcher = new IntentFilter();
        matcher.addAction(CREATE);
        matcher.addAction(CANCEL);
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

    public static void createAlarm(Context context, Alarm alarm) {
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

    public static void update(Context context, List<Alarm> alarms) {
        if (alarms.size() < 0) {
            return;
        }
        createAlarm(context, Alarm.findEarliestAlarm(alarms));
    }

    private AlarmDbHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, AlarmDbHelper.class);
        }
        return databaseHelper;
    }

    private void execute(String action, int alarmId) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Alarm alarm = getHelper().getAlarmDao().queryForId(alarmId);

        Intent i = new Intent(this, AlarmReciever.class);
        i.putExtra(Alarm.KEY_ALARM_ID, alarmId);

        /* Note: Using the same requestCode will make the new alarm override the old one,
        *  so that there is only one alarm scheduled. */
        PendingIntent pi = PendingIntent.getBroadcast(this, 0 /* requestCode */, i, PendingIntent.FLAG_UPDATE_CURRENT);
        if (CREATE.equals(action)) {
            long time = alarm.getNextTime();
            Log.i(getClass().getName(), "Alarm time: " + DateFormat.getDateTimeInstance().format(new Date(time)));

            if (android.os.Build.VERSION.SDK_INT < 19) {
                am.set(AlarmManager.RTC_WAKEUP, time, pi);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, time, pi);
            }
        } else if (CANCEL.equals(action)) {
            am.cancel(pi);
        }
    }
}
