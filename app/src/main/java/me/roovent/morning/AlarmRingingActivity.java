package me.roovent.morning;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import java.util.Calendar;

import me.roovent.morning.model.Alarm;
import me.roovent.morning.model.AlarmDbHelper;


public class AlarmRingingActivity extends OrmLiteBaseActivity<AlarmDbHelper> {
    private static final String TAG = AlarmRingingActivity.class.getName();
    private static final int RINGING_EXPIRED_TIMEOUT = 60 * 1000;

    private PowerManager.WakeLock mWakeLock;
    private RingtonePlayer mRingtonePlayer;
    private Alarm mAlarm;

    private Vibrator mVibrator;
    private RingingNotification mRingingNotification;

    private TextView tvTime;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        /* Wire up */
        tvTime = (TextView) findViewById(R.id.txt_time);
        tvName = (TextView) findViewById(R.id.txt_name);

        findViewById(R.id.btn_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /* Hide status bar and navigation(home & back) */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }

        wakeupScreen();
        populateImageView();
        startVibrating();

        /* Fetch alarm from db */
        int alarmId = getIntent().getIntExtra(Alarm.KEY_ALARM_ID, -1);
        mAlarm = getHelper().getAlarmDao().queryForId(alarmId);
        if (mAlarm == null) {
            Log.e(TAG, "Alarm wasn't found in database. Check why.");
            mAlarm = new Alarm(); /* Just for safety. */
        }

        updateAlarm(mAlarm);

        findViewById(R.id.btn_snooze).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmService.snoozeAlarm(AlarmRingingActivity.this, mAlarm);
                /* Send a notification to allow user to cancel snoozed alarms */
                new SnoozedNotification(AlarmRingingActivity.this, mAlarm).send();
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume()");
        super.onResume();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, mAlarm.hour);
        cal.set(Calendar.MINUTE, mAlarm.minute);
        tvTime.setText(DateFormat.format("hh:mm a", cal.getTime()));

        int alarmId = getIntent().getIntExtra(Alarm.KEY_ALARM_ID, -1);
        if (alarmId != -1 && alarmId != mAlarm.id) {
            mAlarm = getHelper().getAlarmDao().queryForId(alarmId);
            Log.v(TAG, "new alarm comes: " + mAlarm.toString());
            updateAlarm(mAlarm);
        }

        /* Cancel snoozed notification if there is */
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .cancel(SnoozedNotification.getNotificationId(alarmId));
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause()");
        super.onPause();

        /* Saving data should be in onPause(), because other activity's onResume() may use this data. */
        if (mAlarm.repeat == 0) {
            mAlarm.enabled = false;
            getHelper().getAlarmDao().update(mAlarm);
        }

        /* Update alarm schedule */
        AlarmService.update(this);
    }

    /* The activity is no longer visible */
    @Override
    protected void onStop() {
        Log.v(TAG, "onStop()");
        super.onStop();

        /* Send notification to allow user to dismiss the alarm later on. */
        mRingingNotification = new RingingNotification(this);
        mRingingNotification.send();

        releaseScreen();

        /* For some reasons, when the phone is asleep and alarm starts to ring,
         * onStop() will be called before showing up, so finish() cannot be here. */
        //finish();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy()");
        super.onDestroy();

        stopRingtone();
        stopVibrating();

        if (mRingingNotification != null) {
            mRingingNotification.cancel();
        }
    }

    private void updateAlarm(Alarm alarm) {
        if (TextUtils.isEmpty(alarm.name)) {
            tvName.setVisibility(View.GONE);
        } else {
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(mAlarm.name);
        }

        stopRingtone();
        playRingtone(alarm);
    }

    private void populateImageView() {
        Bitmap bitmap = new RingingImageProvider(this).getImage();
        if (bitmap != null) {
            ((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);
        }
    }

    private void startVibrating() {
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

    private void stopVibrating() {
        if (mVibrator != null) {
            mVibrator.cancel();
        }
    }

    private void wakeupScreen() {
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
    }

    private void releaseScreen() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    private void playRingtone(Alarm alarm) {
        /* not silent && no ringtone is playing. */
        if (alarm.ringtone != null && mRingtonePlayer == null) {
            Uri uri = Uri.parse(alarm.ringtone);
            /* When palying, always create a new Ringtone object */
            mRingtonePlayer = new RingtonePlayer(this, uri);
            mRingtonePlayer.play();
        }
    }

    private void stopRingtone() {
        if (mRingtonePlayer != null) {
            mRingtonePlayer.stop();
        }
    }
}
