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

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.agatteclient.BuildConfig;
import com.agatteclient.R;
import com.agatteclient.alarm.AlarmActivity;
import com.agatteclient.alarm.AlarmArrayAdapter;
import com.agatteclient.alarm.AlarmBinder;
import com.agatteclient.alarm.PunchAlarmTime;

/**
 * Test the Alarm View
 * <p/>
 * Created by Rémi Pannequin on 07/11/13.
 */
public class AlarmActivityTest extends ActivityInstrumentationTestCase2 {


    private AlarmActivity mActivity;
    private AlarmBinder alarms;


    public AlarmActivityTest() {
        super(AlarmActivity.class);
    }

    private static CompoundButton getEnableButton(ListView l, int i) {
        RelativeLayout row = (RelativeLayout) l.getChildAt(i);
        return (CompoundButton) row.getChildAt(1);
    }

    private static ToggleButton[] getDayButton(ListView l, int i) {
        View row = l.getChildAt(i);
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

    private static ToggleButton getDayButton(ListView l, int i, int day) {
        View row = l.getChildAt(i);
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

    private static TextView getText(ListView l, int i) {
        View row = l.getChildAt(i);
        return (TextView) row.findViewById(R.id.alarmTimeTextView);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        alarms = AlarmBinder.getInstance(getInstrumentation().getContext());
        if (alarms.size() == 0) {
            PunchAlarmTime a1 = new PunchAlarmTime(8, 0);
            alarms.addAlarm(a1);
            PunchAlarmTime a2 = new PunchAlarmTime(14, 0, PunchAlarmTime.Day.monday, PunchAlarmTime.Day.wednesday);
            alarms.addAlarm(a2);
        }
        alarms.get(0).setEnabled(true);
        alarms.get(1).setEnabled(false);
        setActivityInitialTouchMode(false);
        mActivity = (AlarmActivity) getActivity();
    }

    @MediumTest
    public void testPreConditions() throws Exception {
        assertNotNull(mActivity);
        ListView listView = (ListView) mActivity.findViewById(R.id.alarmListView);
        getInstrumentation().waitForIdleSync();
        AlarmArrayAdapter adapter = (AlarmArrayAdapter) listView.getAdapter();
        assertNotNull(adapter);
        PunchAlarmTime a1 = adapter.getItem(0);
        PunchAlarmTime a2 = adapter.getItem(1);
        assertTrue(a1.isEnabled());
        assertTrue(alarms.get(0).isEnabled());
        assertFalse(alarms.get(1).isEnabled());
        assertTrue(a1.isFireAt(PunchAlarmTime.Day.monday));
        assertTrue(alarms.get(0).isFireAt(PunchAlarmTime.Day.monday));
        assertFalse(a2.isFireAt(PunchAlarmTime.Day.tuesday));
        assertFalse(alarms.get(1).isFireAt(PunchAlarmTime.Day.tuesday));
        assertFalse(a2.isEnabled());
    }

    @MediumTest
    public void testInitialDisplay() throws Exception {
        assertNotNull(mActivity);
        ListView listView = (ListView) mActivity.findViewById(R.id.alarmListView);
        getInstrumentation().waitForIdleSync();
        AlarmArrayAdapter adapter = (AlarmArrayAdapter) listView.getAdapter();
        assertNotNull(adapter);
        assertTrue(alarms.get(0).isEnabled());
        assertTrue(getEnableButton(listView, 0).isChecked());
        assertFalse(alarms.get(1).isEnabled());
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
    @UiThreadTest
    public void testDayButton() throws Exception {
        ListView listView = (ListView) mActivity.findViewById(R.id.alarmListView);
        getInstrumentation().waitForIdleSync();
        AlarmArrayAdapter adapter = (AlarmArrayAdapter) listView.getAdapter();
        assertNotNull(adapter);
        PunchAlarmTime a1 = adapter.getItem(0);
        assertTrue(a1.isFireAt(PunchAlarmTime.Day.monday));
        assertTrue(alarms.get(0).isFireAt(PunchAlarmTime.Day.monday));
        PunchAlarmTime a2 = adapter.getItem(1);
        assertFalse(a2.isFireAt(PunchAlarmTime.Day.tuesday));
        assertFalse(alarms.get(1).isFireAt(PunchAlarmTime.Day.tuesday));

        ToggleButton[] list_button = getDayButton(listView, 0);
        assertNotNull(list_button);
        ToggleButton b_monday = list_button[0];
        TouchUtils.clickView(this, b_monday);
        assertFalse(a1.isFireAt(PunchAlarmTime.Day.monday));
        assertFalse(alarms.get(0).isFireAt(PunchAlarmTime.Day.monday));
        TouchUtils.clickView(this, b_monday);
        assertTrue(a1.isFireAt(PunchAlarmTime.Day.monday));
        assertTrue(alarms.get(0).isFireAt(PunchAlarmTime.Day.monday));

        list_button = getDayButton(listView, 1);
        assertNotNull(list_button);
        ToggleButton b_tuesday = list_button[1];
        TouchUtils.clickView(this, b_tuesday);
        assertTrue(a2.isFireAt(PunchAlarmTime.Day.tuesday));
        assertTrue(alarms.get(1).isFireAt(PunchAlarmTime.Day.tuesday));
        TouchUtils.clickView(this, b_tuesday);
        assertFalse(a2.isFireAt(PunchAlarmTime.Day.tuesday));
        assertFalse(alarms.get(1).isFireAt(PunchAlarmTime.Day.tuesday));
    }

    @MediumTest
    public void testEnableButton() throws Exception {
        ListView listView = (ListView) mActivity.findViewById(R.id.alarmListView);
        Thread.sleep(1000);
        AlarmArrayAdapter adapter = (AlarmArrayAdapter) listView.getAdapter();
        assertNotNull(adapter);
        PunchAlarmTime a1 = adapter.getItem(0);
        assertTrue(a1.isEnabled());
        assertTrue(alarms.get(0).isEnabled());
        PunchAlarmTime a2 = adapter.getItem(1);
        assertFalse(a2.isEnabled());
        assertFalse(alarms.get(1).isEnabled());

        CompoundButton button1 = getEnableButton(listView, 0);
        assertNotNull(button1);
        assertTrue(button1.isChecked());
        TouchUtils.clickView(this, button1);
        assertFalse(a1.isEnabled());
        assertFalse(alarms.get(0).isEnabled());
        TouchUtils.clickView(this, button1);
        assertTrue(a1.isEnabled());
        assertTrue(alarms.get(0).isEnabled());

    }


}
