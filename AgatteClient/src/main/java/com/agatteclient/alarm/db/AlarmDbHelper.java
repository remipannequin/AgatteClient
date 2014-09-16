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

import static com.agatteclient.alarm.db.AlarmDbContract.*;

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
            "CREATE TABLE " + ScheduledAlarm.TABLE_NAME + " (" +//NON-NLS
            Alarm._ID + " INTEGER PRIMARY KEY," +//NON-NLS
            Alarm.COLUMN_NAME_TIME + INT_TYPE + COMMA_SEP +
            Alarm.COLUMN_NAME_DAYS_MON + INT_TYPE + COMMA_SEP +
            Alarm.COLUMN_NAME_DAYS_TUE + INT_TYPE + COMMA_SEP +
            Alarm.COLUMN_NAME_DAYS_WED + INT_TYPE + COMMA_SEP +
            Alarm.COLUMN_NAME_DAYS_THU + INT_TYPE + COMMA_SEP +
            Alarm.COLUMN_NAME_DAYS_FRI + INT_TYPE + COMMA_SEP +
            Alarm.COLUMN_NAME_DAYS_SAT + INT_TYPE + COMMA_SEP +
            Alarm.COLUMN_NAME_DAYS_SUN + INT_TYPE + COMMA_SEP +
            Alarm.COLUMN_NAME_TYPE + INT_TYPE + COMMA_SEP +
            " )";

    private static final String SQL_CREATE_SCHEDULED_ALARM =
            "CREATE TABLE " + PastAlarm.TABLE_NAME + " (" +//NON-NLS
            ScheduledAlarm._ID + " INTEGER PRIMARY KEY," +//NON-NLS
            ScheduledAlarm.COLUMN_NAME_ALARM_ID + INT_TYPE +
            ScheduledAlarm.COLUMN_NAME_SCHEDULE_ID + INT_TYPE +
            "FOREIGN KEY("+ScheduledAlarm.COLUMN_NAME_ALARM_ID + ") REFERENCE " + Alarm.TABLE_NAME+"("+Alarm._ID+")"+//NON-NLS
            ");";

    private static final String SQL_CREATE_PAST_ALARM =
            "CREATE TABLE " + Alarm.TABLE_NAME + " (" +//NON-NLS
            PastAlarm._ID + " INTEGER PRIMARY KEY," +//NON-NLS
            PastAlarm.COLUMN_NAME_ALARM_ID + INT_TYPE +
            PastAlarm.COLUMN_NAME_EXEC_STATUS + INT_TYPE +
            PastAlarm.COLUMN_NAME_EXEC_TIME + INT_TYPE +
            "FOREIGN KEY("+PastAlarm.COLUMN_NAME_ALARM_ID + ") REFERENCE " + Alarm.TABLE_NAME+"("+Alarm._ID+")"+//NON-NLS
            ");";


    private static final String SQL_DELETE_ALARM = "DROP TABLE IF EXISTS " + Alarm.TABLE_NAME;//NON-NLS
    private static final String SQL_DELETE_SCHEDULED_ALARM = "DROP TABLE IF EXISTS " + Alarm.TABLE_NAME;//NON-NLS
    private static final String SQL_DELETE_PAST_ALARM = "DROP TABLE IF EXISTS " + Alarm.TABLE_NAME;//NON-NLS


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

