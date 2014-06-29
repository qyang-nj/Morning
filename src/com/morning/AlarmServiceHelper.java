package com.morning;

import java.util.Calendar;

import com.morning.data.AlarmEntity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmServiceHelper {

	public AlarmServiceHelper(Context context) {
		this.context = context;
	}

	public void setAlarm(AlarmEntity alarm) {
		setAlarm(alarm, false);
	}

	/* For test use only. */
	public void setAlarm(AlarmEntity alarm, boolean now) {
		Calendar cal = Calendar.getInstance();

		if (!now) {
			cal.set(Calendar.HOUR_OF_DAY, alarm.getHour());
			cal.set(Calendar.MINUTE, alarm.getMinute());
			cal.set(Calendar.SECOND, 0);
			if (cal.compareTo(Calendar.getInstance()) == -1) { /* Before */
				cal.add(Calendar.DAY_OF_YEAR, 1);
			}
		}

		Intent intentAlarm = new Intent(context, AlarmReciever.class);
		intentAlarm.putExtra(Constants.INTEND_KEY_ALARM, alarm);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				PendingIntent.getBroadcast(context, 1, intentAlarm,
						PendingIntent.FLAG_UPDATE_CURRENT));
	}

	private Context context;
}
