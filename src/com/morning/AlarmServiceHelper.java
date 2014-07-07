package com.morning;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.morning.data.AlarmEntity;

public class AlarmServiceHelper {

	public AlarmServiceHelper(Context context) {
		this.context = context;
	}

	public void setAlarm(AlarmEntity alarm) {
		setAlarm(alarm, false);
	}

	/* For test use only. */
	public void setAlarm(AlarmEntity alarm, boolean now) {
		Intent intentAlarm = new Intent(context, AlarmReciever.class);
		intentAlarm.putExtra(Constants.INTEND_KEY_ALARM, alarm);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		
		long alertTime = now ? 0 : alarm.getNextTime();
		alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime,
				PendingIntent.getBroadcast(context, 1, intentAlarm,
						PendingIntent.FLAG_UPDATE_CURRENT));
	}

	private Context context;
}
