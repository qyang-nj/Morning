package com.morning.data;

import java.text.DateFormat;
import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;

public class AlarmEntity implements Comparable<AlarmEntity>, Parcelable {
	
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

	public void setRingtone(String sound) {
		this.ringtone = sound;
	}

	public String getRingtone() {
		return ringtone;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	public int getRepeat() {
		return repeat;
	}
	
	public void setCreateTime(long time) {
		this.createTime = time;
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
	
	@Override
	public int compareTo(AlarmEntity another) {
		return Long.valueOf(createTime).compareTo(another.getCreateTime());
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(hour);
		dest.writeInt(minute);
		dest.writeString(name);
		dest.writeString(ringtone);
		dest.writeInt(repeat);
		dest.writeLong(createTime);
		dest.writeInt(activated ? 1 : 0);
	}
	
	private void readFromParcel(Parcel in) {   
		id = in.readInt(); 
		hour = in.readInt();
		minute = in.readInt();
		name = in.readString();
		ringtone = in.readString();
		repeat = in.readInt();
		createTime = in.readLong();
		activated = in.readInt() > 0;
	}

	private Integer id = null; /* If not in db, this should be null. */
	private int hour;
	private int minute;
	private String name = "";
	private String ringtone;
	private int repeat = 0;
	private long createTime;
	private boolean activated = true;
}
