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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class AlarmBinder implements List<PunchAlarmTime> {
    private static final String ALARMS_PREF = "alarms-pref"; //NON-NLS
    private static final String ALARM_SHARED_PREFS = "alarms"; //NON-NLS
    private static AlarmBinder ourInstance;
    private static SharedPreferences preferences;
    private final ArrayList<PunchAlarmTime> alarms;


    private AlarmBinder(SharedPreferences preferences) {
        alarms = new ArrayList<PunchAlarmTime>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Set<String> alarms_s = preferences.getStringSet(ALARMS_PREF, null);
            if (alarms_s != null) {
                for (String s : alarms_s) {
                    assert s != null;
                    try {
                        long n = Long.parseLong(s, 16);
                        alarms.add(PunchAlarmTime.fromLong(n));
                    } catch (NumberFormatException ex) {
                        //TODO Manage error
                    }
                }
            }
        } else {
            String alarms_s = preferences.getString(ALARMS_PREF, "");
            int n_tok = alarms_s.length() / 8;
            for (int i = 0; i < alarms_s.length() / 8; i++) {
                try {
                    long n = Long.parseLong(alarms_s.substring(i * 8, (i + 1) * 8), 16);
                    alarms.add(PunchAlarmTime.fromLong(n));
                } catch (NumberFormatException ex) {
                    //TODO Manage error
                }
            }
        }
    }

    public static AlarmBinder getInstance(Context context) {
        if (ourInstance == null) {
            assert (context != null);
            preferences = context.getSharedPreferences(ALARM_SHARED_PREFS, Context.MODE_PRIVATE);
            ourInstance = new AlarmBinder(preferences);
        }
        return ourInstance;
    }

    public void saveToPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Set<String> alarms_s = new HashSet<String>(alarms.size());
            int i = 0;
            for (PunchAlarmTime a : alarms) {
                long n = a.toLong();
                alarms_s.add(Long.toHexString(n));
            }
            editor.putStringSet(ALARMS_PREF, alarms_s); // value to store

        } else {
            StringBuilder alarms_s = new StringBuilder();
            for (PunchAlarmTime a : alarms) {
                long n = a.toLong();
                alarms_s.append(String.format("%016X", n));
            }
            editor.putString(ALARMS_PREF, alarms_s.toString());
        }
        editor.commit();
    }

    public boolean add(PunchAlarmTime object) {
        if (alarms.add(object)) {
            saveToPreferences();
            return true;
        } else {
            return false;
        }
    }

    @NotNull
    public List<PunchAlarmTime> subList(int start, int end) {
        return alarms.subList(start, end);
    }

    public boolean remove(Object object) {
        if (alarms.remove(object)) {
            saveToPreferences();
            return true;
        } else {
            return false;
        }
    }

    public boolean containsAll(@NotNull Collection<?> collection) {
        return alarms.containsAll(collection);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    public int size() {
        return alarms.size();
    }

    public int lastIndexOf(Object object) {
        return alarms.lastIndexOf(object);
    }

    public PunchAlarmTime get(int index) {
        return alarms.get(index);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public boolean isEmpty() {
        return alarms.isEmpty();
    }

    public boolean contains(Object object) {
        return alarms.contains(object);
    }

    public PunchAlarmTime remove(int index) {
        PunchAlarmTime object = alarms.remove(index);
        saveToPreferences();
        return object;
    }

    public PunchAlarmTime set(int index, PunchAlarmTime object) {
        PunchAlarmTime a = alarms.set(index, object);
        saveToPreferences();
        return a;
    }

    @NotNull
    public Iterator<PunchAlarmTime> iterator() {
        return alarms.iterator();
    }

    public void add(int index, PunchAlarmTime object) {
        alarms.add(index, object);
        saveToPreferences();
    }

    public boolean addAll(Collection<? extends PunchAlarmTime> collection) {
        if (alarms.addAll(collection)) {
            saveToPreferences();
            return true;
        } else {
            return false;
        }
    }

    public <T> T[] toArray(T[] contents) {
        return alarms.toArray(contents);
    }

    public boolean addAll(int index, Collection<? extends PunchAlarmTime> collection) {
        if (alarms.addAll(index, collection)) {
            saveToPreferences();
            return true;
        } else {
            return false;
        }
    }

    public void clear() {
        alarms.clear();
        saveToPreferences();
    }

    @NotNull
    public Object[] toArray() {
        return alarms.toArray();
    }

    public void ensureCapacity(int minimumCapacity) {
        alarms.ensureCapacity(minimumCapacity);
    }

    public int indexOf(Object object) {
        return alarms.indexOf(object);
    }

    public void trimToSize() {
        alarms.trimToSize();
    }

    public boolean removeAll(@NotNull Collection<?> collection) {
        return alarms.removeAll(collection);
    }

    @NotNull
    public ListIterator<PunchAlarmTime> listIterator() {
        return alarms.listIterator();
    }

    public boolean retainAll(@NotNull Collection<?> collection) {
        return alarms.retainAll(collection);
    }

    @NotNull
    public ListIterator<PunchAlarmTime> listIterator(int location) {
        return alarms.listIterator(location);
    }

    public void addAlarm(PunchAlarmTime a) {
        alarms.add(a);
        saveToPreferences();
    }

    public void setEnabled(int i, boolean b) {
        alarms.get(i).setEnabled(b);
        saveToPreferences();
    }

    public void setFireAt(int i, PunchAlarmTime.Day d, boolean b) {
        alarms.get(i).setFireAt(d, b);
        saveToPreferences();
    }

    public void setTime(int i, int hourOfDay, int minute) {
        alarms.get(i).setTime(hourOfDay, minute);
        saveToPreferences();
    }
}

