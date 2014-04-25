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
import android.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
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
    private final Map<PunchAlarmTime, Pair<PendingIntent, Long>> pending_intent_map;
    private final Map<PunchAlarmTime, Integer> request_code_map;
    private int next_request_code = 0;

    private AlarmRegistry() {
        this.pending_intent_map = new HashMap<PunchAlarmTime, Pair<PendingIntent, Long>>();
        this.request_code_map = new HashMap<PunchAlarmTime, Integer>();
    }

    public static AlarmRegistry getInstance() {
        if (ourInstance == null) {
            ourInstance = new AlarmRegistry();
        }
        return ourInstance;
    }

    /**
     * Compute the next unused requestCode
     *
     * @return the next unused requestCode
     */
    private int getFreeRequestCode() {
        return next_request_code++;
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
        for (PunchAlarmTime a : pending_intent_map.keySet()) {
            if (!binder.contains(a)) {
                cancelAlarm(context, a);
            }
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
            request_code_map.put(alarm, rc);
            pending_intent_map.put(alarm, new Pair<PendingIntent, Long>(pi, alarm.toLong()));
            am.set(AlarmManager.RTC_WAKEUP, time, pi);
        }
    }

    /**
     *
     * @param context
     * @param alarm
     */
    private void cancelAlarm(Context context, PunchAlarmTime alarm) {
        PendingIntent sender = pending_intent_map.get(alarm).first;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        pending_intent_map.remove(alarm);
        request_code_map.remove(alarm);

    }

    /**
     *
     * @param context
     * @param alarm
     */
    private void updateAlarm(Context context, PunchAlarmTime alarm) {
        PendingIntent sender = pending_intent_map.get(alarm).first;
        long fingerprint = pending_intent_map.get(alarm).second;
        //check if alarm time has changed by comparing its current fingerprint with the stored one
        if (alarm.toLong() != fingerprint) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            long now = System.currentTimeMillis();
            long time = alarm.nextAlarm(now);

            if (time >= 0) {
                Intent i = new Intent(context, AlarmReceiver.class);
                //using the same request code, the previous alarm is replaced
                int rc = request_code_map.get(alarm);
                PendingIntent pi = PendingIntent.getBroadcast(context, rc, i, PendingIntent.FLAG_ONE_SHOT);
                pending_intent_map.put(alarm, new Pair(pi, alarm.toLong()));
                am.set(AlarmManager.RTC_WAKEUP, time, pi);
            } else {
                //must cancel
                am.cancel(sender);
                pending_intent_map.remove(alarm);
                request_code_map.remove(alarm);
            }
        }
    }


}
