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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This service has a map of pending intents corresponding to to all the scheduled alarms in the
 * system.
 * <p/>
 * When started, it gets a list of requested alarms from the AlarmBinder singleton, compare it with
 * it set of scheduled alarms, and add/remove/modify any if needed (using the system's AlamrService)
 * <p/>
 * Created by Rémi Pannequin on 18/04/14.
 */
public class AlarmRegistry {

    private static AlarmRegistry ourInstance;

    //Manage a collection of alarm, with their fingerprint
    private final Map<PunchAlarmTime, Alarm> pending_intent_map;
    private int next_request_code = 0;

    private AlarmRegistry() {
        this.pending_intent_map = new HashMap<PunchAlarmTime, Alarm>();

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
        for (Alarm a : pending_intent_map.values()) {
            alarmManager.cancel(a.intent);
        }
        pending_intent_map.clear();
        //TODO: remove all alarms for the AM. But is it even possible ?
    }

    /**
     * Compute the next unused requestCode
     *
     * @return the next unused requestCode
     */
    private int getFreeRequestCode() {
        return next_request_code++;
    }

    public Map<PunchAlarmTime, Alarm> getPending_intent_map() {
        return pending_intent_map;
    }

    public long getFingerPrint(PunchAlarmTime a) {
        return pending_intent_map.get(a).finger_print;
    }

    public long getRequestCode(PunchAlarmTime a) {
        return pending_intent_map.get(a).request_code;
    }

    public long getTime(PunchAlarmTime a) {
        return pending_intent_map.get(a).time;
    }

    /**
     * @param context
     */
    public void update(Context context) {
        AlarmBinder binder = AlarmBinder.getInstance(context);

        //Extract longs representing the alarms
        Set<Long> alarms = new HashSet<Long>(binder.size());
        for (PunchAlarmTime a : binder) {
            alarms.add(a.toLong());
        }

        //Cancel alarms that are not in the binder any more
        List<PunchAlarmTime> to_remove = new ArrayList<PunchAlarmTime>(pending_intent_map.size());
        for (PunchAlarmTime a : pending_intent_map.keySet()) {
            if (!binder.contains(a)) {
                to_remove.add(a);
            }
        }
        for (PunchAlarmTime a : to_remove) {
            cancelAlarm(context, a);
        }


        //Add alarms that are new, update the other ones
        for (PunchAlarmTime a : binder) {
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
            int rc = getFreeRequestCode();
            PendingIntent pi = PendingIntent.getBroadcast(context, rc, i, PendingIntent.FLAG_ONE_SHOT);
            Alarm o = new Alarm(pi, alarm.toLong(), rc, time);
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
        long fingerprint = pending_intent_map.get(alarm).finger_print;
        //check if alarm time has changed by comparing its current fingerprint with the stored one
        //If alarm is in the past, update time
        if (alarm.toLong() != fingerprint || pending_intent_map.get(alarm).time < now) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            long time = alarm.nextAlarm(now);
            if (time >= 0) {
                Intent i = new Intent(context, AlarmReceiver.class);
                //using the same request code, the previous alarm is replaced
                int rc = pending_intent_map.get(alarm).request_code;
                PendingIntent pi = PendingIntent.getBroadcast(context, rc, i, PendingIntent.FLAG_ONE_SHOT);
                pending_intent_map.put(alarm, new Alarm(pi, fingerprint, rc, time));
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
    public Iterable<? extends Date> getScheduledAlarms(Date now) {
        LinkedList<Date> result = new LinkedList<Date>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int day = cal.get(Calendar.DAY_OF_YEAR);
        for (Alarm al : pending_intent_map.values()) {
            Date d = new Date(al.time);
            //check if same day
            cal.setTime(d);
            if (cal.get(Calendar.DAY_OF_YEAR) == day) {
                result.add(d);
            }
        }
        return result;
    }

    //public for debugging purposes
    public class Alarm {
        public PendingIntent intent;
        public long finger_print;
        public int request_code;
        public long time;

        Alarm(PendingIntent intent, long finger_print, int request_code, long time) {
            this.intent = intent;
            this.finger_print = finger_print;
            this.request_code = request_code;
            this.time = time;
        }
    }
}
