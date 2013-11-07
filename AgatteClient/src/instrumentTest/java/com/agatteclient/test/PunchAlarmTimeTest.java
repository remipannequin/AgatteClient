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

package com.agatteclient.test;

import android.test.AndroidTestCase;

import com.agatteclient.PunchAlarmTime;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rémi Pannequin on 01/11/13.
 */
public class PunchAlarmTimeTest extends AndroidTestCase {

    public void testFireAt() throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(6,30, PunchAlarmTime.Day.monday);
        assertTrue(instance.fireAt(PunchAlarmTime.Day.monday));
        assertFalse(instance.fireAt(PunchAlarmTime.Day.tuesday));
        assertFalse(instance.fireAt(PunchAlarmTime.Day.wednesday));
        assertFalse(instance.fireAt(PunchAlarmTime.Day.thursday));
        assertFalse(instance.fireAt(PunchAlarmTime.Day.friday));
        assertFalse(instance.fireAt(PunchAlarmTime.Day.saturday));
        assertFalse(instance.fireAt(PunchAlarmTime.Day.sunday));
    }

    public void testFireAt2() throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(10,25);
        assertTrue(instance.fireAt(PunchAlarmTime.Day.monday));
        assertTrue(instance.fireAt(PunchAlarmTime.Day.tuesday));
        assertTrue(instance.fireAt(PunchAlarmTime.Day.wednesday));
        assertTrue(instance.fireAt(PunchAlarmTime.Day.thursday));
        assertTrue(instance.fireAt(PunchAlarmTime.Day.friday));
        assertFalse(instance.fireAt(PunchAlarmTime.Day.saturday));
        assertFalse(instance.fireAt(PunchAlarmTime.Day.sunday));
    }

    public void testFireAt3() throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(18,39, PunchAlarmTime.Day.monday, PunchAlarmTime.Day.wednesday, PunchAlarmTime.Day.friday, PunchAlarmTime.Day.sunday);
        assertTrue(instance.fireAt(PunchAlarmTime.Day.monday));
        assertFalse(instance.fireAt(PunchAlarmTime.Day.tuesday));
        assertTrue(instance.fireAt(PunchAlarmTime.Day.wednesday));
        assertFalse(instance.fireAt(PunchAlarmTime.Day.thursday));
        assertTrue(instance.fireAt(PunchAlarmTime.Day.friday));
        assertFalse(instance.fireAt(PunchAlarmTime.Day.saturday));
        assertTrue(instance.fireAt(PunchAlarmTime.Day.sunday));
    }

    public void testNextAlarm1() throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(10,25);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date actual = instance.nextAlarm(cal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 25);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date expected = cal.getTime();
        assertEquals(expected, actual);
    }

    public void testNextAlarm2() throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(10,25);
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
        PunchAlarmTime instance = new PunchAlarmTime(10,25);
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
