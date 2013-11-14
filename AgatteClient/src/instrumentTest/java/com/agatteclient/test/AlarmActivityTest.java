package com.agatteclient.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.agatteclient.R;
import com.agatteclient.alarm.AlarmActivity;
import com.agatteclient.alarm.AlarmArrayAdapter;
import com.agatteclient.alarm.AlarmBinder;
import com.agatteclient.alarm.PunchAlarmTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rémi Pannequin on 07/11/13.
 */
public class AlarmActivityTest extends ActivityInstrumentationTestCase2 {


    private AlarmActivity mActivity;
    private AlarmBinder alarms;


    public AlarmActivityTest() {
        super(AlarmActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        mActivity = (AlarmActivity)getActivity();
        alarms = AlarmBinder.getInstance();
        alarms.addAlarm(new PunchAlarmTime(8, 0));
        alarms.addAlarm(new PunchAlarmTime(14, 0, PunchAlarmTime.Day.monday, PunchAlarmTime.Day.wednesday));
        mActivity.setAlarms(alarms.getList());
        
    }


    public void testPreConditions() throws Exception {
        assertNotNull(mActivity);
    }

    public void testDayButton() throws Exception {
        ListView listView = (ListView) mActivity.findViewById(R.id.alarmListView);
        PunchAlarmTime a1 = (PunchAlarmTime)listView.getAdapter().getItem(0);
        assertTrue(a1.isFireAt(PunchAlarmTime.Day.monday));
        assertTrue(alarms.getList().get(0).isFireAt(PunchAlarmTime.Day.monday));
        AlarmArrayAdapter adapter = (AlarmArrayAdapter) listView.getAdapter();
        List<ToggleButton> list_button = adapter.getDay_button(0);
        assertNotNull(list_button);
        ToggleButton b_monday = list_button.get(0);
        b_monday.setChecked(false);
        assertFalse(a1.isFireAt(PunchAlarmTime.Day.monday));
        assertFalse(alarms.getList().get(0).isFireAt(PunchAlarmTime.Day.monday));



    }

}
