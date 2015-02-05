package me.roovent.morning.model;

import android.content.Context;

import java.util.Calendar;
import java.util.EnumSet;

import me.roovent.morning.R;

public enum RepeatOption {
    SUNDAY(1, Calendar.SUNDAY),
    MONDAY(1 << 1, Calendar.MONDAY),
    TUESDAY(1 << 2, Calendar.TUESDAY),
    WEDNESDAY(1 << 3, Calendar.WEDNESDAY),
    THURSDAY(1 << 4, Calendar.THURSDAY),
    FRIDAY(1 << 5, Calendar.FRIDAY),
    SATURDAY(1 << 6, Calendar.SATURDAY);

    private int value;
    private int valueOfCalendar; /* Value of Calendar.SUNDAY ant so on */

    private static final int valueOnce = 0;
    private static final int valueWeekday = getValueFromSet(EnumSet.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY));
    private static final int valueWeekend = getValueFromSet(EnumSet.of(SUNDAY, SATURDAY));;

    private RepeatOption(int val, int valOfCal) {
        this.value = val;
        this.valueOfCalendar = valOfCal;
    }

    public static RepeatOption fromCalendar(int valOfCal) {
        for (RepeatOption ro : EnumSet.allOf(RepeatOption.class)) {
            if (valOfCal == ro.getValueOfCalendar()) {
                return ro;
            }
        }
        throw new IllegalArgumentException("Not a valid DAY_OF_WEEK value.");
    }

    public static int getValueFromSet(EnumSet<RepeatOption> set) {
        int val = 0;
        for (RepeatOption opt : set) {
            val += opt.getValue();
        }
        return val;
    }

    public static EnumSet<RepeatOption> getSetFromValue(int value) {
        EnumSet<RepeatOption> set = EnumSet.noneOf(RepeatOption.class);
        for (RepeatOption ro : RepeatOption.values()) {
            if ((ro.getValue() & value) != 0) {
                set.add(ro);
            }
        }
        return set;
    }

    public static String formatSet(EnumSet<RepeatOption> set, Context context) {
        String str;

        int value = getValueFromSet(set);
        if (value == valueOnce) {
            str =context.getString(R.string.repeat_once);
        } else if (value == valueWeekday) {
            str =context.getString(R.string.repeat_weekday);
        } else if (value == valueWeekend) {
            str =context.getString(R.string.repeat_weekend);
        } else {
            StringBuilder sb = new StringBuilder();
            for (RepeatOption ro : set) {
                sb.append(ro.toString()).append(" ");
            }
            str = sb.toString().trim().replace(" ", ", ");
        }
        return str;
    }

    public int getValue() {
        return value;
    }

    public int getValueOfCalendar() {
        return valueOfCalendar;
    }
}
