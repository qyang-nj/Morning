package com.morning.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by Qing on 1/25/15.
 */

@DatabaseTable(tableName = "alarm")
public class Alarm {
    public static final String KEY_ALARM_ID = "KEY_ALARM_ID";

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(canBeNull = false)
    public int hour;

    @DatabaseField(canBeNull = false)
    public int minute;

    @DatabaseField
    public String name;

    @DatabaseField
    public String ringtone;

    @DatabaseField
    public int repeat = 0; /* Enum of RepeatOption */

    @DatabaseField
    public long createTime;

    @DatabaseField
    public boolean enabled = true;

    public Alarm() {
        /* ORMLite needs a no-arg constructor */
        id = -1;

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
    }

    @Override
    public String toString() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        String str = DateFormat.getTimeInstance(DateFormat.SHORT).format(cal.getTime());
        return str;
    }
}
