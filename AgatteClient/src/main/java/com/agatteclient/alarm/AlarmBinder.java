package com.agatteclient.alarm;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rémi Pannequin on 10/11/13.
 */
public class AlarmBinder {
    private static final String ALARMS_PREF = "alarm-pref";
    private static AlarmBinder ourInstance = new AlarmBinder();
    private ArrayList<PunchAlarmTime> alarms;


    public static AlarmBinder getInstance() {
        return ourInstance;
    }


    private AlarmBinder() {
        alarms = new ArrayList<PunchAlarmTime>();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void loadFromPreferences(SharedPreferences preferences) {
        //preferences
        Set<String> alarms_s = preferences.getStringSet(ALARMS_PREF, null);
        for (String s : alarms_s) {
            long n = Long.getLong(s);
            alarms.add(PunchAlarmTime.fromLong(n));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void saveToPreferences(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> alarms_s = new HashSet<String>(alarms.size());
        int i = 0;
        for (PunchAlarmTime a : alarms) {
            long n = a.toLong();
            alarms_s.add(Long.toHexString(n));
        }
        editor.putStringSet(ALARMS_PREF, alarms_s); // value to store
        editor.commit();
    }

    public List<PunchAlarmTime> getList() {
        return alarms;
    }

    public void addAlarm(PunchAlarmTime a) {
        alarms.add(a);
    }
}
