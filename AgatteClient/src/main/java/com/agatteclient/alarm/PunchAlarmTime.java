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

import android.util.Log;

import com.agatteclient.MainActivity;

import java.util.Calendar;
import java.util.Date;

public class PunchAlarmTime {

    private static final Calendar cal = Calendar.getInstance();
    private int time_of_day;
    private int firing_days;
    private boolean enabled;
    private Type type;

    public PunchAlarmTime() {
        time_of_day = 0;
        firing_days = 0;
        enabled = false;
        type = Type.unconstraigned;
    }

    public PunchAlarmTime(int hour, int minute, Day... firing_days) {
        this.time_of_day = (60 * hour + minute);
        for (Day d : firing_days) {
            this.firing_days |= d.f;
        }
        enabled = true;
        type = Type.unconstraigned;
    }

    public PunchAlarmTime(int hour, int minute) {
        this(hour, minute, Day.monday, Day.tuesday, Day.wednesday, Day.thursday, Day.friday);
    }

    public static PunchAlarmTime fromLong(long l) {
        int t = (int) l & (0x00000000ffffffff);
        int days = (int) ((l >> 32) & 0x0000007f);
        //should be lesser than Ox7F = 127
        boolean enabled = (l & (1l << 48)) != 0;
        boolean arrival_permitted = (l & (1l << 49)) == 0;
        boolean leaving_permitted = (l & (1l << 50)) == 0;

        //Only one of them should be true
        PunchAlarmTime instance = new PunchAlarmTime();
        instance.firing_days = days;
        instance.time_of_day = t;
        instance.enabled = enabled;
        instance.type = Type.fromBooleans(leaving_permitted, arrival_permitted);

        return instance;
    }

    public long toLong() {
        return time_of_day +
                (((long) firing_days) << 32) +
                (enabled ? 1l << 48 : 0) +
                (type.isArrivalPermitted() ? 0 : 1l << 49) +
                (type.isLeavingPermitted() ? 0 : 1l << 50);

    }

    /**
     * Compute the fingerprint of this alarm. This value should be different for each alarms, taking
     * into accounts, hour/minutes, days, but not enabled/disabled or alarm type
     *
     * @return the fingerprint, as a signed int (32 bits)
     */
    public int shortFingerPrint() {
        return time_of_day + (firing_days << 16);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setFireAt(Day day, boolean b) {
        if (isFireAt(day) && !b) {
            this.firing_days -= day.f;
        } else if (!isFireAt(day) && b) {
            this.firing_days += day.f;
        }

    }

    public boolean isFireAt(Day day) {
        return ((this.firing_days & day.f) != 0);
    }

    public void setTime(int hourOfDay, int minute) {
        this.time_of_day = (60 * hourOfDay + minute);
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
                Day d = Day.fromCalDay(cal.get(Calendar.DAY_OF_WEEK));
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
                Day d = Day.fromCalDay(cal.get(Calendar.DAY_OF_WEEK));
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
     * @return the date of the alarm (in the current day)
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

    /**
     * Test equality between two alarms
     * @param o the object to check
     * @return true if both object have the same time, days of week, type and enabled status
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof PunchAlarmTime) {
            PunchAlarmTime other = (PunchAlarmTime) o;
            return (other.enabled == enabled) &&
                    (other.firing_days == firing_days) &&
                    (other.time_of_day == time_of_day) &&
                    (other.getType() == getType());
        } else {
            return false;
        }
    }

    public enum Type {
        unconstraigned(false, false),
        arrival(true, false),
        leaving(false, true);

        private boolean leavingForbidden;
        private boolean arrivalForbidden;

        private Type(boolean leavingForbidden, boolean arrivalForbidden) {
            this.leavingForbidden = leavingForbidden;
            this.arrivalForbidden = arrivalForbidden;
        }

        static Type fromBooleans(boolean leavingPermitted, boolean arrivalPermitted) {
            return (leavingPermitted ? (arrivalPermitted ? unconstraigned : leaving) : arrival);
        }

        /**
         * True if this alarm can be triggered to punch a leaving (i.e. number of punches in the
         * day is odd)
         *
         * @return false for arrival only constraints
         */
        public boolean isLeavingPermitted() {
            return !leavingForbidden;
        }

        /**
         * True if this alarm can be triggered to punch an arrival (i.e. number of punches in the
         * day is even
         *
         * @return false for leaving only constraints
         */
        public boolean isArrivalPermitted() {
            return !arrivalForbidden;
        }

    }

    public enum Day {
        monday(1),
        tuesday(1 << 1),
        wednesday(1 << 2),
        thursday(1 << 3),
        friday(1 << 4),
        saturday(1 << 5),
        sunday(1 << 6);

        final int f;

        Day(int flag) {
            this.f = flag;
        }

        static Day fromCalDay(int d) {
            switch (d) {
                case Calendar.MONDAY:
                    return monday;
                case Calendar.TUESDAY:
                    return tuesday;
                case Calendar.WEDNESDAY:
                    return wednesday;
                case Calendar.THURSDAY:
                    return thursday;
                case Calendar.FRIDAY:
                    return friday;
                case Calendar.SATURDAY:
                    return saturday;
                case Calendar.SUNDAY:
                    return sunday;
                default:
                    return monday;
            }
        }
    }
}
