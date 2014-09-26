/*
 * This file is part of AgatteClient.
 *
 * AgatteClient is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AgatteClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AgatteClient.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2014 Rémi Pannequin (remi.pannequin@gmail.com).
 */

package com.agatteclient.alarm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.agatteclient.alarm.db.AlarmContract.Alarm;
import static com.agatteclient.alarm.db.AlarmContract.PastAlarm;
import static com.agatteclient.alarm.db.AlarmContract.ScheduledAlarm;

/**
 * Helper lass for the Alarms DB
 *
 * Created by Rémi Pannequin on 16/09/14.
 */
public class AlarmDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Alarms.db";//NON-NLS

    private static final String TEXT_TYPE = " TEXT";//NON-NLS
    private static final String INT_TYPE = " INTEGER";//NON-NLS
    private static final String COMMA_SEP = ",";



    private static final String SQL_CREATE_ALARM =
            "CREATE TABLE " + Alarm.TABLE_NAME + " (" +//NON-NLS
            Alarm._ID + " INTEGER PRIMARY KEY," +//NON-NLS
            Alarm.HOUR + INT_TYPE + COMMA_SEP +
            Alarm.MINUTE + INT_TYPE + COMMA_SEP +
            Alarm.DAYS_OF_WEEK + INT_TYPE + COMMA_SEP +
            Alarm.ENABLED + INT_TYPE + COMMA_SEP +
            Alarm.CONSTRAINT + INT_TYPE +
            " )";

    public static final int ALARM_ID_INDEX = 0;
    public static final int ALARM_HOUR_INDEX = 1;
    public static final int ALARM_MINUTE_INDEX = 2;
    public static final int ALARM_DAYS_INDEX = 3;
    public static final int ALARM_ENABLED_INDEX = 4;
    public static final int ALARM_TYPE_INDEX = 5;


    private static final String SQL_CREATE_SCHEDULED_ALARM =
            "CREATE TABLE " + ScheduledAlarm.TABLE_NAME + " (" +//NON-NLS
            ScheduledAlarm._ID + " INTEGER PRIMARY KEY," +//NON-NLS
            ScheduledAlarm.ALARM_ID + INT_TYPE + COMMA_SEP +
            "FOREIGN KEY("+ScheduledAlarm.ALARM_ID + ") REFERENCES " + Alarm.TABLE_NAME+"("+Alarm._ID+")"+//NON-NLS
            ");";

    private static final String SQL_CREATE_PAST_ALARM =
            "CREATE TABLE " + PastAlarm.TABLE_NAME + " (" +//NON-NLS
            PastAlarm._ID + " INTEGER PRIMARY KEY," +//NON-NLS
            PastAlarm.ALARM_ID + INT_TYPE + COMMA_SEP +
            PastAlarm.EXEC_STATUS + INT_TYPE + COMMA_SEP +
            PastAlarm.EXEC_TIME + INT_TYPE + COMMA_SEP +
            PastAlarm.EXEC_DAY_OF_YEAR + INT_TYPE + COMMA_SEP +
            PastAlarm.EXEC_YEAR + INT_TYPE + COMMA_SEP +
            "FOREIGN KEY("+PastAlarm.ALARM_ID + ") REFERENCES " + Alarm.TABLE_NAME+"("+Alarm._ID+")"+//NON-NLS
            ");";

    private static final String SQL_DELETE_ALARM = "DROP TABLE IF EXISTS " + Alarm.TABLE_NAME;//NON-NLS
    private static final String SQL_DELETE_SCHEDULED_ALARM = "DROP TABLE IF EXISTS " + Alarm.TABLE_NAME;//NON-NLS
    private static final String SQL_DELETE_PAST_ALARM = "DROP TABLE IF EXISTS " + Alarm.TABLE_NAME;//NON-NLS

    public static final String[] ALARM_QUERY_COLUMNS = {
            Alarm._ID,
            Alarm.HOUR,
            Alarm.MINUTE,
            Alarm.DAYS_OF_WEEK,
            Alarm.ENABLED,
            Alarm.CONSTRAINT};

    public static final String[] SCHEDULED_QUERY_COLUMNS = {
            ScheduledAlarm._ID,
            ScheduledAlarm.TIME};

    public static final String SQL_QUERY_PAST_ALARM =
            "SELECT "+PastAlarm.ALARM_ID +COMMA_SEP +//NON-NLS
                      PastAlarm.EXEC_TIME + COMMA_SEP +
                      PastAlarm.EXEC_DAY_OF_YEAR + COMMA_SEP +
                      PastAlarm.EXEC_YEAR + COMMA_SEP +
                      PastAlarm.EXEC_STATUS + COMMA_SEP +
                      Alarm.CONSTRAINT +
            " FROM " + PastAlarm.TABLE_NAME + COMMA_SEP + Alarm.TABLE_NAME +//NON-NLS
            " WHERE " + PastAlarm.TABLE_NAME+ "." + PastAlarm.ALARM_ID + "=" + Alarm.TABLE_NAME + "." + Alarm._ID +//NON-NLS
            " AND " + PastAlarm.EXEC_DAY_OF_YEAR + "=?" +//NON-NLS
            " AND " + PastAlarm.EXEC_YEAR + "=?" ;//NON-NLS

    public static final String SCHEDULED_ID_SELECTION = ScheduledAlarm.ALARM_ID + "=?";

    public AlarmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ALARM);
        db.execSQL(SQL_CREATE_SCHEDULED_ALARM);
        db.execSQL(SQL_CREATE_PAST_ALARM);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Nothing to to do (yet) : no previous version
    }

}

