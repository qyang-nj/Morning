package me.roovent.morning;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.roovent.morning.model.Alarm;
import me.roovent.morning.model.AlarmDbHelper;

/**
 * Created by Qing Yang on 1/26/15.
 */
public class AlarmService extends IntentService {
    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";

    public static final String KEY_IS_SNOOZED = "isSnoozed";

    private IntentFilter matcher;
    private AlarmDbHelper databaseHelper = null;

    public static void createAlarm(Context context, Alarm alarm) {
        createAlarm(context, alarm, false);
    }

    public static void snoozeAlarm(Context context, Alarm alarm) {
        createAlarm(context, alarm, true);
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

    private static void createAlarm(Context context, Alarm alarm, boolean snoozed) {
        if (alarm == null) {
            return;
        }
        Intent i = new Intent(context, AlarmService.class);
        i.setAction(AlarmService.CREATE);
        i.putExtra(Alarm.KEY_ALARM_ID, alarm.id);
        i.putExtra(KEY_IS_SNOOZED, snoozed);
        context.startService(i);
    }

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

        if (alarmId < 0) { /* invalid alarm id */
            return;
        }

        if (!matcher.matchAction(action)) { /* invalid action */
            return;
        }

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Alarm alarm = getHelper().getAlarmDao().queryForId(alarmId);

        boolean isSnoozed = intent.getBooleanExtra(KEY_IS_SNOOZED, false);
        int requestCode = isSnoozed ? alarmId + 1 : 0;
        long time = isSnoozed ? getSnoozeTime() : alarm.getNextTime();

        Intent intentForRingAlarm = new Intent(this, AlarmReceiver.class);
        intentForRingAlarm.putExtra(Alarm.KEY_ALARM_ID, alarmId);
        PendingIntent piForRingAlarm = PendingIntent.getBroadcast(this, requestCode,
                intentForRingAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

        if (time == Long.MAX_VALUE || CANCEL.equals(action)) { /* Cancel alarm */
            am.cancel(piForRingAlarm);

            /* Cancel snoozed notification if there is */
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .cancel(SnoozedNotification.getNotificationId(alarmId));

            Log.i(getClass().getName(), time == Long.MAX_VALUE ?
                    "[ No scheduled alarm ]" : "[ Alarm canceled ] " + alarm.toString());
        } else if (CREATE.equals(action)) {
            setAlarm(am, piForRingAlarm, time);
            Log.i(getClass().getName(), String.format("[ Alarm scheduled ] [%s] [%s] [Snoozed: %s]",
                    alarm.toString(), SimpleDateFormat.getDateTimeInstance().format(new Date(time)), isSnoozed));
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

    /* Get snooze duration from shared preferences */
    private int getSnoozeDuration() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int duration = 5;
        try {
            duration = Integer.parseInt(sharedPref.getString("pref_snooze_duration", "5"));
        } catch (NumberFormatException e) {
            Log.e(getClass().getName(), e.getMessage());
            duration = 5;
        }
        return duration;
    }

    public long getSnoozeTime() {
        return (new Date().getTime()) / 1000 * 1000 + getSnoozeDuration() * 60 * 1000;
    }

    private void setAlarm(AlarmManager am, PendingIntent pi, long time) {
         /* If the stated trigger time is in the past, the alarm will be triggered immediately. */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            am.set(AlarmManager.RTC_WAKEUP, time, pi);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, time, pi);
        }
    }
}
