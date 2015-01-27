package com.morning;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.morning.model.Alarm;
import com.morning.model.AlarmDbHelper;

import java.util.Calendar;


public class AlarmRingingActivity extends OrmLiteBaseActivity<AlarmDbHelper> {
    private static final int WAKELOCK_TIMEOUT = 60 * 1000;

    private PowerManager.WakeLock mWakeLock;
    private Ringtone mRingtone;
    private Alarm mAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        /* Fetch alarm */
        int alarmId = getIntent().getIntExtra(Alarm.KEY_ALARM_ID, -1);
        mAlarm = getHelper().getAlarmDao().queryForId(alarmId);
        if (mAlarm == null) {
            Log.e(getClass().getName(), "Alarm wasn't found in database. Check why.");
            mAlarm = new Alarm(); /* Just for safety. */
        }

        TextView txtTime = (TextView) findViewById(R.id.txt_time);
        Calendar cal = Calendar.getInstance();
        txtTime.setText(DateFormat.format("hh:mm a", cal.getTime()));

        findViewById(R.id.btn_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btn_snooze).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /* Ensure wakelock release */
        Runnable releaseWakelock = new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

                if (mWakeLock != null && mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
            }
        };
        new Handler().postDelayed(releaseWakelock, WAKELOCK_TIMEOUT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* Set the window to keep screen on */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        /* Acquire wakelock */
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null) {
            mWakeLock = pm.newWakeLock((PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), getClass().getName());
        }

        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }

        /* Play ringtone */
        if (mRingtone == null) {
            Uri uri = (mAlarm.ringtone == null ?
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) :
                    Uri.parse(mAlarm.ringtone));
            mRingtone = new Ringtone(this, uri);
        }
        mRingtone.play();
    }

    @Override
    protected void onPause() {
        Log.i(getClass().getName(), "onPause()");
        super.onPause();

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }

        /* Saving data should be in onPause(), because other activity's onResume() may use this data. */
        if (mAlarm.repeat == 0) {
            mAlarm.enabled = false;
            getHelper().getAlarmDao().update(mAlarm);
        }
    }

    @Override
    protected void onStop() {
        Log.i(getClass().getName(), "onStop()");
        super.onStop();

        mRingtone.stop();
        AlarmService.update(this);
    }
}
