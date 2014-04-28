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

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.agatteclient.alarm.AlarmBinder;
import com.agatteclient.alarm.PunchAlarmTime;

import java.util.Calendar;
import java.util.Date;

public class AlarmBinderTest extends AndroidTestCase {


    @SmallTest
    public void testGetInstance() {
        Context ctx = getContext();
        AlarmBinder instance = AlarmBinder.getInstance(ctx);
        assertNotNull(instance);
        assertEquals(0, instance.size());
        assertFalse("empty instance contains random object", instance.contains(new PunchAlarmTime(0, 0)));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Context ctx = getContext();
        AlarmBinder instance = AlarmBinder.getInstance(ctx);
        instance.clear();
    }

    @SmallTest
    public void testAddAlarm() {
        Context ctx = getContext();
        AlarmBinder instance = AlarmBinder.getInstance(ctx);
        assertEquals(0, instance.size());
        PunchAlarmTime expected = new PunchAlarmTime(0, 0);
        instance.add(expected);
        assertEquals(1, instance.size());
        assertSame(expected, instance.get(0));
        assertTrue(instance.contains(expected));
        instance.remove(expected);
        assertEquals(0, instance.size());
    }

    @SmallTest
    public void testSetEnabled() {
        Context ctx = getContext();
        AlarmBinder instance = AlarmBinder.getInstance(ctx);
        assertEquals(0, instance.size());
        PunchAlarmTime expected = new PunchAlarmTime(0, 0);
        expected.setEnabled(true);
        instance.add(expected);
        assertEquals(true, instance.get(0).isEnabled());
        expected.setEnabled(false);
        assertEquals(false, instance.get(0).isEnabled());
        instance.setEnabled(0, true);
        assertEquals(true, instance.get(0).isEnabled());
        instance.setEnabled(0, true);
        assertEquals(true, instance.get(0).isEnabled());
        instance.setEnabled(0, false);
        assertEquals(false, instance.get(0).isEnabled());
    }

    @SmallTest
    public void testSetFireAt() {
        Context ctx = getContext();
        AlarmBinder instance = AlarmBinder.getInstance(ctx);
        assertEquals(0, instance.size());
        PunchAlarmTime expected = new PunchAlarmTime(10, 50, PunchAlarmTime.Day.monday);
        PunchAlarmTime expected2 = new PunchAlarmTime(10, 50, PunchAlarmTime.Day.wednesday);
        instance.add(expected);
        instance.add(expected2);
        assertEquals(true, instance.get(0).isFireAt(PunchAlarmTime.Day.monday));
        assertEquals(false, instance.get(0).isFireAt(PunchAlarmTime.Day.tuesday));
        assertEquals(false, instance.get(0).isFireAt(PunchAlarmTime.Day.wednesday));
        assertEquals(false, instance.get(0).isFireAt(PunchAlarmTime.Day.thursday));
        assertEquals(false, instance.get(1).isFireAt(PunchAlarmTime.Day.monday));
        assertEquals(false, instance.get(1).isFireAt(PunchAlarmTime.Day.tuesday));
        assertEquals(true, instance.get(1).isFireAt(PunchAlarmTime.Day.wednesday));
        assertEquals(false, instance.get(1).isFireAt(PunchAlarmTime.Day.thursday));

        instance.setFireAt(0, PunchAlarmTime.Day.friday, true);
        assertEquals(true, instance.get(0).isFireAt(PunchAlarmTime.Day.monday));
        assertEquals(true, instance.get(0).isFireAt(PunchAlarmTime.Day.friday));
        instance.setFireAt(0, PunchAlarmTime.Day.monday, false);
        assertEquals(false, instance.get(0).isFireAt(PunchAlarmTime.Day.monday));
        assertEquals(true, instance.get(0).isFireAt(PunchAlarmTime.Day.friday));

        instance.setFireAt(1, PunchAlarmTime.Day.tuesday, true);
        assertEquals(true, instance.get(1).isFireAt(PunchAlarmTime.Day.tuesday));
        assertEquals(true, instance.get(1).isFireAt(PunchAlarmTime.Day.wednesday));
        instance.setFireAt(1, PunchAlarmTime.Day.wednesday, false);
        assertEquals(false, instance.get(1).isFireAt(PunchAlarmTime.Day.wednesday));
        assertEquals(true, instance.get(1).isFireAt(PunchAlarmTime.Day.tuesday));
    }

    @SmallTest
    public void testSetTime() {
        Context ctx = getContext();
        AlarmBinder instance = AlarmBinder.getInstance(ctx);
        PunchAlarmTime a1 = new PunchAlarmTime(0, 0, PunchAlarmTime.Day.monday);
        PunchAlarmTime a2 = new PunchAlarmTime(0, 0, PunchAlarmTime.Day.monday);
        instance.add(a1);
        instance.add(a2);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 2);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 1);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date now = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 18);
        Date exp1 = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 18);
        cal.set(Calendar.MINUTE, 54);
        Date exp2 = cal.getTime();

        instance.setTime(0, 10, 18);
        assertEquals(exp1, a1.nextAlarm(now));
        instance.setTime(1, 18, 54);
        assertEquals(exp2, a2.nextAlarm(now));
    }
}
