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
import android.util.Pair;

import com.agatteclient.card.DayCard;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class DayCardTest extends AndroidTestCase {

    public void testCreate() throws Exception {
        DayCard instance = new DayCard();
        assertNotNull(instance);
        assertEquals(0, instance.getNumberOfPunches());
        assertNotNull(instance.getPunches());
        assertTrue(instance.isEven());
    }

    public void testAdd() throws Exception {
        DayCard instance = new DayCard(200, 2012);
        instance.addPunch("8:00");
        assertEquals(1, instance.getNumberOfPunches());
        Date[] c = instance.getPunches();
        assertEquals(1, c.length);
        Calendar cal = Calendar.getInstance();
        cal.setTime(c[0]);
        assertEquals(0, cal.get(Calendar.MILLISECOND));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(8, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(200, cal.get(Calendar.DAY_OF_YEAR));
        assertEquals(2012, cal.get(Calendar.YEAR));
        assertTrue(instance.isOdd());
    }

    public void testTotalTime() throws Exception {
        DayCard instance = new DayCard();
        instance.addPunch("8:16");
        instance.addPunch("12:27");
        instance.addPunch("16:37");
        assertEquals(3, instance.getNumberOfPunches());
        assertTrue(instance.isOdd());
        instance.addPunch("18:29");
        double expected = 4 + (11.0 / 60.0) + 1.0 + (52.0 / 60.0);
        assertEquals(expected, instance.getTotalTime());
    }

    public void testAdd2() throws Exception {
        DayCard instance = new DayCard(200, 2012);
        instance.addPunch("8:00");
        instance.addPunch("8:00");

        assertEquals(1, instance.getNumberOfPunches());
        Date[] c = instance.getPunches();
        assertEquals(1, c.length);
        Calendar cal = Calendar.getInstance();
        cal.setTime(c[0]);
        assertEquals(0, cal.get(Calendar.MILLISECOND));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(8, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(200, cal.get(Calendar.DAY_OF_YEAR));
        assertEquals(2012, cal.get(Calendar.YEAR));
        assertTrue(instance.isOdd());
    }

    public void testAddCollection() throws Exception {
        DayCard instance = new DayCard(200, 2012);
        String[] punches = new String[]{"09:02", "12:07", "13:23"};
        instance.addPunches(punches, false);
        Date[] c = instance.getPunches();
        assertEquals(3, c.length);
    }

    public void testCorrectedTotalTime1() throws Exception {
        DayCard instance = new DayCard(200, 2012);
        instance.addPunch("08:15");
        instance.addPunch("12:15");
        instance.addPunch("13:15");
        instance.addPunch("15:15");
        assertEquals(4, instance.getNumberOfPunches());
        Date[] c = instance.getPunches();
        assertEquals(4, c.length);
        assertTrue(instance.isEven());
        assertEquals(6d, instance.getTotalTime());
        assertEquals(6d, instance.getCorrectedTotalTime());
    }

    public void testCorrectedTotalTime2() throws Exception {
        DayCard instance = new DayCard(200, 2012);
        instance.addPunch("08:15");
        instance.addPunch("12:15");
        instance.addPunch("12:45");
        instance.addPunch("15:15");
        assertEquals(4, instance.getNumberOfPunches());
        Date[] c = instance.getPunches();
        assertEquals(4, c.length);
        assertTrue(instance.isEven());
        assertEquals(6.5d, instance.getTotalTime());
        assertEquals(6.25d, instance.getCorrectedTotalTime());
        Pair<Date, Date>[] cp = instance.getCorrectedPunches();
        assertEquals(1, cp.length);
    }

    public void testCorrectedTotalTime3() throws Exception {
        DayCard instance = new DayCard(200, 2012);
        instance.addPunch("08:00");
        instance.addPunch("12:00");

        instance.addPunch("12:15");
        instance.addPunch("12:45");

        instance.addPunch("13:00");
        instance.addPunch("13:30");

        instance.addPunch("13:45");
        instance.addPunch("15:45");
        assertEquals(8, instance.getNumberOfPunches());
        Date[] c = instance.getPunches();
        assertEquals(8, c.length);
        assertTrue(instance.isEven());
        assertEquals(7d, instance.getTotalTime());
        assertEquals(7d, instance.getCorrectedTotalTime());
    }

    public void testCorrectedTotalTime4() throws Exception {
        DayCard instance = new DayCard(200, 2012);
        instance.addPunch("08:00");
        instance.addPunch("12:00");

        instance.addPunch("12:15");
        instance.addPunch("12:45");

        instance.addPunch("13:00");
        instance.addPunch("13:30");

        instance.addPunch("13:39");
        instance.addPunch("15:45");
        assertEquals(8, instance.getNumberOfPunches());
        Date[] c = instance.getPunches();
        assertEquals(8, c.length);
        assertTrue(instance.isEven());
        assertEquals(7.1d, instance.getTotalTime());
        assertEquals(7d, instance.getCorrectedTotalTime());
    }

    public void testCorrectedTotalTime5() throws Exception {
        DayCard instance = new DayCard(200, 2012);
        instance.addPunch("08:00");
        instance.addPunch("16:00");
        assertEquals(2, instance.getNumberOfPunches());
        Date[] c = instance.getPunches();
        assertEquals(2, c.length);
        assertTrue(instance.isEven());
        assertEquals(8d, instance.getTotalTime());
        assertEquals(5.5d, instance.getCorrectedTotalTime());
    }

    public void testCorrectedTotalTime6() throws Exception {
        DayCard instance = new DayCard(200, 2012);
        instance.addPunch("09:10");
        instance.addPunch("12:10");
        instance.addPunch("12:43");
        instance.addPunch("15:43");
        assertEquals(4, instance.getNumberOfPunches());
        Date[] c = instance.getPunches();
        assertEquals(4, c.length);
        assertTrue(instance.isEven());
        assertEquals(6d, instance.getTotalTime());
        assertEquals(5.8d, instance.getCorrectedTotalTime());
    }

    public void testIsCurrentDay1() {
        DayCard instance = new DayCard(200, 2012);
        boolean b = instance.isCurrentDay();
        assertFalse(b);
    }

    public void testIsCurrentDay2() {
        DayCard instance = new DayCard();
        boolean b = instance.isCurrentDay();
        assertTrue(b);
    }

    public void testGetVirtualPunches() throws ParseException {
        DayCard instance = new DayCard(200, 2012);
        instance.addPunch("09:00", true);
        instance.addPunch("12:00", true);
        assertEquals(2, instance.getNumberOfVirtualPunches());
        assertEquals(0, instance.getNumberOfPunches());
        assertEquals(3d, instance.getTotalTime());
        assertEquals(3d, instance.getCorrectedTotalTime());
    }

    public void testGetVirtualPunches2() throws ParseException {
        DayCard instance = new DayCard(200, 2012);
        instance.addPunch("09:00", true);
        instance.addPunch("12:00", true);
        instance.addPunch("12:10");
        instance.addPunch("17:10");
        assertEquals(2, instance.getNumberOfVirtualPunches());
        assertEquals(2, instance.getNumberOfPunches());
        assertEquals(8d, instance.getTotalTime());
        assertEquals(8d, instance.getCorrectedTotalTime());
    }

    public void testTotalTimeWithVirtual1() throws ParseException {
        DayCard instance = new DayCard(200, 2012);
        instance.addPunch("14:00", true);
        instance.addPunch("18:00", true);
        instance.addPunch("8:30");
        assertTrue(instance.isOdd());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.DAY_OF_YEAR, 200);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        assertEquals(5d, instance.getTotalTime(cal.getTime()));
    }

    public void testApplyCorrection1() throws ParseException {
        DayCard instance = new DayCard(200, 2012);
        instance.addPunch("09:00");
        instance.addPunch("12:00");
        instance.addPunch("12:30");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.DAY_OF_YEAR, 200);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 45);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date now = cal.getTime();
        instance.applyCorrection(now);
        Pair<Date, Date>[] actual = instance.getCorrectedPunches();
        assertEquals(0, actual.length);
        assertEquals(3.25d, instance.getCorrectedTotalTime(now));
    }

    public void testApplyCorrection2() throws ParseException {
        DayCard instance = new DayCard(200, 2012);
        instance.addPunch("09:00");
        instance.addPunch("12:00");
        instance.addPunch("12:30");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.DAY_OF_YEAR, 200);
        cal.set(Calendar.HOUR_OF_DAY, 13);
        cal.set(Calendar.MINUTE, 45);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date now = cal.getTime();
        instance.applyCorrection(now);
        Pair<Date, Date>[] actual = instance.getCorrectedPunches();
        assertEquals(0, actual.length);
        assertEquals(4.25d, instance.getCorrectedTotalTime(now));
    }

    public void testApplyCorrection3() throws ParseException {
        DayCard instance = new DayCard(200, 2012);
        instance.addPunch("09:00");
        instance.addPunch("12:00");
        instance.addPunch("12:30");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.DAY_OF_YEAR, 200);
        cal.set(Calendar.HOUR_OF_DAY, 14);
        cal.set(Calendar.MINUTE, 45);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date now = cal.getTime();
        instance.applyCorrection(now);
        Pair<Date, Date>[] actual = instance.getCorrectedPunches();
        assertEquals(1, actual.length);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 30);
        assertEquals(cal.getTime(), actual[0].first);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 45);
        assertEquals(cal.getTime(), actual[0].second);
        assertEquals(5d, instance.getCorrectedTotalTime(now));
    }


}
