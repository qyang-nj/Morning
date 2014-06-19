package com.morning;

import java.util.EnumSet;

public enum RepeatOption {
	SUNDAY(1), MONDAY(1 << 1), TUESDAY(1 << 2), WEDNESDAY(1 << 3), THUERSDAY(
			1 << 4), FRIDAY(1 << 5), SATURDAY(1 << 6);

	private int value;

	private RepeatOption(int val) {
		this.value = val;
	}

	public int getValue() {
		return value;
	}

	public static int set2Val(EnumSet<RepeatOption> set) {
		int val = 0;
		for (RepeatOption opt : set) {
			val += opt.getValue();
		}
		return val;
	}

	public static EnumSet<RepeatOption> val2Set(int value) {
		EnumSet<RepeatOption> set = EnumSet.noneOf(RepeatOption.class);
		for (RepeatOption ro : RepeatOption.values()) {
			if ((ro.getValue() & value) != 0) {
				set.add(ro);
			}
		}
		return set;
	}
	
	public static String set2String(EnumSet<RepeatOption> set) {
		if (set.size() == 0) {
			return "Once";
		}
		
		StringBuilder sb = new StringBuilder();
		for (RepeatOption ro : set) {
			sb.append(ro.toString()).append(" ");
		}
		return sb.toString().trim().replace(" ", ", ");
	}
}
