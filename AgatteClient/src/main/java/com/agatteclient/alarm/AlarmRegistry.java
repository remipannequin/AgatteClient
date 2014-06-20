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
import android.content.Context;
import android.content.Intent;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This service has a map of pending intents corresponding to to all the scheduled alarms in the
 * system.
 * <p/>
 * When started, it gets a list of requested alarms from the AlarmList singleton, compare it with
 * it set of scheduled alarms, and add/remove/modify any if needed (using the system's AlamrService)
 * <p/>
 * Created by Rémi Pannequin on 18/04/14.
 */
public class AlarmRegistry {

    public static final String ALARM_TYPE = "alarm-type";//NON-NLS
    public static final String ALARM_ID = "alarm-id";//NON-NLS
    private static AlarmRegistry ourInstance;

    //Manage a collection of alarm, with their fingerprint
    private final Map<PunchAlarmTime, ScheduledAlarm> pending_intent_map;
    // Map of alarms reported to have been done, number of day in year is the key
    private final Multimap<Integer, RecordedAlarm> done_history;
    private final Multimap<Integer, RecordedAlarm> failed_history;

    private AlarmRegistry() {
        this.pending_intent_map = new HashMap<PunchAlarmTime, ScheduledAlarm>();
        this.done_history = ArrayListMultimap.create();
        this.failed_history = ArrayListMultimap.create();
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
        pending_intent_map.clear();
        //TODO: remove all alarms for the AM. But is it even possible ?
    }

    public Map<PunchAlarmTime, ScheduledAlarm> getPending_intent_map() {
        return pending_intent_map;
    }

    public long getFingerPrint(PunchAlarmTime a) {
        return pending_intent_map.get(a).finger_print;
    }

    public long getTime(PunchAlarmTime a) {
        return pending_intent_map.get(a).time;
    }

    /**
     * @param context
     */
    public void update(Context context) {
        AlarmList alist = AlarmList.getInstance(context);

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
    }

    /**
     * @param context
     * @param alarm
     */
    private void addAlarm(Context context, PunchAlarmTime alarm) {
        // Check if alarm does actually fire
        long now = System.currentTimeMillis();
        long time = alarm.nextAlarm(now);
        if (time >= 0) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, AlarmReceiver.class);
            i.putExtra(ALARM_TYPE, alarm.getType().ordinal());
            int fingerPrint = alarm.shortFingerPrint();
            AlarmList alist = AlarmList.getInstance(context);
            i.putExtra(ALARM_ID, alist.indexOf(alarm));
            PendingIntent pi = PendingIntent.getBroadcast(context, fingerPrint, i, PendingIntent.FLAG_ONE_SHOT);
            ScheduledAlarm o = new ScheduledAlarm(pi, fingerPrint, time);
            pending_intent_map.put(alarm, o);
            am.set(AlarmManager.RTC_WAKEUP, time, pi);
        }
    }

    /**
     * @param context
     * @param alarm
     */
    private void cancelAlarm(Context context, PunchAlarmTime alarm) {
        PendingIntent sender = pending_intent_map.get(alarm).intent;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        pending_intent_map.remove(alarm);
    }

    /**
     * @param context
     * @param alarm
     */
    private void updateAlarm(Context context, PunchAlarmTime alarm) {
        PendingIntent sender = pending_intent_map.get(alarm).intent;
        long now = System.currentTimeMillis();
        int fingerprint = pending_intent_map.get(alarm).finger_print;
        //check if alarm time has changed by comparing its current fingerprint with the stored one
        //If alarm is in the past, update time. If alarm is not enabled any more, check it (cancel)
        if (alarm.shortFingerPrint() != fingerprint || pending_intent_map.get(alarm).time < now || !alarm.isEnabled()) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            long time = alarm.nextAlarm(now);
            if (time >= 0) {
                Intent i = new Intent(context, AlarmReceiver.class);
                i.putExtra(ALARM_TYPE, alarm.getType().ordinal());
                AlarmList alist = AlarmList.getInstance(context);
                i.putExtra(ALARM_ID, alist.indexOf(alarm));
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
        for (Map.Entry<PunchAlarmTime, ScheduledAlarm> entry : pending_intent_map.entrySet()) {
            ScheduledAlarm al = entry.getValue();
            Date d = new Date(al.time);
            //check if same day
            cal.setTime(d);
            if (cal.get(Calendar.DAY_OF_YEAR) == day) {

                result.add(entry.getKey());
            }
        }
        return result;
    }

    /**
     * Return the list of done alarm for this day
     *
     * @param now the day to consider
     * @return
     */
    public Iterable<RecordedAlarm> getDoneAlarms(Date now) {
        LinkedList<PunchAlarmTime> result = new LinkedList<PunchAlarmTime>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int day = cal.get(Calendar.DAY_OF_YEAR);
        return done_history.get(day);
    }

    /**
     * Return the list of failed alarm for this day
     *
     * @param now the day to consider
     * @return
     */
    public Iterable<RecordedAlarm> getFailedAlarms(Date now) {
        LinkedList<PunchAlarmTime> result = new LinkedList<PunchAlarmTime>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int day = cal.get(Calendar.DAY_OF_YEAR);
        return failed_history.get(day);
    }

    /**
     * Report an alarm to have correctly been done, this current day
     *
     * @param a
     */
    public void setDone(PunchAlarmTime a) {
        Calendar cal = Calendar.getInstance();
        //compute exec. date
        Date off = a.getTime();
        RecordedAlarm rec = new RecordedAlarm(off, a.getType());
        cal.setTime(off);
        done_history.put(cal.get(Calendar.DAY_OF_YEAR), rec);
    }

    /**
     * Report an alarm to have failed, this current day
     *
     * @param a
     */
    public void setFailed(PunchAlarmTime a) {
        Calendar cal = Calendar.getInstance();
        //compute exec. date
        Date off = a.getTime();
        RecordedAlarm rec = new RecordedAlarm(off, a.getType());
        cal.setTime(off);
        failed_history.put(cal.get(Calendar.DAY_OF_YEAR), rec);
    }






    //public for debugging purposes
    public class ScheduledAlarm {
        public PendingIntent intent;
        public int finger_print;//This is also used as request code
        public long time;

        ScheduledAlarm(PendingIntent intent, int finger_print, long time) {
            this.intent = intent;
            this.finger_print = finger_print;
            this.time = time;
        }
    }

    public class RecordedAlarm {
        public Date date_executed;
        public PunchAlarmTime.Type type;

        public RecordedAlarm(Date date_executed, PunchAlarmTime.Type type) {
            this.date_executed = date_executed;
            this.type = type;
        }
    }
}
