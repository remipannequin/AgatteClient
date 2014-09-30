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

package com.agatteclient.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.agatteclient.MainActivity;
import com.agatteclient.alarm.db.AlarmContract;
import com.agatteclient.alarm.db.AlarmDbHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * This service has a map of pending intents corresponding to to all the scheduled alarms in the
 * system.
 * <p/>
 * When started, it gets a list of requested alarms from the Alarms singleton, compare it with
 * it set of scheduled alarms, and add/remove/modify any if needed (using the system's AlamrService)
 * <p/>
 * Created by Rémi Pannequin on 18/04/14.
 */
public class AlarmRegistry {

    public static final String ALARM_ID = "alarm-id";//NON-NLS
    private static AlarmRegistry ourInstance;

    private SQLiteDatabase db;



    public static AlarmRegistry getInstance() {
        if (ourInstance == null) {
            ourInstance = new AlarmRegistry();
        }
        return ourInstance;
    }

    private SQLiteDatabase getDb(Context context) {
        if (db == null || !db.isOpen()) {
            AlarmDbHelper db_helper = new AlarmDbHelper(context);
            db = db_helper.getWritableDatabase();
        }
        return db;
    }

    public static void reset() {
        ourInstance = null;
    }

    /**
     * Cancel and remove all scheduled alarms
     *
     * @param context
     */
    public void cancelAll(Context context) {
        SQLiteDatabase db = getDb(context);
        String[] col =  {AlarmContract.ScheduledAlarm._ID};
        Cursor c = db.query(
                AlarmContract.ScheduledAlarm.TABLE_NAME,
                col,
                null, null,
                null, null,
                null
        );
        List<Integer> ids = new ArrayList<Integer>();
        while (c.moveToNext()) {
            ids.add(c.getInt(0));
        }

        for (int i : ids) {
            unschedule(context, String.valueOf(i));
        }
    }

    /**
     * reschedule every alarms in the past
     *
     *
     * @param context
     */
    public void check(Context context) {
        //cancel all alarms
        cancelAll(context);
        //reschedule all alarms



    }

    /**
     * @param context
     * @param alarm
     */
    private void schedule(Context context, PunchAlarmTime alarm) {

        // Check if alarm does actually fire
        long now = System.currentTimeMillis();
        long time = alarm.nextAlarm(now);
        if (time >= 0) {
            SQLiteDatabase db = getDb(context);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, AlarmReceiver.class);


            // Insert in DB
            ContentValues values = new ContentValues(2);
            values.put(AlarmContract.ScheduledAlarm.ALARM_ID, alarm.getId());
            values.put(AlarmContract.ScheduledAlarm.TIME, time);
            long requestId = db.insert(AlarmContract.ScheduledAlarm.TABLE_NAME, null, values);
            i.putExtra(ALARM_ID, requestId);
            PendingIntent pi = PendingIntent.getBroadcast(context, (int)requestId, i, PendingIntent.FLAG_ONE_SHOT);
            am.set(AlarmManager.RTC_WAKEUP, time, pi);
        }
    }

    private void schedule(Context context, long alarmId) {
        PunchAlarmTime a = getAlarm(context, alarmId);
        schedule(context, a);
    }


    private void unschedule(Context context, PunchAlarmTime alarm) {
        unschedule(context, String.valueOf(alarm.getId()));
    }


    /**
     * Remove Alarm with corresponding ID from the AlarmManager and from the Scheduled DB table.
     * If alarm is not scheduled, do nothing.
     *
     * @param context
     * @param alarm_id
     */
    private void unschedule(Context context, String alarm_id) {

        SQLiteDatabase db = getDb(context);
        boolean is_scheduled = false;
        // First get corresponding requestID
        String[] selectionArgs = {alarm_id};
        Cursor cursor = db.query(
                AlarmContract.ScheduledAlarm.TABLE_NAME,
                AlarmDbHelper.SCHEDULED_QUERY_COLUMNS,
                AlarmDbHelper.SCHEDULED_ID_SELECTION,
                selectionArgs,
                null, null, null);
        while (cursor.moveToNext()) {
            is_scheduled = true;
            long time = cursor.getLong(1);
            int request_id = cursor.getInt(0);
            Intent i = new Intent(context, AlarmReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(context, request_id, i, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(sender);
        }
        // Delete from DB (if there is anything to delete)
        if (is_scheduled) {
            db.delete(
                    AlarmContract.ScheduledAlarm.TABLE_NAME,
                    AlarmDbHelper.SCHEDULED_ID_SELECTION,
                    selectionArgs);
        }
        cursor.close();
    }

    /**
     * Verify that the alarm passed in parameter is correctly scheduled:
     *
     * cases:
     * 1. alarm is not enabled : un-schedule it (no changes if not scheduled)
     * 2. alarm is enabled
     *    2.1. There is no entry in the DB : schedule it.
     *    2.2. there is an entry in the DB : check time, update if needed
     *
     *
     * @param context
     * @param alarm
     */
    private void reschedule(Context context, PunchAlarmTime alarm) {

        if (!alarm.isEnabled()) {
            unschedule(context, alarm);
            return;
        }

        SQLiteDatabase db = getDb(context);

        // Query DB to check if an alarm has been scheduled
        String a_id = String.valueOf(alarm.getId());
        String[] selectionArgs = {a_id};
        String sort = AlarmContract.ScheduledAlarm.TIME + " DESC";//NON-NLS
        Cursor cursor = db.query(
                AlarmContract.ScheduledAlarm.TABLE_NAME,
                AlarmDbHelper.SCHEDULED_QUERY_COLUMNS,
                AlarmDbHelper.SCHEDULED_ID_SELECTION,
                selectionArgs,
                null, null,
                sort);

        //first entry is oldest date
        if (!cursor.moveToNext()) {
            //No entry in DB : call schedule !
            schedule(context, alarm);
            cursor.close();
            return;
        }

        long now = System.currentTimeMillis();
        long scheduled_time = cursor.getLong(1);
        long time = alarm.nextAlarm(now);
        int request_id = cursor.getInt(0);
        //check time
        if (time != scheduled_time) {
            Intent i = new Intent(context, AlarmReceiver.class);

            i.putExtra(ALARM_ID, request_id);
            PendingIntent pi = PendingIntent.getBroadcast(context, request_id, i, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            // Reschedule alarm
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pi);
            ContentValues values = new ContentValues(1);
            values.put(AlarmContract.ScheduledAlarm.TIME, time);
            db.update(
                    AlarmContract.ScheduledAlarm.TABLE_NAME,
                    values,
                    AlarmContract.ScheduledAlarm._ID+"="+request_id,
                    null);
        } //else : nothing to update...

        // Remove all other entries, if any
        if (!cursor.isLast()) {
            String where = AlarmContract.ScheduledAlarm.ALARM_ID+"=? AND NOT "+AlarmContract.ScheduledAlarm._ID+"=?";//NON-NLS
            String[] whereArgs = {a_id, String.valueOf(request_id)};
            db.delete(
                    AlarmContract.ScheduledAlarm.TABLE_NAME,
                    where,
                    whereArgs);
        }
        cursor.close();
    }


    /**
     * Return the list of alarms for this day : both scheduled, successfully done and failed
     *
     * @param now the day to consider
     * @return
     */
    public Iterable<RecordedAlarm> getRecordedAlarms(Context context, Date now) {
        LinkedList<RecordedAlarm> result = new LinkedList<RecordedAlarm>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int day = cal.get(Calendar.DAY_OF_YEAR);
        int year = cal.get(Calendar.YEAR);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long d_begin = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        long d_end = cal.getTimeInMillis();
        // Query DB with appropriate where clause
        SQLiteDatabase db = getDb(context);

        String[] whereArgs = {String.valueOf(d_begin), String.valueOf(d_end)};
        Cursor cursor = db.rawQuery(
                AlarmDbHelper.SQL_QUERY_SCHEDULED_ALARM,
                whereArgs);
        while (cursor.moveToNext()) {
            RecordedAlarm r = new ScheduledAlarm(cursor);
            result.add(r);
        }
        cursor.close();

        String[] selectionArgs = {String.valueOf(day), String.valueOf(year)};
        Cursor cursor2 = db.rawQuery(AlarmDbHelper.SQL_QUERY_PAST_ALARM, selectionArgs);
        while (cursor2.moveToNext()) {
            RecordedAlarm r = new PastAlarm(cursor2);
            result.add(r);
        }
        cursor2.close();
        return result;
    }


    /**
     * Report an alarm to have correctly been done, this current day, ANd re-schedule it.
     *
     * @param id the ID of the Alarm to set as done
     */
    private void setDone(Context context, long id, AlarmContract.ExecStatus status) {
        Calendar cal = Calendar.getInstance();
        //compute exec. date
        PunchAlarmTime a = getAlarm(context, id);
        Date off = a.getTime();
        //RecordedAlarm rec = new RecordedAlarm(off, a.getConstraint());
        cal.setTime(off);
        int d = cal.get(Calendar.DAY_OF_YEAR);
        int y = cal.get(Calendar.YEAR);
        int t = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
        // Insert in past alarm table

        SQLiteDatabase db = getDb(context);
        ContentValues values = new ContentValues(5);
        values.put(AlarmContract.PastAlarm.ALARM_ID, id);
        values.put(AlarmContract.PastAlarm.EXEC_YEAR, y);
        values.put(AlarmContract.PastAlarm.EXEC_DAY_OF_YEAR, d);
        values.put(AlarmContract.PastAlarm.EXEC_TIME, t);
        values.put(AlarmContract.PastAlarm.EXEC_STATUS, status.ordinal());
        db.insert(AlarmContract.PastAlarm.TABLE_NAME, null, values);
        // reschedule
        reschedule(context, a);
    }


    /**
     * Report an alarm to have correctly been done, this current day
     *
     * @param id the ID of the Alarm to set as done
     */
    public void setSucessfull(Context context, long id) {
        setDone(context, id, AlarmContract.ExecStatus.SUCCESS);
    }


    /**
     * Report an alarm to have failed, this current day
     *
     * @param id the ID of the Alarm to set as done
     */
    public void setFailed(Context context, long id) {
       setDone(context, id, AlarmContract.ExecStatus.FAILURE);
    }


    /**
     * Create a new alarm, and add it to the database
     * @param context
     * @param h
     * @param m
     */
    public void addAlarm(Context context, int h, int m) {
        ContentValues values = new ContentValues();
        values.put(AlarmContract.Alarm.HOUR, h);
        values.put(AlarmContract.Alarm.MINUTE, m);
        values.put(AlarmContract.Alarm.DAYS_OF_WEEK, 31);//five first days of week
        values.put(AlarmContract.Alarm.ENABLED, 0);

        SQLiteDatabase db = getDb(context);
        long rowId = db.insert(AlarmContract.Alarm.TABLE_NAME, null, values);
        if (rowId < 0) {
            throw new SQLException("Failed to insert row DB ");
        }
        Log.v(MainActivity.LOG_TAG, "Added alarm rowId = " + rowId);//NON-NLS
    }


    /**
     * Lookup alarm with given ID in the database.
     *
     * @param context
     * @param alarmId the ID of the alarm to search
     * @return a PunchAlarmTime if found, null of no alarm with ID exists
     */
    public PunchAlarmTime getAlarm(Context context, long alarmId) {
        SQLiteDatabase db = getDb(context);
        String[] projection = AlarmDbHelper.ALARM_QUERY_COLUMNS;
        String sort = AlarmContract.Alarm.DEFAULT_SORT_ORDER;
        String selection = AlarmContract.Alarm._ID + "=?";
        String[] selectionArgs = {String.valueOf(alarmId)};
        Cursor cursor = db.query(
                AlarmContract.Alarm.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sort);
        PunchAlarmTime alarm = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                alarm = new PunchAlarmTime(cursor);
            }
            cursor.close();
        }
        return alarm;
    }


    //TODO: who will close this DB ??
    public Cursor getAlarms(Context context) {
        SQLiteDatabase db = getDb(context);
        String[] projection = AlarmDbHelper.ALARM_QUERY_COLUMNS;
        String sort = AlarmContract.Alarm.DEFAULT_SORT_ORDER;
        String selection = null;
        String[] selectionArgs = {};
        return db.query(
                AlarmContract.Alarm.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sort);
    }


    /**
     * Unschedule and then remove alarm identified by alarmId
     * @param context
     * @param alarmId
     */
    public void remove(Context context, long alarmId) {
        unschedule(context, String.valueOf(alarmId));
        SQLiteDatabase db = getDb(context);
        String selection = AlarmContract.Alarm._ID + "=?";
        String[] selectionArgs = {String.valueOf(alarmId)};
        db.delete(AlarmContract.Alarm.TABLE_NAME, selection, selectionArgs);
    }


    private void setAttribute(Context context, long id, ContentValues values) {
        SQLiteDatabase db = getDb(context);
        String where = AlarmContract.Alarm._ID + "=?";
        String[] whereArgs = {String.valueOf(id)};
        db.update(AlarmContract.Alarm.TABLE_NAME, values, where, whereArgs);
    }


    /**
     * Enable/disable this alarm, schedule it accordingly
     * @param context
     * @param id
     * @param b
     */
    public void setEnabled(Context context, long id, boolean b) {
        ContentValues values = new ContentValues(1);
        values.put(AlarmContract.Alarm.ENABLED, (b? "1":"0"));
        setAttribute(context, id, values);
        if (b) {
            schedule(context, id);
        } else {
            unschedule(context, String.valueOf(id));
        }
    }


    public void setDayOfWeek(Context context, long id, AlarmContract.Day d, boolean b) {
        PunchAlarmTime a = getAlarm(context, id);
        a.setFireAt(d, b);
        ContentValues values = new ContentValues(1);
        int dow = a.getDaysOfWeek();
        values.put(AlarmContract.Alarm.DAYS_OF_WEEK, String.valueOf(dow));
        setAttribute(context, id, values);
        reschedule(context, a);
    }


    public void setConstraint(Context context, long id, AlarmContract.Constraint constraint) {
        ContentValues values = new ContentValues(1);
        values.put(AlarmContract.Alarm.CONSTRAINT, String.valueOf(constraint.ordinal()));
        setAttribute(context, id, values);
    }


    public void setTime(Context context, long id, int hour, int minute) {
        PunchAlarmTime a = getAlarm(context, id);
        ContentValues values = new ContentValues(2);
        values.put(AlarmContract.Alarm.HOUR, String.valueOf(hour));
        values.put(AlarmContract.Alarm.MINUTE, String.valueOf(minute));
        setAttribute(context, id, values);
        reschedule(context, a);
    }

    /**
     * Search the Alarm DB to get the corresponding Alarm ID (positive long)
     * If no such data is found, return -1
     *
     * @param context
     * @param schedule_id
     * @return
     */
    public RecordedAlarm getIdFromSchedule(Context context, long schedule_id) {
        SQLiteDatabase db = getDb(context);
        String[] whereArgs = {String.valueOf(schedule_id)};
        Cursor cursor = db.rawQuery(
                AlarmDbHelper.SQL_QUERY_SCHEDULED_ALARM_BY_ID,
                whereArgs);
        if (cursor.moveToNext()) {
            RecordedAlarm r = new ScheduledAlarm(cursor);
            cursor.close();
            return r;
        }
        return null;
    }


    public abstract class RecordedAlarm {

        public long alarm_id;
        public Date date;
        public AlarmContract.Constraint constraint;
        public AlarmContract.ExecStatus status;

    }


    private class PastAlarm extends RecordedAlarm {

        PastAlarm(Cursor c) {
            Calendar cal = Calendar.getInstance();
            this.alarm_id = c.getInt(0);
            int t = c.getInt(1);
            int h = t/60;
            int m = t - (h * 60);
            int d = c.getInt(2);
            int y = c.getInt(3);
            cal.set(Calendar.YEAR, y);
            cal.set(Calendar.DAY_OF_YEAR, d);
            cal.set(Calendar.HOUR, h);
            cal.set(Calendar.MINUTE, m);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            this.date = cal.getTime();
            this.status = AlarmContract.ExecStatus.values()[c.getInt(4)];
            this.constraint = AlarmContract.Constraint.values()[c.getInt(5)];
        }
    }


    private class ScheduledAlarm extends RecordedAlarm {

        ScheduledAlarm(Cursor c) {

            this.alarm_id = c.getInt(0);
            long time = c.getLong(1);
            this.date = new Date(time);
            this.constraint = AlarmContract.Constraint.values()[c.getInt(2)];
            this.status = AlarmContract.ExecStatus.SCHEDULED;
        }
    }




}
