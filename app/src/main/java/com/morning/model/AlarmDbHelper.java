package com.morning.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Qing on 1/25/15.
 */
public class AlarmDbHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "morning.db";
    private static final int DATABASE_VERSION = 2;

    private RuntimeExceptionDao<Alarm, Integer> alarmDao = null;

    public AlarmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        Log.i(AlarmDbHelper.class.getName(), "onCreate");
        try {
            TableUtils.createTable(connectionSource, Alarm.class);
        } catch (SQLException e) {
            Log.e(AlarmDbHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

        Alarm a = new Alarm();
        a.name = "Test";
        a.enabled = false;
        getAlarmDao().create(a);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Log.i(AlarmDbHelper.class.getName(), "onUpgrade");
        try {
            TableUtils.dropTable(connectionSource, Alarm.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(AlarmDbHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        super.close();
        alarmDao = null;
    }

    public RuntimeExceptionDao<Alarm, Integer> getAlarmDao() {
        if (alarmDao == null) {
            alarmDao = getRuntimeExceptionDao(Alarm.class);
        }
        return alarmDao;
    }
}
