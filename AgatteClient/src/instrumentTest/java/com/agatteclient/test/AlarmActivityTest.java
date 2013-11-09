package com.agatteclient.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.agatteclient.alarm.AlarmActivity;

/**
 * Created by remi on 07/11/13.
 */
public class AlarmActivityTest extends ActivityInstrumentationTestCase2 {


    private Activity mActivity;


    public AlarmActivityTest() {
        super(AlarmActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        mActivity = getActivity();
    }


    public void testPreConditions() {
        assertNotNull(mActivity);
    }
}
