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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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

    public static final String ALARM_TYPE = "alarm-type";//NON-NLS
    public static final String ALARM_ID = "alarm-id";//NON-NLS
    private static AlarmRegistry ourInstance;


    //TODO: replace all this with DB interactions
    //Manage a collection of alarm, with their fingerprint
    private final Map<PunchAlarmTime, ScheduledAlarm> pending_intent_map;



    private AlarmRegistry() {
        this.pending_intent_map = new HashMap<PunchAlarmTime, ScheduledAlarm>();

    }

    public static AlarmRegistry getInstance() {
        if (ourInstance == null) {
            ourInstance = new AlarmRegistry();
        }
        return ourInstance;
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
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (ScheduledAlarm a : pending_intent_map.values()) {
            alarmManager.cancel(a.intent);
        }
        //pending_intent_map.clear();
        //TODO: remove all alarms for the AM. But is it even possible ?

    }

    public Map<PunchAlarmTime, ScheduledAlarm> getPending_intent_map() {
        return pending_intent_map;
    }

    /**
     * @param context
     */
    public void check(Context context) {
        //TODO: replace with SQL queries :

        // 1. If there is schedule entry with no corresponding alarms, cancel them, and clean entry

        // 2. get the list of enabled Alarms that have no corresponding shedule entry, Add them

        // 3. for every schedule, check that the schedule date is the same than the alarm date
        /*
        Alarms alist = Alarms.getInstance(context);

        //Cancel alarms that are not in the binder any more
        List<PunchAlarmTime> to_remove = new ArrayList<PunchAlarmTime>(pending_intent_map.size());
        for (PunchAlarmTime a : pending_intent_map.keySet()) {
            if (!alist.contains(a)) {
                to_remove.add(a);
            }
        }
        for (PunchAlarmTime a : to_remove) {
            cancelAlarm(context, a);
        }

        //Add alarms that are new, update the other ones
        for (PunchAlarmTime a : alist) {
            if (!pending_intent_map.containsKey(a)) {
                addAlarm(context, a);
            } else {
                updateAlarm(context, a);
            }
        }
        */
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
            AlarmDbHelper db_helper = new AlarmDbHelper(context);
            SQLiteDatabase db = db_helper.getReadableDatabase();
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, AlarmReceiver.class);
            i.putExtra(ALARM_TYPE, alarm.getConstraint().ordinal());
            i.putExtra(ALARM_ID, alarm.getId());

            // Insert in DB
            ContentValues values = new ContentValues(2);
            values.put(AlarmContract.ScheduledAlarm.ALARM_ID, alarm.getId());
            values.put(AlarmContract.ScheduledAlarm.TIME, time);
            long requestId = db.insert(AlarmContract.ScheduledAlarm.TABLE_NAME, null, values);

            PendingIntent pi = PendingIntent.getBroadcast(context, (int)requestId, i, PendingIntent.FLAG_ONE_SHOT);
            am.set(AlarmManager.RTC_WAKEUP, time, pi);
        }
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
        AlarmDbHelper db_helper = new AlarmDbHelper(context);
        SQLiteDatabase db = db_helper.getReadableDatabase();
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

        AlarmDbHelper db_helper = new AlarmDbHelper(context);
        SQLiteDatabase db = db_helper.getReadableDatabase();

        // Query DB to check if an alarm has been scheduled
        String a_id = String.valueOf(alarm.getId());
        String[] selectionArgs = {a_id};
        String sort = AlarmContract.ScheduledAlarm.TIME + "DESC";//NON-NLS
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
            return;
        }

        long now = System.currentTimeMillis();
        long scheduled_time = cursor.getLong(1);
        long time = alarm.nextAlarm(now);
        int request_id = cursor.getInt(0);
        //check time
        if (time != scheduled_time) {
            Intent i = new Intent(context, AlarmReceiver.class);
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
    }

    /**
     * Return the list of scheduled alarm for this day
     *
     * @param now the day to consider
     * @return
     */
    public Iterable<PunchAlarmTime> getScheduledAlarms(Date now) {
        LinkedList<PunchAlarmTime> result = new LinkedList<PunchAlarmTime>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int day = cal.get(Calendar.DAY_OF_YEAR);
        //TODO: query DB with appropriate where clause
        return result;
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

        AlarmDbHelper db_helper = new AlarmDbHelper(context);
        SQLiteDatabase db = db_helper.getReadableDatabase();
        String[] selectionArgs = {String.valueOf(day), String.valueOf(year)};
        Cursor cursor = db.rawQuery(AlarmDbHelper.SQL_QUERY_PAST_ALARM, selectionArgs);
        if (cursor.moveToNext()) {
            RecordedAlarm r = new RecordedAlarm(cursor);
            result.add(r);
        }
        return result;
    }


    /**
     * Report an alarm to have correctly been done, this current day
     *
     * @param id the ID of the Alarm to set as done
     */
    private void setDone(Context context, int id, AlarmContract.ExecStatus status) {
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
        AlarmDbHelper db_helper = new AlarmDbHelper(context);
        SQLiteDatabase db = db_helper.getWritableDatabase();
        ContentValues values = new ContentValues(5);
        values.put(AlarmContract.PastAlarm.ALARM_ID, id);
        values.put(AlarmContract.PastAlarm.EXEC_YEAR, y);
        values.put(AlarmContract.PastAlarm.EXEC_DAY_OF_YEAR, d);
        values.put(AlarmContract.PastAlarm.EXEC_TIME, t);
        values.put(AlarmContract.PastAlarm.EXEC_STATUS, status.ordinal());
        db.insert(AlarmContract.PastAlarm.TABLE_NAME, null, values);

        // Delete from schedule table
        String where = AlarmContract.ScheduledAlarm.ALARM_ID+"=?";
        String[] whereArgs = {String.valueOf(id)};
        db.delete(AlarmContract.ScheduledAlarm.TABLE_NAME, where, whereArgs);
    }


    /**
     * Report an alarm to have correctly been done, this current day
     *
     * @param id the ID of the Alarm to set as done
     */
    public void setSucessfull(Context context, int id) {
        setDone(context, id, AlarmContract.ExecStatus.SUCCESS);
    }


    /**
     * Report an alarm to have failed, this current day
     *
     * @param id the ID of the Alarm to set as done
     */
    public void setFailed(Context context, int id) {
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

        if (!values.containsKey(AlarmContract.Alarm.HOUR)) {
            values.put(AlarmContract.Alarm.HOUR, 0);
        }
        if (!values.containsKey(AlarmContract.Alarm.MINUTE)) {
            values.put(AlarmContract.Alarm.MINUTE, 0);
        }
        if (!values.containsKey(AlarmContract.Alarm.DAYS_OF_WEEK)) {
            values.put(AlarmContract.Alarm.DAYS_OF_WEEK, 0);
        }
        if (!values.containsKey(AlarmContract.Alarm.ENABLED)) {
            values.put(AlarmContract.Alarm.ENABLED, 0);
        }

        AlarmDbHelper db_helper = new AlarmDbHelper(context);
        SQLiteDatabase db = db_helper.getWritableDatabase();
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
        AlarmDbHelper db_helper = new AlarmDbHelper(context);
        SQLiteDatabase db = db_helper.getReadableDatabase();
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


    public Cursor getAlarms(Context context) {
        AlarmDbHelper db_helper = new AlarmDbHelper(context);
        SQLiteDatabase db = db_helper.getReadableDatabase();
        String[] projection = AlarmDbHelper.ALARM_QUERY_COLUMNS;
        String sort = AlarmContract.Alarm.DEFAULT_SORT_ORDER;
        String selection = null;
        String[] selectionArgs = {};
        return  db.query(
                AlarmContract.Alarm.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sort);
    }

    public void remove(Context context, long alarmId) {
        AlarmDbHelper db_helper = new AlarmDbHelper(context);
        SQLiteDatabase db = db_helper.getWritableDatabase();
        String selection = AlarmContract.Alarm._ID + "=?";
        String[] selectionArgs = {String.valueOf(alarmId)};
        db.delete(AlarmContract.Alarm.TABLE_NAME, selection, selectionArgs);
    }

    private void setAttribute(Context context, long id, ContentValues values) {
        AlarmDbHelper db_helper = new AlarmDbHelper(context);
        SQLiteDatabase db = db_helper.getWritableDatabase();
        String where = AlarmContract.Alarm._ID + "=?";
        String[] whereArgs = {String.valueOf(id)};
        db.update(AlarmContract.Alarm.TABLE_NAME, values, where, whereArgs);
    }

    public void setEnabled(Context context, long id, boolean b) {
        ContentValues values = new ContentValues(1);
        values.put(AlarmContract.Alarm.ENABLED, (b? "1":"0"));
        setAttribute(context, id, values);
    }

    public void setDayOfWeek(Context context, long id, AlarmContract.Day d, boolean b) {
        PunchAlarmTime a = getAlarm(context, id);
        a.setFireAt(d, b);
        ContentValues values = new ContentValues(1);
        int dow = a.getDaysOfWeek();
        values.put(AlarmContract.Alarm.DAYS_OF_WEEK, String.valueOf(dow));
        setAttribute(context, id, values);
    }

    public void setConstraint(Context context, long id, AlarmContract.Constraint constraint) {
        ContentValues values = new ContentValues(1);
        values.put(AlarmContract.Alarm.CONSTRAINT, String.valueOf(constraint.ordinal()));
        setAttribute(context, id, values);
    }

    public void setTime(Context context, long id, int hour, int minute) {
        ContentValues values = new ContentValues(2);
        values.put(AlarmContract.Alarm.HOUR, String.valueOf(hour));
        values.put(AlarmContract.Alarm.MINUTE, String.valueOf(minute));
        setAttribute(context, id, values);
    }

    //public for debugging purposes
    public class ScheduledAlarm {
        public PendingIntent intent;
        public int intent_id;
        public long alarm_id;
        public long scheduled_time;

        ScheduledAlarm() {
            //TODO
        }

        public PendingIntent getPendingIntent() {
            if (intent != null) {
                return intent;
            } else {
                //TODO: get intent back from AlarmManager (if any ?!)
                return null;
            }
        }
    }

    public class RecordedAlarm {

        public long alarm_id;
        public Date date_executed;
        public AlarmContract.Constraint constraint;
        public AlarmContract.ExecStatus status;

        RecordedAlarm(Cursor c) {
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
            this.date_executed = cal.getTime();
            this.status = AlarmContract.ExecStatus.values()[c.getInt(4)];
            this.constraint = AlarmContract.Constraint.values()[c.getInt(5)];
        }




    }
}
