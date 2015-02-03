package com.morning;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.morning.model.Alarm;
import com.morning.model.AlarmDbHelper;
import com.squareup.picasso.Picasso;

import java.util.Calendar;


public class AlarmRingingActivity extends OrmLiteBaseActivity<AlarmDbHelper> {
    private static final int RINGING_EXPIRED_TIMEOUT = 60 * 1000;

    private PowerManager.WakeLock mWakeLock;
    private Ringtone mRingtone;
    private Alarm mAlarm;

    private Handler mAutoSnoozeHandler;
    private Runnable mAutoSnoozeCallback;

    private Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        /* Hide status bar and navigation(home & back) */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }

        /* Fetch alarm from db */
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
                AlarmService.snoozeAlarm(AlarmRingingActivity.this, mAlarm);
                Toast.makeText(AlarmRingingActivity.this, "Snoozed for 10 minutes", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        /* Fetch image from server */
        populateImageView();

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
        new Handler().postDelayed(releaseWakelock, RINGING_EXPIRED_TIMEOUT);

        /* Set auto snooze */
        mAutoSnoozeCallback = new Runnable() {
            @Override
            public void run() {
                AlarmService.snoozeAlarm(AlarmRingingActivity.this, mAlarm);
                finish();
            }
        };
        mAutoSnoozeHandler = new Handler();
        mAutoSnoozeHandler.postDelayed(mAutoSnoozeCallback, RINGING_EXPIRED_TIMEOUT);
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
        if (mAlarm.ringtone != null) {/* not silent */
            if (mRingtone == null) {
                Uri uri = Uri.parse(mAlarm.ringtone);
                mRingtone = new Ringtone(this, uri);
            }
            mRingtone.play();
        }

        /* Vibrate */
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean vibrate = sharedPref.getBoolean("pref_vibrate", true);
        if (vibrate) {
            if (mVibrator == null) {
                mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            }
            mVibrator.vibrate(new long[]{0, 500, 500}, 1);
            Log.i(getClass().getName(), "Vibrating");
        }
    }

    @Override
    protected void onPause() {
        Log.i(getClass().getName(), "onPause()");
        super.onPause();

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }

        if (mAutoSnoozeHandler != null) {
            mAutoSnoozeHandler.removeCallbacks(mAutoSnoozeCallback);
        }

        /* Saving data should be in onPause(), because other activity's onResume() may use this data. */
        if (mAlarm.repeat == 0) {
            mAlarm.enabled = false;
            getHelper().getAlarmDao().update(mAlarm);
        }

        /* Stop ringing */
        if (mRingtone != null) {
            mRingtone.stop();
        }

        /* Stop vibrating */
        if (mVibrator != null) {
            mVibrator.cancel();
        }

        /* Update alarm schedule */
        AlarmService.update(this);
    }

    private void populateImageView() {
        String imageUrl = getSharedPreferences(AlarmImageService.PREFERENCE_IMAGE_URL, Context.MODE_MULTI_PROCESS)
                .getString(AlarmImageService.PREFERENCE_IMAGE_URL, null);
        if (imageUrl != null) {
            Picasso.with(AlarmRingingActivity.this).load(imageUrl)
                    .placeholder(R.drawable.logo).into((ImageView) findViewById(R.id.image));
        }
    }
}
