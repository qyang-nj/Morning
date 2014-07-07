package com.morning.data;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.EnumSet;

import android.os.Parcel;
import android.os.Parcelable;

public class AlarmEntity implements Comparable<AlarmEntity>, Parcelable {

	public static final Parcelable.Creator<AlarmEntity> CREATOR = new Creator<AlarmEntity>() {
		@Override
		public AlarmEntity[] newArray(int size) {
			return new AlarmEntity[size];
		}

		@Override
		public AlarmEntity createFromParcel(Parcel source) {
			return new AlarmEntity(source);
		}
	};

	public AlarmEntity() {
		Calendar cal = Calendar.getInstance();
		this.hour = cal.get(Calendar.HOUR_OF_DAY);
		this.minute = cal.get(Calendar.MINUTE);
		this.createTime = System.currentTimeMillis();
	}

	public AlarmEntity(Parcel in) {
		readFromParcel(in);
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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean on) {
		enabled = on;
	}
	
	public void setSnooze(int min) {
		this.snooze = min;
	}

	/**
	 * Get the next alert time of this alarm.
	 * @return the absolute milliseconds
	 */
	public long getNextTime() {

		if (this.enabled == false) {
			return Long.MAX_VALUE;
		}

		Calendar cal = Calendar.getInstance();
		if (this.snooze > 0) {
			cal.add(Calendar.MINUTE, snooze);
			return cal.getTimeInMillis();
		}
		
		cal.set(Calendar.HOUR_OF_DAY, this.hour);
		cal.set(Calendar.MINUTE, this.minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		if (this.repeat == 0) {
			if (cal.compareTo(Calendar.getInstance()) == -1) { /* Before */
				cal.add(Calendar.DAY_OF_YEAR, 1);
			}
		} else {
			EnumSet<RepeatOption> options = RepeatOption.getSetFromValue(this.repeat);
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			int dayCount = 0;
			for (; dayCount < 7; ++dayCount) {
				int day = ((dayOfWeek - 1) + dayCount) % 7 + 1;
				if (options.contains(RepeatOption.fromCalendar(day))) {
					break;
				}
			}
			cal.add(Calendar.DAY_OF_YEAR, dayCount);
		}
		return cal.getTimeInMillis();
	}
	
	public void commit() {
		if (id == null) {
			return;
		}
		AlarmDbHandler.getInstance().updateAlarm(this);
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
		dest.writeSerializable(id);
		dest.writeInt(hour);
		dest.writeInt(minute);
		dest.writeString(name);
		dest.writeString(ringtone);
		dest.writeInt(repeat);
		dest.writeLong(createTime);
		dest.writeInt(enabled ? 1 : 0);
	}

	private void readFromParcel(Parcel in) {
		id = (Integer) in.readSerializable();
		hour = in.readInt();
		minute = in.readInt();
		name = in.readString();
		ringtone = in.readString();
		repeat = in.readInt();
		createTime = in.readLong();
		enabled = in.readInt() > 0;
	}

	private Integer id = null; /* If not in db, this should be null. */
	private int hour;
	private int minute;
	private String name = "";
	private String ringtone;
	private int repeat = 0; /* Enum of RepeatOption */
	private long createTime;
	private boolean enabled = true;
	private int snooze = 0; /* minutes */
}
