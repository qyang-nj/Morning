package com.morning.model;

import java.util.Calendar;
import java.util.EnumSet;

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

    public static String formatSet(EnumSet<RepeatOption> set) {
        if (set.size() == 0) {
            return "Once";
        }

        StringBuilder sb = new StringBuilder();
        for (RepeatOption ro : set) {
            sb.append(ro.toString()).append(" ");
        }
        return sb.toString().trim().replace(" ", ", ");
    }

    public int getValue() {
        return value;
    }

    public int getValueOfCalendar() {
        return valueOfCalendar;
    }
}
