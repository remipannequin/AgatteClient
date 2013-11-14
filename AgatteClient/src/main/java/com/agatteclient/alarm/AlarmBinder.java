package com.agatteclient.alarm;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rémi Pannequin on 10/11/13.
 */
public class AlarmBinder {
    private static AlarmBinder ourInstance = new AlarmBinder();
    private ArrayList<PunchAlarmTime> alarms;


    public static AlarmBinder getInstance() {
        return ourInstance;
    }


    private AlarmBinder() {
        alarms = new ArrayList<PunchAlarmTime>();
    }

    private void loadFromPreferences(SharedPreferences preferences) {

    }

    private void saveToPreferences(SharedPreferences preferences) {

    }

    public List<PunchAlarmTime> getList() {
        return alarms;
    }

    public void addAlarm(PunchAlarmTime a) {
        alarms.add(a);
    }
}
