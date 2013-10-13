package com.agatteclient.test;

import android.test.AndroidTestCase;

import com.agatteclient.DayCard;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by remi on 02/10/13.
 */
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
        assertEquals("Wed Jul 18 08:00:00 GMT 2012", c[0].toString());
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
        assertEquals("Wed Jul 18 08:00:00 GMT 2012", c[0].toString());
        assertTrue(instance.isOdd());
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
        Date[] cp = instance.getCorrectedPunches();
        assertEquals(4, cp.length);
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



}
