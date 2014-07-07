package com.morning.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlarmDbHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 8; /* Only increase */
	private static final String DATABASE_NAME = "alarm_app";
	private static final String TABLE_NAME = "alarms";

	/* Table Columns names */
	private static final String KEY_ID = "_id"; /* Must be "_id". */
	private static final String KEY_NAME = "name";
	private static final String KEY_HOUR = "hour";
	private static final String KEY_MINUTE = "minute";
	private static final String KEY_RINGTONE = "ringtone";
	private static final String KEY_REPEAT = "repeat";
	private static final String KEY_ACTIVATED = "activated";
	private static final String KEY_CTIME = "ctime";
	private static final String KEY_NEXT_TIME = "ntime";
	
	private static AlarmDbHandler instance;
	
	public static AlarmDbHandler getInstance() {
		if (instance == null) {
			throw new RuntimeException("AlarmDbHandler has not been initialized.");
		}
		return instance;
	}
	
	public static void init(Context context) {
		instance = new AlarmDbHandler(context);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql_create_table = String
				.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s TEXT, %s INTEGER)",
						TABLE_NAME, KEY_ID, KEY_NAME, KEY_HOUR, KEY_MINUTE,
						KEY_REPEAT, KEY_ACTIVATED, KEY_CTIME, KEY_RINGTONE, KEY_NEXT_TIME);
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
		values.put(KEY_ACTIVATED, alarm.isEnabled());
		values.put(KEY_CTIME, alarm.getCreateTime());
		values.put(KEY_RINGTONE, alarm.getRingtone());
		values.put(KEY_NEXT_TIME, alarm.getNextTime());

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
		values.put(KEY_ACTIVATED, alarm.isEnabled());
		values.put(KEY_CTIME, alarm.getCreateTime());
		values.put(KEY_RINGTONE, alarm.getRingtone());
		values.put(KEY_NEXT_TIME, alarm.getNextTime());

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
	
	public static AlarmEntity getAlarmFromCursor(Cursor cursor) {
		AlarmEntity alarm = new AlarmEntity();
		alarm.setId(cursor.getInt(0));
		alarm.setName(cursor.getString(1));
		alarm.setHour(cursor.getInt(2));
		alarm.setMinute(cursor.getInt(3));
		alarm.setRepeat(cursor.getInt(4));
		alarm.setEnabled(cursor.getInt(5) > 0);
		alarm.setCreateTime(cursor.getLong(6));
		alarm.setRingtone(cursor.getString(7));
		return alarm;
	}
	
	public Cursor getCursorOfList() {
		String selectQuery = String.format("SELECT * FROM %s ORDER BY %s DESC",
				TABLE_NAME, KEY_CTIME);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}

	private AlarmDbHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
}
