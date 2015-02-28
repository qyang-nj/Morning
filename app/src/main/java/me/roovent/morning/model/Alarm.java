package me.roovent.morning.model;

import android.util.SparseIntArray;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by Qing on 1/25/15.
 */

@DatabaseTable(tableName = "alarm")
public class Alarm {
    public static final String KEY_ALARM_ID = "KEY_ALARM_ID";
    public static final String KEY_IS_SNOOZED = "isSnoozed";

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
        return String.format("Alarm(%d) %02d:%02d {%x}", id, hour, minute, repeat);
    }

    public String getTimeString() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(cal.getTime());
    }

    /**
     * Get the next alert time of this alarm.
     *
     * @return the absolute milliseconds
     */
    public long getNextTime() {

        if (!this.enabled) {
            return Long.MAX_VALUE;
        }

        Calendar cal = Calendar.getInstance();
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
            int dayOfWeek = calendar2Ordinal.get(cal.get(Calendar.DAY_OF_WEEK));
            int offset = (cal.compareTo(Calendar.getInstance()) == -1) ? 1 : 0;
            int dayCount = 0;
            for (; dayCount < 7; ++dayCount) {
                int day = (dayOfWeek + dayCount + offset) % 7;
                if (options.contains(RepeatOption.fromCalendar(ordinal2Calendar[day]))) {
                    break;
                }
            }
            cal.add(Calendar.DAY_OF_YEAR, dayCount + offset);
        }
        return cal.getTimeInMillis();
    }

    public static Alarm findEarliestAlarm(List<Alarm> alarms) {
        Alarm earliestAlarm = null;
        long earliestTime;

        if (alarms.size() > 0) {
            earliestAlarm = alarms.get(0);
            earliestTime = earliestAlarm.getNextTime();
            for (int i = 1; i < alarms.size(); ++i) {
                long thisTime = alarms.get(i).getNextTime();
                if (earliestTime > thisTime) {
                    earliestAlarm = alarms.get(i);
                    earliestTime = thisTime;
                }
            }
        }
        return earliestAlarm;
    }

    private static SparseIntArray calendar2Ordinal;
    private static int[] ordinal2Calendar = new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
            Calendar.WEDNESDAY, Calendar.TUESDAY, Calendar.FRIDAY, Calendar.SATURDAY};

    static {
        calendar2Ordinal = new SparseIntArray();
        calendar2Ordinal.put(Calendar.SUNDAY, 0);
        calendar2Ordinal.put(Calendar.MONDAY, 1);
        calendar2Ordinal.put(Calendar.TUESDAY, 2);
        calendar2Ordinal.put(Calendar.WEDNESDAY, 3);
        calendar2Ordinal.put(Calendar.TUESDAY, 4);
        calendar2Ordinal.put(Calendar.FRIDAY, 5);
        calendar2Ordinal.put(Calendar.SATURDAY, 6);
    }
}
