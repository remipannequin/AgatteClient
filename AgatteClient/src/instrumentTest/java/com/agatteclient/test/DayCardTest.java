package com.agatteclient.test;

import android.test.AndroidTestCase;

import com.agatteclient.DayCard;

import junit.framework.TestCase;

import java.util.Collection;
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
        assertTrue(instance.isEvent());
    }

    public void testAdd() throws Exception {
        DayCard instance = new DayCard(200, 2012);
        instance.addPunch("8:00");
        assertEquals(1, instance.getNumberOfPunches());
        Iterator<Date> c = instance.getPunches().iterator();
        assertTrue(c.hasNext());
        assertEquals("Wed Jul 18 08:00:00 GMT 2012", c.next().toString());
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

}
