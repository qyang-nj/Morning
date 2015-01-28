package com.morning;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
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

import retrofit.RestAdapter;


public class AlarmRingingActivity extends OrmLiteBaseActivity<AlarmDbHelper> {
    private static final int RINGING_EXPIRED_TIMEOUT = 60 * 1000;

    private PowerManager.WakeLock mWakeLock;
    private Ringtone mRingtone;
    private Alarm mAlarm;

    private Handler mAutoSnoozeHandler;
    private Runnable mAutoSnoozeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        /* Hide status bar and navigation(home & back) */
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

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
                Toast.makeText(AlarmRingingActivity.this, "Snoozed for 10 minitues", Toast.LENGTH_SHORT).show();
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

        if (mAutoSnoozeHandler != null) {
            mAutoSnoozeHandler.removeCallbacks(mAutoSnoozeCallback);
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

    private void populateImageView() {
        new AsyncTask<Handler, Void, Void>() {
            @Override
            protected Void doInBackground(Handler... handlers) {
                try {
                    RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(AlarmRingingImage.BASE_URL).build();
                    AlarmRingingImage.ImageGetter image = restAdapter.create(AlarmRingingImage.ImageGetter.class);
                    final String imageUrl = image.getImageUrl().url;
                    handlers[0].post(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.with(AlarmRingingActivity.this).load(imageUrl)
                                    .placeholder(R.drawable.logo).into((ImageView) findViewById(R.id.image));
                        }
                    });
                } catch (Exception e) {
                    Log.e(getClass().getName(), e.getMessage());
                }
                return null;
            }
        }.execute(new Handler(), null, null);
    }
}
