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

package com.agatteclient.test;

import android.test.AndroidTestCase;

import com.agatteclient.alarm.PunchAlarmTime;
import com.agatteclient.alarm.db.AlarmContract;

import java.util.Calendar;
import java.util.Date;

import static com.agatteclient.alarm.db.AlarmContract.Day.*;

public class PunchAlarmTimeTest extends AndroidTestCase {

    public void testFireAt() throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(6, 30, monday);
        assertTrue(instance.isFireAt(monday));
        assertFalse(instance.isFireAt(tuesday));
        assertFalse(instance.isFireAt(wednesday));
        assertFalse(instance.isFireAt(thursday));
        assertFalse(instance.isFireAt(friday));
        assertFalse(instance.isFireAt(saturday));
        assertFalse(instance.isFireAt(sunday));
    }

    public void testFireAt2() throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(10, 25);
        assertTrue(instance.isFireAt(monday));
        assertTrue(instance.isFireAt(tuesday));
        assertTrue(instance.isFireAt(wednesday));
        assertTrue(instance.isFireAt(thursday));
        assertTrue(instance.isFireAt(friday));
        assertFalse(instance.isFireAt(saturday));
        assertFalse(instance.isFireAt(sunday));
    }

    public void testFireAt3() throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(18, 39, monday, wednesday, friday, sunday);
        assertTrue(instance.isFireAt(monday));
        assertFalse(instance.isFireAt(tuesday));
        assertTrue(instance.isFireAt(wednesday));
        assertFalse(instance.isFireAt(thursday));
        assertTrue(instance.isFireAt(friday));
        assertFalse(instance.isFireAt(saturday));
        assertTrue(instance.isFireAt(sunday));
    }

    public void testSetFireAt() throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(18, 39);
        int dow;
        for (int i = 0; i < 5; i++) {
            dow = instance.getDaysOfWeek();
            AlarmContract.Day day = values()[i];
            assertTrue(instance.isFireAt(day));
            assertTrue(AlarmContract.Day.isSet(dow, day));
            dow = AlarmContract.Day.set(dow, day);
            assertTrue(AlarmContract.Day.isSet(dow, day));
            dow = AlarmContract.Day.unset(dow, day);
            assertFalse(AlarmContract.Day.isSet(dow, day));
        }

        for (int i = 5; i < 7; i++) {
            AlarmContract.Day day = values()[i];
            dow = instance.getDaysOfWeek();
            assertFalse(instance.isFireAt(day));
            assertFalse(AlarmContract.Day.isSet(dow, day));
            dow = AlarmContract.Day.set(dow, day);
            assertTrue(AlarmContract.Day.isSet(dow, day));
            dow = AlarmContract.Day.unset(dow, day);
            assertFalse(AlarmContract.Day.isSet(dow, day));
        }
    }

    public void testNextAlarm1() throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(10, 25);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date actual = instance.nextAlarm(cal.getTime());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 25);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expected = cal.getTime();
        assertEquals(expected, actual);
    }

    public void testNextAlarm2() throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(10, 25);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 11);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date actual = instance.nextAlarm(cal.getTime());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 25);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expected = cal.getTime();
        assertEquals(expected, actual);
    }


    public void testNextAlarm3() throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(10, 25);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        Date actual = instance.nextAlarm(cal.getTime());
        cal.add(Calendar.DATE, 2);
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 25);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expected = cal.getTime();
        assertEquals(expected, actual);
    }

}
