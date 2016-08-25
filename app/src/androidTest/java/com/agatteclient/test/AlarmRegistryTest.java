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
import android.test.RenamingDelegatingContext;

import com.agatteclient.alarm.AlarmRegistry;
import com.agatteclient.alarm.PunchAlarmTime;

import java.util.Calendar;
import java.util.Map;

public class AlarmRegistryTest extends AndroidTestCase {

private Context context;

    public void setUp(){
        context = new RenamingDelegatingContext(getContext(), "test_");

    }

    public void testAddAlarm() {
        AlarmRegistry instance = AlarmRegistry.getInstance();
        int h = 12;
        int m = 36;
        long id = instance.addAlarm(context, h, m);
        assertTrue(id > 0);
        PunchAlarmTime a = instance.getAlarm(context, id);
        assertNotNull(a);
    }



}
