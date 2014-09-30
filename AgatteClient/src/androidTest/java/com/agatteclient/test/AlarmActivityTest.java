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
import android.test.ActivityInstrumentationTestCase2;
import android.test.RenamingDelegatingContext;
import android.test.TouchUtils;

import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.agatteclient.BuildConfig;
import com.agatteclient.R;
import com.agatteclient.alarm.AlarmActivity;
import com.agatteclient.alarm.AlarmActivity.AlarmCursorAdapter;
import com.agatteclient.alarm.AlarmRegistry;
import com.agatteclient.alarm.PunchAlarmTime;
import com.agatteclient.alarm.db.AlarmContract;
import com.agatteclient.alarm.db.AlarmDbHelper;

import java.util.List;

/**
 * Test the ScheduledAlarm View
 * <p/>
 * Created by Rémi Pannequin on 07/11/13.
 */
public class AlarmActivityTest extends ActivityInstrumentationTestCase2 {


    public static final long AID1 = 10000001l;
    public static final long AID2 = 10000002l;
    private int index;
    private AlarmActivity mActivity;
    private PunchAlarmTime[] alarms;
    //private RenamingDelegatingContext r_context;


    public AlarmActivityTest() {
        super(AlarmActivity.class);
    }

    private CompoundButton getEnableButton(ListView l, int i) {
        RelativeLayout row = (RelativeLayout) l.getChildAt(i+index);
        return (CompoundButton) row.getChildAt(1);
    }

    private ImageButton getExpandButton(ListView l, int i) {
        View row = l.getChildAt(i+index);
        return (ImageButton) row.findViewById(R.id.collapse);
    }

    private ToggleButton[] getDayButton(ListView l, int i) {
        View row = l.getChildAt(i+index);
        ToggleButton[] b = new ToggleButton[7];
        b[0] = (ToggleButton) row.findViewById(R.id.toggleButton_monday);
        b[1] = (ToggleButton) row.findViewById(R.id.toggleButton_tuesday);
        b[2] = (ToggleButton) row.findViewById(R.id.toggleButton_wednesday);
        b[3] = (ToggleButton) row.findViewById(R.id.toggleButton_thursday);
        b[4] = (ToggleButton) row.findViewById(R.id.toggleButton_friday);
        b[5] = (ToggleButton) row.findViewById(R.id.toggleButton_saturday);
        b[6] = (ToggleButton) row.findViewById(R.id.toggleButton_sunday);
        return b;
    }

    private ToggleButton getDayButton(ListView l, int i, int day) {
        View row = l.getChildAt(i+index);
        if (BuildConfig.DEBUG && row == null) throw new RuntimeException();
        switch (day) {
            case 0:
                return (ToggleButton) row.findViewById(R.id.toggleButton_monday);
            case 1:
                return (ToggleButton) row.findViewById(R.id.toggleButton_tuesday);
            case 2:
                return (ToggleButton) row.findViewById(R.id.toggleButton_wednesday);
            case 3:
                return (ToggleButton) row.findViewById(R.id.toggleButton_thursday);
            case 4:
                return (ToggleButton) row.findViewById(R.id.toggleButton_friday);
            case 5:
                return (ToggleButton) row.findViewById(R.id.toggleButton_saturday);
            case 6:
                return (ToggleButton) row.findViewById(R.id.toggleButton_sunday);
        }
        return null;
    }

    private TextView getText(ListView l, int i) {
        View row = l.getChildAt(i+index);
        return (TextView) row.findViewById(R.id.alarmTimeTextView);
    }

