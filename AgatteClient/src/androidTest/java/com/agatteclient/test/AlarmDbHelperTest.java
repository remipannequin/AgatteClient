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

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.agatteclient.alarm.db.AlarmContract;
import com.agatteclient.alarm.db.AlarmDbHelper;

/**
 * Created by Rémi Pannequin on 30/09/14.
 */
public class AlarmDbHelperTest extends AndroidTestCase {

    private Context context;

    public void setUp(){
        context = new RenamingDelegatingContext(getContext(), "test_");

    }

    public void testGetDb(){
        // Here i have my new database wich is not connected to the standard database of the App
        AlarmDbHelper instance = new AlarmDbHelper(context);
        SQLiteDatabase db = instance.getReadableDatabase();
        assertNotNull("returned DB is null", db);
        db.close();
    }

    public void testInsert() {
        AlarmDbHelper instance = new AlarmDbHelper(context);
        SQLiteDatabase db = instance.getWritableDatabase();
        long id = 1000001l;
        ContentValues v1 = new ContentValues(6);
        v1.put(AlarmContract.Alarm._ID, id);
        v1.put(AlarmContract.Alarm.HOUR, 8);
        v1.put(AlarmContract.Alarm.MINUTE, 0);
        v1.put(AlarmContract.Alarm.DAYS_OF_WEEK, 31);
        v1.put(AlarmContract.Alarm.ENABLED, 1);
        v1.put(AlarmContract.Alarm.CONSTRAINT, AlarmContract.Constraint.unconstraigned.ordinal());
        long n = db.insert(AlarmContract.Alarm.TABLE_NAME, null, v1);
        assertEquals("wrong inserted ID", n, id);

        db.delete(AlarmContract.Alarm.TABLE_NAME, null, null);
        db.close();
    }

    public void tearDown() throws Exception{
        super.tearDown();
    }
}