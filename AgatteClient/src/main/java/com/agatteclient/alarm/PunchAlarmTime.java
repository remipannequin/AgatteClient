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

    private int time_of_day;
    private int firing_days;
    private static final Calendar cal = Calendar.getInstance();

    public enum Day {
        monday(1),
        tuesday(1<<2),
        wednesday(1<<3),
        thursday(1<<4),
        friday(1<<5),
        saturday(1<<6),
        sunday(1<<7);

        int f;

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
                    return  wednesday;
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

    public PunchAlarmTime() {
        time_of_day = 0;
        firing_days = 0;
    }

    public PunchAlarmTime(int hour, int minute, Day... firing_days) {
        this.time_of_day = (60 * hour + minute);
        for (Day d : firing_days) {
            this.firing_days |= d.f;
        }
    }

    public long toLong(){
        return time_of_day + (firing_days << 32);
    }

    public static PunchAlarmTime fromLong(long l) {
        int t = (int)l;
        int days = (int)(l>>32);
        PunchAlarmTime instance = new PunchAlarmTime();
        instance.firing_days = days;
        instance.time_of_day = t;
        return instance;
    }

    public PunchAlarmTime(int hour, int minute) {
        this(hour,minute, Day.monday, Day.tuesday, Day.wednesday, Day.thursday , Day.friday);
    }

    public boolean fireAt(Day day) {
        return ((this.firing_days & day.f) != 0);
    }

    /**
     * Compute the next date when the alarm should be fired.
     * Result is null if there is no next occurrence (i.e. no firing days)
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
            for (int i=0; i < 7; i++) {
                Day d = Day.fromCalDay(cal.get(Calendar.DAY_OF_WEEK));
                if (fireAt(d)) {
                    return cal.getTime();
                }
                cal.add(Calendar.DATE, 1);
            }
            return null;
        } else {
            //after alarm time: search next firing day
            for (int i=0; i < 7; i++) {
                cal.add(Calendar.DATE, 1);
                Day d = Day.fromCalDay(cal.get(Calendar.DAY_OF_WEEK));
                if (fireAt(d)) {
                    return cal.getTime();
                }
            }
            return null;
        }
        //cal.set
    }

    public long nextAlarm(long currentTimeMillis) {
        Date d = new Date(currentTimeMillis);
        return nextAlarm(d).getTime();
    }

}
