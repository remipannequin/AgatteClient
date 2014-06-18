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


import android.app.AlarmManager;
import android.content.Context;
import android.test.AndroidTestCase;

import com.agatteclient.alarm.AlarmList;
import com.agatteclient.alarm.AlarmRegistry;
import com.agatteclient.alarm.PunchAlarmTime;

import java.util.Calendar;
import java.util.Map;

public class AlarmRegistryTest extends AndroidTestCase {

    private Context ctx;
    private AlarmList binder;
    private long now;
    private long t1;
    private long t2;
    private long t3;
    private PunchAlarmTime a1;
    private PunchAlarmTime a2;
    private PunchAlarmTime a3;
    private AlarmManager am;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        AlarmRegistry.reset();
        ctx = getContext();
        am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        binder = AlarmList.getInstance(ctx);
        Calendar cal = Calendar.getInstance();
        now = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, 2);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        t1 = cal.getTimeInMillis();
        a1 = new PunchAlarmTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        binder.add(a1);
        cal.add(Calendar.HOUR_OF_DAY, 4);
        t2 = cal.getTimeInMillis();
        a2 = new PunchAlarmTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        binder.add(a2);
        cal.add(Calendar.HOUR_OF_DAY, 8);
        t3 = cal.getTimeInMillis();
        a3 = new PunchAlarmTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        binder.add(a3);
    }


    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        binder.clear();
        AlarmRegistry.reset();
    }


    public void testUpdate1() {
        assertEquals(3, binder.size());
        AlarmRegistry instance = AlarmRegistry.getInstance();
        instance.update(ctx);
        Map<PunchAlarmTime, AlarmRegistry.Alarm> pim = instance.getPending_intent_map();
        assertEquals(3, pim.size());
        assertTrue(pim.containsKey(a1));
        assertEquals(a1.toLong(), (long) pim.get(a1).finger_print);
        assertTrue(pim.containsKey(a2));
        assertEquals(a2.toLong(), (long) pim.get(a2).finger_print);
        assertTrue(pim.containsKey(a3));
        assertEquals(a3.toLong(), (long) pim.get(a3).finger_print);
        instance.cancelAll(ctx);
    }

    public void testUpdate2() {
        AlarmRegistry instance = AlarmRegistry.getInstance();
        a2.setEnabled(false);
        instance.update(ctx);
        Map<PunchAlarmTime, AlarmRegistry.Alarm> pim = instance.getPending_intent_map();
        assertEquals(2, pim.size());
        assertTrue(pim.containsKey(a1));
        assertEquals(a1.toLong(), (long) pim.get(a1).finger_print);
        assertTrue(pim.containsKey(a3));
        assertEquals(a3.toLong(), (long) pim.get(a3).finger_print);
        a2.setEnabled(true);
        instance.update(ctx);
        pim = instance.getPending_intent_map();
        assertEquals(3, pim.size());
        assertTrue(pim.containsKey(a1));
        assertEquals(a1.toLong(), (long) pim.get(a1).finger_print);
        assertTrue(pim.containsKey(a2));
        assertEquals(a2.toLong(), (long) pim.get(a2).finger_print);
        assertTrue(pim.containsKey(a3));
        assertEquals(a3.toLong(), (long) pim.get(a3).finger_print);
        instance.cancelAll(ctx);
    }

    public void testUpdate3() {
        AlarmRegistry instance = AlarmRegistry.getInstance();
        a1.setEnabled(false);
        a2.setEnabled(false);
        a3.setEnabled(false);
        instance.update(ctx);
        Map<PunchAlarmTime, AlarmRegistry.Alarm> pim = instance.getPending_intent_map();
        assertEquals(0, pim.size());
        instance.cancelAll(ctx);
    }


    public void testUpdate4() {

        AlarmRegistry instance = AlarmRegistry.getInstance();
        instance.update(ctx);
        Map<PunchAlarmTime, AlarmRegistry.Alarm> pim = instance.getPending_intent_map();

        // check times
        assertEquals(t1, instance.getTime(a1));
        assertEquals(t2, instance.getTime(a2));
        assertEquals(t3, instance.getTime(a3));

        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.HOUR_OF_DAY, 12);
        cal.add(Calendar.MINUTE, 12);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        long t2_bis = cal.getTimeInMillis();
        a2.setTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        instance.update(ctx);
        assertEquals(t1, instance.getTime(a1));
        assertEquals(t2_bis, instance.getTime(a2));
        assertEquals(t3, instance.getTime(a3));


        instance.cancelAll(ctx);
    }


}
