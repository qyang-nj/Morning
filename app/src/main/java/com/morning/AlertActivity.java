package com.morning;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.morning.data.AlarmDbHandler;
import com.morning.data.AlarmEntity;
import com.morning.data.ImageManager;

import java.util.Calendar;

public class AlertActivity extends Activity {

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the system UI. This is to prevent the jarring
     * behavior of controls going away while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int viewId = view.getId();
            if (viewId == R.id.btn_snooze) {
                alarm.setSnooze(Constants.SNOOZE_TIME);
            } else if (viewId == R.id.btn_off) {

            }
            finish();
            return false;
        }
    };
    private AlarmEntity alarm = null;
    private Ringtone ringtone;
    private PowerManager.WakeLock wakeLock;
    private AlarmDbHandler dbHandler;
    private AlarmServiceHelper alarmServiceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "AlertActivity.onCreate()");

        setContentView(R.layout.activity_alert);
        getActionBar().hide();

        Intent in = getIntent();
        alarm = in.getParcelableExtra(Constants.INTEND_KEY_ALARM);
        assert alarm != null;

        dbHandler = AlarmDbHandler.getInstance(this);
        alarmServiceHelper = AlarmServiceHelper.getInstance(this);

        TextView lblCurrentTime = (TextView) findViewById(R.id.lblCurrentTime);
        Calendar cal = Calendar.getInstance();
        lblCurrentTime.setText(DateFormat.format("hh:mm a", cal.getTime()));

        ImageView imgShown = (ImageView) findViewById(R.id.imgShown);
        imgShown.setImageBitmap(ImageManager.getRandomImage());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(R.id.btn_snooze).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.btn_off).setOnTouchListener(mDelayHideTouchListener);

        /* Wake up phone */
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, Constants.TAG);
        wakeLock.acquire();
        Log.i(Constants.TAG, "AlertActivity: wakelock acquired.");

        /* Play ringtone */
        Uri uri = alarm.getRingtone() == null ? RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) : Uri
                .parse(alarm.getRingtone());

        ringtone = new Ringtone(this, uri);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ringtone.play();
    }

    @Override
    protected void onPause() {
        Log.i(Constants.TAG, "AlertActivity.onPause()");
        super.onPause();

        /* Saving data should be in onPause����, because other activity's onResume() may use this data. */
        if (alarm.getRepeat() == 0) {
            alarm.setEnabled(false);
            dbHandler.updateAlarm(alarm);
        }
    }

    @Override
    protected void onStop() {
        Log.i(Constants.TAG, "AlertActivity.onStop()");
        super.onPause();

        /* Release wakelock */
        if (wakeLock != null) {
            wakeLock.release();
            Log.i(Constants.TAG, "AlertActivity: wakelock released.");
        }

        ringtone.stop();
        alarmServiceHelper.updateAlert();
    }
}