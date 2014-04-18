package com.agatteclient.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Pair;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This file is part of AgatteClient
 * <p/>
 * This service has a map of pending intents corresponding to to all the scheduled alarms in the
 * system.
 * <p/>
 * When started, it gets a list of requested alarms from the AlarmBinder singleton, compare it with
 * it set of scheduled alarms, and add/remove/modify any if needed (using the system's AlamrService)
 * <p/>
 * Created by Rémi Pannequin on 18/04/14.
 */
public class AlarmService extends Service {

    //Manage a collection of alarm, with their fingerprint
    private final Map<PunchAlarmTime, Pair<PendingIntent, Long>> pendingIntentMap;

    public AlarmService() {
        this.pendingIntentMap = new HashMap<PunchAlarmTime, Pair<PendingIntent, Long>>();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //TODO: remove all intents
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //TODO: introduce various types of intent : UPDATE, CLEAR_ALL, ?
        Context context = getApplicationContext();
        AlarmBinder binder = AlarmBinder.getInstance(context);

        //Extract longs representing the alarms
        Set<Long> alarms = new HashSet<Long>(binder.size());
        for (PunchAlarmTime a : binder) {
            alarms.add(a.toLong());
        }

        //Cancel alarms that are not in the binder any more
        for (PunchAlarmTime a : pendingIntentMap.keySet()) {
            if (!binder.contains(a)) {
                cancelAlarm(context, a);
            }
        }

        //Add alarms that are new, update the other ones
        for (PunchAlarmTime a : binder) {
            if (!pendingIntentMap.containsKey(a)) {
                addAlarm(context, a);
            } else {
                updateAlarm(context, a);
            }
        }
        return null;
    }


    private void addAlarm(Context context, PunchAlarmTime alarm) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        //TODO: use request code
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_ONE_SHOT);
        pendingIntentMap.put(alarm, new Pair<PendingIntent, Long>(pi, alarm.toLong()));

        long now = System.currentTimeMillis();
        long delay = alarm.nextAlarm(now);
        am.set(AlarmManager.RTC_WAKEUP, delay, pi);
    }


    private void cancelAlarm(Context context, PunchAlarmTime alarm) {
        PendingIntent sender = pendingIntentMap.get(alarm).first;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        pendingIntentMap.remove(alarm);

    }


    private void updateAlarm(Context context, PunchAlarmTime alarm) {
        PendingIntent sender = pendingIntentMap.get(alarm).first;
        long fingerprint = pendingIntentMap.get(alarm).second;
        //check if alarm time has changed by comparing its current fingerprint with the stored one
        if (alarm.toLong() != fingerprint) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(sender);
            Intent i = new Intent(context, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_ONE_SHOT);
            pendingIntentMap.put(alarm, new Pair(pi, alarm.toLong()));
            long now = System.currentTimeMillis();
            long delay = alarm.nextAlarm(now);
            am.set(AlarmManager.RTC_WAKEUP, delay, pi);
        }
    }


}
