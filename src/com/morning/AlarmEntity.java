package com.morning;

import java.text.DateFormat;
import java.util.Calendar;

public class AlarmEntity {

	public AlarmEntity() {
		Calendar cal = Calendar.getInstance();
		this.hour = cal.get(Calendar.HOUR_OF_DAY);
		this.minute = cal.get(Calendar.MINUTE);
		this.createTime = System.currentTimeMillis();
	}

	public AlarmEntity(int hour, int minute, String name) {
		this();
		this.hour = hour;
		this.minute = minute;
		this.name = name;
	}

	public AlarmEntity(int hour, int minute) {
		this();
		this.hour = hour;
		this.minute = minute;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getHour() {
		return hour;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getMinute() {
		return minute;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

	public String getSound() {
		return sound;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	public int getRepeat() {
		return repeat;
	}

	public long getCreateTime() {
		return createTime;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean on) {
		activated = on;
	}

	@Override
	public String toString() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		String str = DateFormat.getTimeInstance(DateFormat.SHORT).format(
				cal.getTime());
		return str;
	}

	private Integer id = null; /* If not in db, this should be null. */
	private int hour;
	private int minute;
	private String name = "";
	private String sound;
	private int repeat = 0;
	private long createTime;
	private boolean activated = true;
}
