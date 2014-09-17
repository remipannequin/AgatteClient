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
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, AlarmReceiver.class);
            i.putExtra(ALARM_TYPE, alarm.getConstraint().ordinal());
            //TODO: insert in DB
            int fingerPrint = 0;//alarm.shortFingerPrint();//should be the ID returned by insertion in the Schedule Table


            i.putExtra(ALARM_ID, alarm.getId());
            PendingIntent pi = PendingIntent.getBroadcast(context, fingerPrint, i, PendingIntent.FLAG_ONE_SHOT);
            //ScheduledAlarm o = new ScheduledAlarm(pi, fingerPrint, time);
            //pending_intent_map.put(alarm, o);
            am.set(AlarmManager.RTC_WAKEUP, time, pi);
        }
    }

    /**
     * @param context
     * @param alarm
     */
    private void unschedule(Context context, PunchAlarmTime alarm) {
        PendingIntent sender = pending_intent_map.get(alarm).intent;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        //TODO: delete from DB

        //pending_intent_map.remove(alarm);
    }

    /**
     * @param context
     * @param alarm
     */
    private void reschedule(Context context, PunchAlarmTime alarm) {
        PendingIntent sender = pending_intent_map.get(alarm).intent;
        long now = System.currentTimeMillis();
        /*
        //check if alarm time has changed by comparing its current fingerprint with the stored one
        //If alarm is in the past, update time. If alarm is not enabled any more, check it (cancel)
        if (alarm.getId() != fingerprint || pending_intent_map.get(alarm).time < now || !alarm.isEnabled()) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            long time = alarm.nextAlarm(now);
            if (time >= 0) {
                Intent i = new Intent(context, AlarmReceiver.class);
                i.putExtra(ALARM_TYPE, alarm.getConstraint().ordinal());
                //Alarms alist = Alarms.getInstance(context);
                i.putExtra(ALARM_ID, alarm.getId());
                //using the same request code, the previous alarm is replaced
                PendingIntent pi = PendingIntent.getBroadcast(context, fingerprint, i, PendingIntent.FLAG_ONE_SHOT);
                pending_intent_map.put(alarm, new ScheduledAlarm(pi, fingerprint, time));
                am.set(AlarmManager.RTC_WAKEUP, time, pi);
            } else {
                //must cancel
                am.cancel(sender);
                pending_intent_map.remove(alarm);
            }
        }
        */
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
     * Return the list of done alarm for this day
     *
     * @param now the day to consider
     * @return
     */
    public Iterable<RecordedAlarm> getDoneAlarms(Date now) {
        LinkedList<RecordedAlarm> result = new LinkedList<RecordedAlarm>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int day = cal.get(Calendar.DAY_OF_YEAR);
        //TODO: query DB with appropriate where clause
        return result;
    }

    /**
     * Return the list of failed alarm for this day
     *
     * @param now the day to consider
     * @return
     */
    public Iterable<RecordedAlarm> getFailedAlarms(Date now) {
        LinkedList<RecordedAlarm> result = new LinkedList<RecordedAlarm>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int day = cal.get(Calendar.DAY_OF_YEAR);
        //TODO: query DB with appropriate where clause
        return result;
    }

    /**
     * Report an alarm to have correctly been done, this current day
     *
     * @param id the ID of the Alarm to set as done
     */
    public void setDone(Context context, int id) {
        Calendar cal = Calendar.getInstance();
        //compute exec. date
        PunchAlarmTime a = getAlarm(context, id);
        Date off = a.getTime();
        //RecordedAlarm rec = new RecordedAlarm(off, a.getConstraint());
        cal.setTime(off);
        //TODO: insert
    }

    /**
     * Report an alarm to have failed, this current day
     *
     * @param id the ID of the Alarm to set as done
     */
    public void setFailed(Context context, int id) {
        Calendar cal = Calendar.getInstance();
        //compute exec. date
        PunchAlarmTime a = getAlarm(context, id);
        Date off = a.getTime();
        //RecordedAlarm rec = new RecordedAlarm(off, a.getConstraint());
        cal.setTime(off);
        //TODO: insert
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
        public Date date_executed;
        public long alarm_id;
        public AlarmContract.Constraint type;
        public AlarmContract.ExecStatus status;

        public RecordedAlarm(Date date_executed, long id, AlarmContract.ExecStatus status) {
            this.alarm_id = id;
            this.date_executed = date_executed;
            this.status = status;
        }
    }
}
