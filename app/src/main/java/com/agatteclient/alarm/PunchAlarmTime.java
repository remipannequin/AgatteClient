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

import android.database.Cursor;
import android.util.Log;

import com.agatteclient.MainActivity;
import com.agatteclient.alarm.db.AlarmContract;
import com.agatteclient.alarm.db.AlarmDbHelper;

import java.util.Calendar;
import java.util.Date;

/**
 * An Alarm that should be executed at a given time.
 * <p/>
 * Backed by a database row
 */
public class PunchAlarmTime {

    private static final Calendar cal = Calendar.getInstance();
    private int time_of_day;
    private int days_of_week;
    private boolean enabled;
    private AlarmContract.Constraint constraint;
    private long id;

    public PunchAlarmTime(int hour, int minute, AlarmContract.Day... firing_days) {
        this.time_of_day = (60 * hour + minute);
        this.days_of_week = AlarmContract.Day.cat(firing_days);
        enabled = true;
    }

    public PunchAlarmTime(int hour, int minute) {
        this(hour, minute, AlarmContract.Day.monday,
                AlarmContract.Day.tuesday, AlarmContract.Day.wednesday,
                AlarmContract.Day.thursday, AlarmContract.Day.friday);
    }

    public PunchAlarmTime(Cursor c) {
        this.id = c.getLong(AlarmDbHelper.ALARM_ID_INDEX);
        this.time_of_day = c.getInt(AlarmDbHelper.ALARM_HOUR_INDEX) * 60 + c.getInt(AlarmDbHelper.ALARM_MINUTE_INDEX);
        this.days_of_week = c.getInt(AlarmDbHelper.ALARM_DAYS_INDEX);
        this.enabled = (c.getInt(AlarmDbHelper.ALARM_ENABLED_INDEX) == 1);
        this.constraint = AlarmContract.Constraint.values()[c.getInt(AlarmDbHelper.ALARM_TYPE_INDEX)];
    }

    public long getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public AlarmContract.Constraint getConstraint() {
        return constraint;
    }

    public boolean isFireAt(AlarmContract.Day day) {
        return AlarmContract.Day.isSet(this.days_of_week, day);
    }

    public int getDaysOfWeek() {
        return days_of_week;
    }


    /**
     * Compute the next date when the alarm should be fired.
     * Result is null if there is no next occurrence (i.e. no firing days)
     *
     * @param now the current time
     * @return the Date when the alarm should go off next or null if it is not activated
     */
    public Date nextAlarm(Date now) {
        if (!isEnabled()) {
            return null;
        }

        cal.setTime(now);
        //get the number of minute of day
        int num_min = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);

        //set calendar to alarm date
        cal.set(Calendar.HOUR_OF_DAY, this.time_of_day / 60);
        cal.set(Calendar.MINUTE, this.time_of_day % 60);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);


        if (num_min < this.time_of_day) {
            //before alarm today
            //search if day is a firing day
            for (int i = 0; i < 7; i++) {
                AlarmContract.Day d = AlarmContract.Day.fromCalDay(cal.get(Calendar.DAY_OF_WEEK));
                if (isFireAt(d)) {
                    return cal.getTime();
                }
                cal.add(Calendar.DATE, 1);
            }
            Log.w(MainActivity.LOG_TAG, "ScheduledAlarm will not fire in the next 7 days.");//NON-NLS
            return null;
        } else {
            //after alarm time: search next firing day
            for (int i = 0; i < 7; i++) {
                cal.add(Calendar.DATE, 1);
                AlarmContract.Day d = AlarmContract.Day.fromCalDay(cal.get(Calendar.DAY_OF_WEEK));
                if (isFireAt(d)) {
                    return cal.getTime();
                }
            }
            return null;
        }
        //cal.set
    }

    /**
     * Get the next time the alarm should be triggered, 0 if it should run immediately or -1 if it does not trigger
     *
     * @param currentTimeMillis now
     * @return the time of the next alarm (in milliseconds)
     */
    public long nextAlarm(long currentTimeMillis) {
        Date d = new Date(currentTimeMillis);
        Date next = nextAlarm(d);
        if (next == null) return -1;
        return next.getTime();
    }

    /**
     * Get the time of the alarm in the present day (even if the alarm does no fire this day !)
     *
     * @return a Date corresponding to the next time the alarm should get off
     */
    public Date getTime() {
        //set calendar to now (to get the present day)
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, this.time_of_day / 60);
        cal.set(Calendar.MINUTE, this.time_of_day % 60);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public int getTimeOfDay() {
        return time_of_day;
    }
}
