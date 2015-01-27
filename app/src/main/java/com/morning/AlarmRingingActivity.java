package com.morning;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.morning.model.Alarm;
import com.morning.model.AlarmDbHelper;

import java.util.Calendar;


public class AlarmRingingActivity extends OrmLiteBaseActivity<AlarmDbHelper> {
    private Ringtone mRingtone;
    private Alarm mAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        int alarmId = getIntent().getIntExtra(Alarm.KEY_ALARM_ID, -1);
        mAlarm = getHelper().getAlarmDao().queryForId(alarmId);
        if (mAlarm == null) {
            Log.e(getClass().getName(), "Alarm wasn't found in database. Check why.");
            mAlarm = new Alarm(); /* Just for safety. */
        }

        TextView txtTime = (TextView) findViewById(R.id.txt_time);
        Calendar cal = Calendar.getInstance();
        txtTime.setText(DateFormat.format("hh:mm a", cal.getTime()));

        /* Play ringtone */
        Uri uri = (mAlarm.ringtone == null ?
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) :
                Uri.parse(mAlarm.ringtone));
        mRingtone = new Ringtone(this, uri);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRingtone.play();
    }

    @Override
    protected void onPause() {
        Log.i(getClass().getName(), "onPause()");
        super.onPause();

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
