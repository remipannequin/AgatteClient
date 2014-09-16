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

import android.provider.BaseColumns;

/**
 * Contract for the Alarm Database
 *
 * Created by Rémi Pannequin on 16/09/14.
 */
public class AlarmDbContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public AlarmDbContract() {}

    public enum AlarmType {
        ARRIVAL_ONLY,
        DEPARTURE_ONLY,
        ARRIVAL_OR_DEPARTURE
    }

    /**
     * A data kind representing a punch alarm
     */
    public static abstract class Alarm implements BaseColumns {
        public static final String TABLE_NAME = "alarm";//NON-NLS
        public static final String COLUMN_NAME_TIME = "time";//NON-NLS
        public static final String COLUMN_NAME_DAYS_MON = "days_mon";//NON-NLS
        public static final String COLUMN_NAME_DAYS_TUE = "days_tue";//NON-NLS
        public static final String COLUMN_NAME_DAYS_WED = "days_wed";//NON-NLS
        public static final String COLUMN_NAME_DAYS_THU = "days_thu";//NON-NLS
        public static final String COLUMN_NAME_DAYS_FRI = "days_wed";//NON-NLS
        public static final String COLUMN_NAME_DAYS_SAT = "days_sat";////NON-NLSNON-NLS
        public static final String COLUMN_NAME_DAYS_SUN = "days_sun";//NON-NLS
        public static final String COLUMN_NAME_TYPE = "type";//NON-NLS
    }

    /**
     * A data kind representing a scheduled alarm
     */
    public static abstract class ScheduledAlarm implements BaseColumns {
        public static final String TABLE_NAME = "scheduled_alarm";//NON-NLS
        public static final String COLUMN_NAME_ALARM_ID = "alarm_id";//NON-NLS
        public static final String COLUMN_NAME_SCHEDULE_ID = "schedule_id";//NON-NLS
    }

    public enum ExecStatus {
        SUCCESS,
        FAILURE,
        INVALID
    }

    /**
     * A data kind representing a alarm that have been executed (successfully or not)
     */
     public static abstract class PastAlarm implements BaseColumns {
        public static final String TABLE_NAME = "past_alarm";//NON-NLS
        public static final String COLUMN_NAME_ALARM_ID = "alarm_id";//NON-NLS
        public static final String COLUMN_NAME_EXEC_TIME = "exec_time";//NON-NLS
        public static final String COLUMN_NAME_EXEC_STATUS = "status";//NON-NLS

    }




}
