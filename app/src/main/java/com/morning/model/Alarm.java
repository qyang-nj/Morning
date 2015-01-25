package com.morning.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by qing on 1/25/15.
 */

@DatabaseTable(tableName = "accounts")
public class Alarm {

    @DatabaseField(id = true)
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
    }
}
