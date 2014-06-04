package com.qyang;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmServiceHelper {

	public AlarmServiceHelper(Context context) {
		this.context = context;
	}

	public void setAlarm(int hour, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		if (cal.compareTo(Calendar.getInstance()) == -1) { /* Before */
			cal.add(Calendar.DAY_OF_YEAR, 1);
		}

		Intent intentAlarm = new Intent(context, AlarmReciever.class);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				PendingIntent.getBroadcast(context, 1, intentAlarm,
						PendingIntent.FLAG_UPDATE_CURRENT));
	}

	public void setAlarm(int hour, int minute, int repeat) {
		if (repeat == 0) {
			setAlarm(hour, minute);
			return;
		}
	}

	private Context context;
}
