package me.roovent.morning;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import me.roovent.morning.model.Alarm;
import me.roovent.morning.model.AlarmDbHelper;


public class AlarmRingingActivity extends OrmLiteBaseActivity<AlarmDbHelper> {
    private static final int RINGING_EXPIRED_TIMEOUT = 60 * 1000;

    private PowerManager.WakeLock mWakeLock;
    private RingtonePlayer mRingtonePlayer;
    private Alarm mAlarm;

    private Handler mAutoSnoozeHandler;
    private Runnable mAutoSnoozeCallback;

    private Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(getClass().getName(), "onCreate()");
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

        wakeupScreen();
    }

    @Override
    protected void onResume() {
        Log.d(getClass().getName(), "onResume()");
        super.onResume();

        /* Fetch alarm from db
         *
         * Because two alarms may ring at the same time (one of them is snoozed),
         * onResume() can be invoked twice for different alarms. Hence, fetching alarm
         * from db should be in onResume() instead of onCreate().
         */
        int alarmId = getIntent().getIntExtra(Alarm.KEY_ALARM_ID, -1);
        mAlarm = getHelper().getAlarmDao().queryForId(alarmId);
        if (mAlarm == null) {
            Log.e(getClass().getName(), "Alarm wasn't found in database. Check why.");
            mAlarm = new Alarm(); /* Just for safety. */
        }

        /* Set TextView: time */
        TextView txtTime = (TextView) findViewById(R.id.txt_time);
        Calendar cal = Calendar.getInstance();
        txtTime.setText(DateFormat.format("hh:mm a", cal.getTime()));

        /* Set TextView: name */
        TextView txtName = (TextView) findViewById(R.id.txt_name);
        if (mAlarm.name == null || mAlarm.name.isEmpty()) {
            txtName.setVisibility(View.GONE);
        } else {
            txtName.setVisibility(View.VISIBLE);
            txtName.setText(mAlarm.name);
        }

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
                finish();
            }
        });

        /* Fetch image from server */
        populateImageView();

        startVibrating();
        playRingtone();
    }

    @Override
    protected void onPause() {
        Log.d(getClass().getName(), "onPause()");
        super.onPause();

        if (mAutoSnoozeHandler != null) {
            mAutoSnoozeHandler.removeCallbacks(mAutoSnoozeCallback);
        }

        /* Saving data should be in onPause(), because other activity's onResume() may use this data. */
        if (mAlarm.repeat == 0) {
            mAlarm.enabled = false;
            getHelper().getAlarmDao().update(mAlarm);
        }

        stopRingtone();
        stopVibrating();

        /* Update alarm schedule */
        AlarmService.update(this);
    }

    /* The activity is no longer visible */
    @Override
    protected void onStop() {
        Log.d(getClass().getName(), "onStop()");
        super.onStop();

        releaseScreen();

        /* For some reasons, when the phone is asleep and alarm starts to ring,
         * onStop() will be called before showing up, so finish() cannot be here. */
        //finish();
    }

    private void populateImageView() {
        String imageUrl = getSharedPreferences(AlarmImageService.PREFERENCE_IMAGE_URL, Context.MODE_MULTI_PROCESS)
                .getString(AlarmImageService.PREFERENCE_IMAGE_URL, null);
        if (imageUrl != null) {
            Picasso.with(AlarmRingingActivity.this).load(imageUrl)
                    .placeholder(R.drawable.logo).into((ImageView) findViewById(R.id.image));
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

    private void playRingtone() {
        if (mAlarm.ringtone != null) {/* not silent */
            Uri uri = Uri.parse(mAlarm.ringtone);
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
