package com.morning.data;

import java.util.Collections;
import java.util.List;

import android.content.Context;

public class AlarmEntityManager {
	private AlarmDbHandler db;
	private List<AlarmEntity> allAlarms;
	private static AlarmEntityManager alarmMngr;

	private AlarmEntityManager(Context context) {
		db = new AlarmDbHandler(context);
		allAlarms = db.getAllAlarm();
		Collections.sort(allAlarms);
	}
	
	public static void init(Context context) {
		alarmMngr = new AlarmEntityManager(context);
	}
	
	public static AlarmEntityManager getInstance() {
		assert alarmMngr != null;
		return alarmMngr;
	}

	public List<AlarmEntity> getAllAlarms() {
		return allAlarms;
	}

	public void addAlarm(AlarmEntity alarm) {
		assert db != null;
		assert allAlarms != null;

		int id = db.addAlarm(alarm);
		alarm.setId(id);
		allAlarms.add(0, alarm);
	}

	public void delAlarm(AlarmEntity alarm) {
		assert db != null;
		assert allAlarms != null;

		db.delAlarm(alarm);
		allAlarms.remove(alarm);
	}

	public void updateAlarm(AlarmEntity alarm) {
		assert db != null;
		db.updateAlarm(alarm);
	}
}