    private void updateAlarms() {
        Context ctx = getInstrumentation().getTargetContext();
        alarms[0] = AlarmRegistry.getInstance().getAlarm(ctx, AID1);
        alarms[1] = AlarmRegistry.getInstance().getAlarm(ctx, AID2);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //r_context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
        //Populate DB
        //AlarmDbHelper helper = new AlarmDbHelper(r_context);
        AlarmDbHelper helper = new AlarmDbHelper(getInstrumentation().getTargetContext());
        SQLiteDatabase db = helper.getWritableDatabase();

        List<PunchAlarmTime> old_alarms = AlarmRegistry.getInstance().getAlarms(getInstrumentation().getTargetContext());
        index = old_alarms.size();

        ContentValues v1 = new ContentValues(6);
        v1.put(AlarmContract.Alarm._ID, AID1);
        v1.put(AlarmContract.Alarm.HOUR, 8);
        v1.put(AlarmContract.Alarm.MINUTE, 0);
        v1.put(AlarmContract.Alarm.DAYS_OF_WEEK, 31);
        v1.put(AlarmContract.Alarm.ENABLED, 1);
        v1.put(AlarmContract.Alarm.CONSTRAINT, AlarmContract.Constraint.unconstraigned.ordinal());
        db.insert(AlarmContract.Alarm.TABLE_NAME, null, v1);

        ContentValues v2 = new ContentValues(6);
        v2.put(AlarmContract.Alarm._ID, AID2);
        v2.put(AlarmContract.Alarm.HOUR, 14);
        v2.put(AlarmContract.Alarm.MINUTE, 0);
        v2.put(AlarmContract.Alarm.DAYS_OF_WEEK, 5);
        v2.put(AlarmContract.Alarm.ENABLED, 0);
        v2.put(AlarmContract.Alarm.CONSTRAINT, AlarmContract.Constraint.unconstraigned.ordinal());
        db.insert(AlarmContract.Alarm.TABLE_NAME, null, v2);

        db.close();

        alarms = new PunchAlarmTime[2];
        updateAlarms();

        setActivityInitialTouchMode(false);
        mActivity = (AlarmActivity) getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        AlarmDbHelper helper = new AlarmDbHelper(getInstrumentation().getTargetContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        String where = AlarmContract.Alarm._ID + "=?";
        String[] whereArgs1 = {String.valueOf(AID1)};
        String[] whereArgs2 = {String.valueOf(AID2)};
        db.delete(AlarmContract.Alarm.TABLE_NAME,
                where, whereArgs1);
        db.delete(AlarmContract.Alarm.TABLE_NAME,
                where, whereArgs2);
        db.close();
        super.tearDown();
    }

    @MediumTest
    public void testPreConditions() throws Exception {
        assertNotNull(mActivity);
        ListView listView = (ListView) mActivity.findViewById(R.id.alarmListView);
        getInstrumentation().waitForIdleSync();
        AlarmActivity.AlarmCursorAdapter adapter = mActivity.getAdapter();
        assertNotNull(adapter);

        assertNotNull(alarms[0]);
        assertNotNull(alarms[1]);
        assertTrue(alarms[0].isEnabled());
        assertFalse(alarms[1].isEnabled());
        assertTrue(alarms[0].isFireAt(AlarmContract.Day.monday));
        assertFalse(alarms[1].isFireAt(AlarmContract.Day.tuesday));

    }

    @MediumTest
    public void testInitialDisplay() throws Exception {
        assertNotNull(mActivity);
        ListView listView = (ListView) mActivity.findViewById(R.id.alarmListView);
        getInstrumentation().waitForIdleSync();
        CursorAdapter adapter = (CursorAdapter) listView.getAdapter();
        updateAlarms();
        assertNotNull(adapter);
        assertNotNull(alarms[0]);
        assertNotNull(alarms[1]);
        assertTrue(alarms[0].isEnabled());
        assertTrue(getEnableButton(listView, 0).isChecked());
        assertFalse(alarms[1].isEnabled());
        assertFalse(getEnableButton(listView, 1).isChecked());
        assertEquals("08:00", getText(listView, 0).getText());
        assertEquals("14:00", getText(listView, 1).getText());

        assertTrue(getDayButton(listView, 0, 0).isChecked());
        assertTrue(getDayButton(listView, 0, 1).isChecked());
        assertTrue(getDayButton(listView, 0, 2).isChecked());
        assertTrue(getDayButton(listView, 0, 3).isChecked());
        assertTrue(getDayButton(listView, 0, 4).isChecked());
        assertFalse(getDayButton(listView, 0, 5).isChecked());
        assertFalse(getDayButton(listView, 0, 6).isChecked());

        assertTrue(getDayButton(listView, 1, 0).isChecked());
        assertFalse(getDayButton(listView, 1, 1).isChecked());
        assertTrue(getDayButton(listView, 1, 2).isChecked());
        assertFalse(getDayButton(listView, 1, 3).isChecked());
        assertFalse(getDayButton(listView, 1, 4).isChecked());
        assertFalse(getDayButton(listView, 1, 5).isChecked());
        assertFalse(getDayButton(listView, 1, 6).isChecked());

    }

    @MediumTest
    //@UiThreadTest
    public void testDayButton() throws Exception {
        ListView listView = (ListView) mActivity.findViewById(R.id.alarmListView);
        getInstrumentation().waitForIdleSync();
        AlarmCursorAdapter adapter = (AlarmCursorAdapter) listView.getAdapter();
        assertNotNull(adapter);
        assertNotNull(alarms[0]);
        assertNotNull(alarms[1]);
        assertTrue(alarms[0].isFireAt(AlarmContract.Day.monday));
        assertFalse(alarms[1].isFireAt(AlarmContract.Day.tuesday));

        ToggleButton[] list_button = getDayButton(listView, 0);
        assertNotNull(list_button);
        ToggleButton b_monday = list_button[0];
        list_button = getDayButton(listView, 1);
        assertNotNull(list_button);
        ToggleButton b_tuesday = list_button[1];
        ImageButton expand_b0 = getExpandButton(listView, 0);
        TouchUtils.clickView(this, expand_b0);
        //listView.invalidate();
        Thread.sleep(1000);
        TouchUtils.clickView(this, b_monday);
        Thread.sleep(1000);
        updateAlarms();
        assertFalse(alarms[0].isFireAt(AlarmContract.Day.monday));

        TouchUtils.clickView(this, b_monday);
        updateAlarms();
        assertTrue(alarms[0].isFireAt(AlarmContract.Day.monday));

        TouchUtils.clickView(this, b_tuesday);
        updateAlarms();
        assertTrue(alarms[1].isFireAt(AlarmContract.Day.tuesday));

        TouchUtils.clickView(this, b_tuesday);
        updateAlarms();
        assertFalse(alarms[1].isFireAt(AlarmContract.Day.tuesday));

    }

    @MediumTest
    public void testEnableButton() throws Exception {
        ListView listView = (ListView) mActivity.findViewById(R.id.alarmListView);
        Thread.sleep(1000);
        AlarmCursorAdapter adapter = (AlarmCursorAdapter) listView.getAdapter();
        assertNotNull(adapter);

        updateAlarms();
        assertNotNull(alarms[0]);
        assertNotNull(alarms[1]);
        assertTrue(alarms[0].isEnabled());
        assertFalse(alarms[1].isEnabled());

        CompoundButton button1 = getEnableButton(listView, 0);
        assertNotNull(button1);
        assertTrue(button1.isChecked());

        TouchUtils.clickView(this, button1);
        Thread.sleep(1000);
        updateAlarms();
        assertFalse(alarms[0].isEnabled());

        TouchUtils.clickView(this, button1);
        Thread.sleep(1000);
        updateAlarms();
        assertTrue(alarms[0].isEnabled());

    }
}