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
import android.util.Log;

import com.agatteclient.MainActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class AlarmList implements List<PunchAlarmTime> {
    private static final String ALARMS_PREF = "alarms-pref"; //NON-NLS
    private static final String ALARM_SHARED_PREFS = "alarms"; //NON-NLS
    private static AlarmList ourInstance;
    private static SharedPreferences preferences;
    private final ArrayList<PunchAlarmTime> alarms;
    private final HashMap<Integer, PunchAlarmTime> alarms_by_fingerprint;
    private AlarmChangeListener listener;


    private AlarmList(SharedPreferences preferences) {
        alarms = new ArrayList<PunchAlarmTime>();
        alarms_by_fingerprint = new HashMap<Integer, PunchAlarmTime>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Set<String> alarms_s = preferences.getStringSet(ALARMS_PREF, null);
            if (alarms_s != null) {
                for (String s : alarms_s) {
                    assert s != null;
                    try {
                        long n = Long.parseLong(s, 16);
                        PunchAlarmTime a = PunchAlarmTime.fromLong(n);
                        alarms.add(a);
                        alarms_by_fingerprint.put(a.shortFingerPrint(), a);
                    } catch (NumberFormatException ex) {
                        Log.e(MainActivity.LOG_TAG, String.format("NumberFormatException when restoring alarms: %s", ex.getMessage()));//NON-NLS
                    }
                }
            }
        } else {
            String alarms_s = preferences.getString(ALARMS_PREF, "");
            int n_tok = alarms_s.length() / 16;
            for (int i = 0; i < alarms_s.length() / 16; i++) {
                try {
                    long n = Long.parseLong(alarms_s.substring(i * 16, (i + 1) * 16), 16);
                    PunchAlarmTime a = PunchAlarmTime.fromLong(n);
                    alarms.add(a);
                    alarms_by_fingerprint.put(a.shortFingerPrint(), a);
                } catch (NumberFormatException ex) {
                    Log.e(MainActivity.LOG_TAG, String.format("NumberFormatException when restoring alarms: %s", ex.getMessage()));//NON-NLS
                }
            }
        }
    }

    public static AlarmList getInstance(Context context) {
        if (ourInstance == null) {
            assert (context != null);
            preferences = context.getSharedPreferences(ALARM_SHARED_PREFS, Context.MODE_PRIVATE);
            ourInstance = new AlarmList(preferences);
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
            alarms_by_fingerprint.put(object.shortFingerPrint(), object);
            saveToPreferences();
            return true;
        } else {
            return false;
        }
    }


    public List<PunchAlarmTime> subList(int start, int end) {
        return alarms.subList(start, end);
    }

    public boolean remove(Object object) {
        if (alarms.remove(object)) {
            if (object instanceof PunchAlarmTime) {
                PunchAlarmTime a = (PunchAlarmTime) object;
                alarms_by_fingerprint.remove(a.shortFingerPrint());
            }
            saveToPreferences();
            return true;
        } else {
            return false;
        }
    }

    public boolean containsAll(Collection<?> collection) {
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

    public PunchAlarmTime getFromFingerPrint(int p) {
        return alarms_by_fingerprint.get(p);
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
        if (object != null) {
            alarms_by_fingerprint.remove(object.shortFingerPrint());
            if (listener != null) {
                listener.onAlarmRemoved(object);
            }
        }
        saveToPreferences();
        return object;
    }

    public PunchAlarmTime set(int index, PunchAlarmTime object) {
        PunchAlarmTime old = alarms.get(index);
        PunchAlarmTime a = alarms.set(index, object);
        if (old != null) {
            alarms_by_fingerprint.remove(old);
        }
        alarms_by_fingerprint.put(object.shortFingerPrint(), object);
        if (listener != null) {
            listener.onAlarmRemoved(old);
            listener.onAlarmAdded(a);
        }
        saveToPreferences();
        return a;
    }

    public Iterator<PunchAlarmTime> iterator() {
        return alarms.iterator();
    }

    public void add(int index, PunchAlarmTime object) {
        alarms.add(index, object);
        alarms_by_fingerprint.put(object.shortFingerPrint(), object);
        if (listener != null) {
            listener.onAlarmAdded(object);
        }
        saveToPreferences();
    }

    public boolean addAll(Collection<? extends PunchAlarmTime> collection) {
        if (alarms.addAll(collection)) {
            for (PunchAlarmTime a : collection) {
                alarms_by_fingerprint.put(a.shortFingerPrint(), a);
                if (listener != null) {
                    listener.onAlarmAdded(a);
                }
            }

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
            for (PunchAlarmTime a : collection) {
                alarms_by_fingerprint.put(a.shortFingerPrint(), a);
                if (listener != null) {
                    listener.onAlarmAdded(a);
                }
            }
            saveToPreferences();
            return true;
        } else {
            return false;
        }
    }

    public void clear() {
        if (listener != null) {
            for (PunchAlarmTime a : this) {
                listener.onAlarmRemoved(a);
            }
        }
        alarms.clear();
        alarms_by_fingerprint.clear();
        saveToPreferences();
    }

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

    public boolean removeAll(Collection<?> collection) {
        for (Object o : collection) {
            if (o instanceof PunchAlarmTime && alarms.contains(o)) {
                PunchAlarmTime a = (PunchAlarmTime) o;
                alarms_by_fingerprint.remove(a.shortFingerPrint());
                if (listener != null) {
                    listener.onAlarmRemoved((PunchAlarmTime) o);
                }
            }
        }
        return alarms.removeAll(collection);
    }

    public ListIterator<PunchAlarmTime> listIterator() {
        return alarms.listIterator();
    }

    public boolean retainAll(Collection<?> collection) {
        return alarms.retainAll(collection);
    }

    public ListIterator<PunchAlarmTime> listIterator(int location) {
        return alarms.listIterator(location);
    }

    public void setEnabled(int i, boolean b) {
        PunchAlarmTime a = alarms.get(i);
        a.setEnabled(b);
        if (listener != null) {
            listener.onAlarmModified(a);
        }
        saveToPreferences();
    }

    public void setFireAt(int i, PunchAlarmTime.Day d, boolean b) {
        PunchAlarmTime a = alarms.get(i);
        a.setFireAt(d, b);
        if (listener != null) {
            listener.onAlarmModified(a);
        }
        saveToPreferences();
    }

    public void setTime(int i, int hourOfDay, int minute) {
        PunchAlarmTime a = alarms.get(i);
        a.setTime(hourOfDay, minute);
        if (listener != null) {
            listener.onAlarmModified(a);
        }
        saveToPreferences();
    }

    public void setType(int i, PunchAlarmTime.Type t) {
        PunchAlarmTime a = alarms.get(i);
        a.setType(t);
        if (listener != null) {
            listener.onAlarmModified(a);
        }
        saveToPreferences();
    }


    public void setAlarmChangeListener(AlarmChangeListener listener) {
        this.listener = listener;
    }

    abstract class AlarmChangeListener {
        abstract public void onAlarmAdded(PunchAlarmTime a);

        abstract public void onAlarmModified(PunchAlarmTime a);

        abstract public void onAlarmRemoved(PunchAlarmTime a);
    }
}