package com.morning.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlarmDbHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 4; /* Only increase */
	private static final String DATABASE_NAME = "alarm_app";
	private static final String TABLE_NAME = "alarms";

	/* Table Columns names */
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_HOUR = "hour";
	private static final String KEY_MINUTE = "minute";
//	private static final String KEY_SOUND = "sound";
	private static final String KEY_REPEAT = "repeat";
	private static final String KEY_ACTIVATED = "activated";

	public AlarmDbHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql_create_table = String
				.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER)",
						TABLE_NAME, KEY_ID, KEY_NAME, KEY_HOUR, KEY_MINUTE,
						KEY_REPEAT, KEY_ACTIVATED);
		db.execSQL(sql_create_table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public int addAlarm(AlarmEntity alarm) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, alarm.getName());
		values.put(KEY_HOUR, alarm.getHour());
		values.put(KEY_MINUTE, alarm.getMinute());
		values.put(KEY_REPEAT, alarm.getRepeat());
		values.put(KEY_ACTIVATED, alarm.isActivated());

		int id = (int) db.insert(TABLE_NAME, null, values);
		db.close();

		return id;
	}

	public void updateAlarm(AlarmEntity alarm) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, alarm.getName());
		values.put(KEY_HOUR, alarm.getHour());
		values.put(KEY_MINUTE, alarm.getMinute());
		values.put(KEY_REPEAT, alarm.getRepeat());
		values.put(KEY_ACTIVATED, alarm.isActivated());

		db.update(TABLE_NAME, values, KEY_ID + " = ?", new String[] { alarm
				.getId().toString() });
		db.close();
	}

	public void delAlarm(AlarmEntity alarm) {
		if (alarm.getId() == null) {
			return; /* Not in the database */
		}

		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, KEY_ID + " = ?", new String[] { alarm.getId()
				.toString() });
		db.close();
	}

	public List<AlarmEntity> getAllAlarm() {
		List<AlarmEntity> alarmList = new ArrayList<AlarmEntity>();
		String selectQuery = "SELECT * FROM " + TABLE_NAME;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				int hour = cursor.getInt(2);
				int minute = cursor.getInt(3);
				AlarmEntity alarm = new AlarmEntity(hour, minute);
				alarm.setId(cursor.getInt(0));
				alarm.setName(cursor.getString(1));
				alarm.setRepeat(cursor.getInt(4));
				alarm.setActivated(cursor.getInt(5) != 0);
				alarmList.add(alarm);
			} while (cursor.moveToNext());
		}

		db.close();
		return alarmList;
	}
}
