/*This file is part of AgatteClient.

    AgatteClient is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AgatteClient is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AgatteClient.  If not, see <http://www.gnu.org/licenses/>.*/

package com.agatteclient.alarm;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rémi Pannequin on 01/11/13.
 */
public class PunchAlarmTime {

    private static final Calendar cal = Calendar.getInstance();
    private int time_of_day;
    private int firing_days;
    private boolean enabled;

    public PunchAlarmTime() {
        time_of_day = 0;
        firing_days = 0;
        enabled = false;
    }

    public PunchAlarmTime(int hour, int minute, Day... firing_days) {
        this.time_of_day = (60 * hour + minute);
        for (Day d : firing_days) {
            this.firing_days |= d.f;
        }
        enabled = true;
    }

    public PunchAlarmTime(int hour, int minute) {
        this(hour, minute, Day.monday, Day.tuesday, Day.wednesday, Day.thursday, Day.friday);
    }

    public static PunchAlarmTime fromLong(long l) {
        int t = (int) l;
        int days = (int) (l >> 32);
        boolean enabled = (l & (1l << 48)) != 0;
        PunchAlarmTime instance = new PunchAlarmTime();
        instance.firing_days = days;
        instance.time_of_day = t;
        instance.enabled = enabled;

        return instance;
    }

    public long toLong() {
        return time_of_day + (((long) firing_days) << 32) + (enabled ? 1l << 48 : 0);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
     * @param now
     * @return
     */
    public Date nextAlarm(Date now) {
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
            //TODO: log warning !
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
     * @param currentTimeMillis
     * @return
     */
    public long nextAlarm(long currentTimeMillis) {
        Date d = new Date(currentTimeMillis);
        Date next = nextAlarm(d);
        if (next == null) return -1;
        return next.getTime();
    }

    public Date getTime() {
        cal.set(Calendar.HOUR_OF_DAY, this.time_of_day / 60);
        cal.set(Calendar.MINUTE, this.time_of_day % 60);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PunchAlarmTime) {
            PunchAlarmTime other = (PunchAlarmTime) o;
            return (other.enabled == enabled) && (other.firing_days == firing_days) && (other.time_of_day == time_of_day);
        } else {
            return false;
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
