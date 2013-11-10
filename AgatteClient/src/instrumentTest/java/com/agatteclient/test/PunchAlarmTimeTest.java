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

import com.agatteclient.alarm.PunchAlarmTime;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rémi Pannequin on 01/11/13.
 */
public class PunchAlarmTimeTest extends AndroidTestCase {

    public void testFireAt() throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(6,30, PunchAlarmTime.Day.monday);
        assertTrue(instance.isFireAt(PunchAlarmTime.Day.monday));
        assertFalse(instance.isFireAt(PunchAlarmTime.Day.tuesday));
        assertFalse(instance.isFireAt(PunchAlarmTime.Day.wednesday));
        assertFalse(instance.isFireAt(PunchAlarmTime.Day.thursday));
        assertFalse(instance.isFireAt(PunchAlarmTime.Day.friday));
        assertFalse(instance.isFireAt(PunchAlarmTime.Day.saturday));
        assertFalse(instance.isFireAt(PunchAlarmTime.Day.sunday));
    }

    public void testFireAt2() throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(10,25);
        assertTrue(instance.isFireAt(PunchAlarmTime.Day.monday));
        assertTrue(instance.isFireAt(PunchAlarmTime.Day.tuesday));
        assertTrue(instance.isFireAt(PunchAlarmTime.Day.wednesday));
        assertTrue(instance.isFireAt(PunchAlarmTime.Day.thursday));
        assertTrue(instance.isFireAt(PunchAlarmTime.Day.friday));
        assertFalse(instance.isFireAt(PunchAlarmTime.Day.saturday));
        assertFalse(instance.isFireAt(PunchAlarmTime.Day.sunday));
    }

    public void testFireAt3() throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(18,39, PunchAlarmTime.Day.monday, PunchAlarmTime.Day.wednesday, PunchAlarmTime.Day.friday, PunchAlarmTime.Day.sunday);
        assertTrue(instance.isFireAt(PunchAlarmTime.Day.monday));
        assertFalse(instance.isFireAt(PunchAlarmTime.Day.tuesday));
        assertTrue(instance.isFireAt(PunchAlarmTime.Day.wednesday));
        assertFalse(instance.isFireAt(PunchAlarmTime.Day.thursday));
        assertTrue(instance.isFireAt(PunchAlarmTime.Day.friday));
        assertFalse(instance.isFireAt(PunchAlarmTime.Day.saturday));
        assertTrue(instance.isFireAt(PunchAlarmTime.Day.sunday));
    }

    public void testSetFireAt()  throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(18,39);

        for (int i = 0; i < 5; i++) {
            PunchAlarmTime.Day day = PunchAlarmTime.Day.values()[i];
            assertTrue(instance.isFireAt(day));
            instance.setFireAt(day, true);
            assertTrue(instance.isFireAt(day));
            instance.setFireAt(day, false);
            assertFalse(instance.isFireAt(day));
        }

        for (int i = 5; i < 7; i++) {
            PunchAlarmTime.Day day = PunchAlarmTime.Day.values()[i];
            assertFalse(instance.isFireAt(day));
            instance.setFireAt(day, false);
            assertFalse(instance.isFireAt(day));
            instance.setFireAt(day, true);
            assertTrue(instance.isFireAt(day));
        }
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

    public void testToLong()  throws Exception {
        PunchAlarmTime instance = new PunchAlarmTime(10,25);
        long actual = instance.toLong();
        long expected = 281608120697457l;
        assertEquals(expected, actual);
    }

    public void testFromLong()  throws Exception {
        PunchAlarmTime expected = new PunchAlarmTime(6,52, PunchAlarmTime.Day.monday, PunchAlarmTime.Day.wednesday, PunchAlarmTime.Day.friday, PunchAlarmTime.Day.sunday);
        expected.setEnabled(false);
        PunchAlarmTime actual = PunchAlarmTime.fromLong(412l+365072220160l);
        assertTrue(expected.equals(actual));
        assertEquals(expected, actual);
    }

}
