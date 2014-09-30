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

package com.agatteclient.alarm.db;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Calendar;

/**
 * Contract for the Alarm Database
 *
 * Created by Rémi Pannequin on 16/09/14.
 */
public class AlarmContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public AlarmContract() {}

    /**
     * A data kind representing a punch alarm
     */
    public static abstract class Alarm implements BaseColumns {
        public static final String TABLE_NAME = "Alarm";//NON-NLS
        public static final String HOUR = "hour";//NON-NLS
        public static final String MINUTE = "minute"; //NON-NLS
        public static final String DAYS_OF_WEEK = "days_of_week";//NON-NLS
        public static final String ENABLED = "enabled";//NON-NLS
        public static final String CONSTRAINT = "time_constraint";//NON-NLS

        public static final String DEFAULT_SORT_ORDER = HOUR + ", " + MINUTE + " ASC";//NON-NLS

    }

    /**
     * A data kind representing a scheduled alarm
     */
    public static abstract class ScheduledAlarm implements BaseColumns {
        public static final String TABLE_NAME = "ScheduledAlarm";//NON-NLS
        public static final String ALARM_ID = "alarm_id";//NON-NLS
        /**
         * Scheduled date, i.e. number of milliseconds from epoch
         */
        public static final String TIME = "time";//NON-NLS
    }

    public enum ExecStatus {
        SUCCESS,
        FAILURE,
        INVALID,
        SCHEDULED;
    }

    /**
     * A data kind representing a alarm that have been executed (successfully or not)
     */
     public static abstract class PastAlarm implements BaseColumns {
        public static final String TABLE_NAME = "PastAlarm";//NON-NLS
        public static final String ALARM_ID = "alarm_id";//NON-NLS
        /**
         * Number of minutes in the day INTEGER
         * 60 * hour + minute, with hour in 24 hours format
         */
        public static final String EXEC_TIME = "exec_time";//NON-NLS
        /**
         * Day umber in the year (1-366) INTEGER
         */
        public static final String EXEC_DAY_OF_YEAR = "exec_day";//NON-NLS
        /**
         * Year (e.g. 2014) INTEGER
         */
        public static final String EXEC_YEAR = "exec_year";//NON-NLS
        /**
         * Status code, see below
         */
        public static final String EXEC_STATUS = "status";//NON-NLS

    }

    public enum Constraint {
        unconstraigned(false, false),
        arrival(true, false),
        leaving(false, true);

        private boolean leavingForbidden;
        private boolean arrivalForbidden;

        private Constraint(boolean leavingForbidden, boolean arrivalForbidden) {
            this.leavingForbidden = leavingForbidden;
            this.arrivalForbidden = arrivalForbidden;
        }

        static Constraint fromBooleans(boolean leavingPermitted, boolean arrivalPermitted) {
            return (leavingPermitted ? (arrivalPermitted ? unconstraigned : leaving) : arrival);
        }

        /**
         * True if this alarm can be triggered to punch a leaving (i.e. number of punches in the
         * day is odd)
         *
         * @return
         */
        public boolean isLeavingPermitted() {
            return !leavingForbidden;
        }

        /**
         * True if this alarm can be triggered to punch an arrival (i.e. number of punches in the
         * day is even
         *
         * @return
         */
        public boolean isArrivalPermitted() {
            return !arrivalForbidden;
        }

    }

    public enum Day {
        monday(1),//1
        tuesday(1 << 1),//2
        wednesday(1 << 2),//4
        thursday(1 << 3),//8
        friday(1 << 4),//16
        saturday(1 << 5),//32
        sunday(1 << 6);//64

        private final int f;

        private Day(int flag) {
            this.f = flag;
        }

        public static Day fromCalDay(int d) {
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

        public static int set(int code, Day d) {
            return code | d.f;
        }

        public static int unset(int code, Day d) {
            return code & ~(d.f);
        }

        public static boolean isSet(int code, Day d) {
            return (code & d.f) != 0;
        }

        public static int cat(Day[] firing_days) {
            int r = 0;
            for (AlarmContract.Day d : firing_days) {
                r |= d.f;
            }
            return r;
        }
    }



}
