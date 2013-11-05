package com.agatteclient.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.agatteclient.DayCardView;
import com.agatteclient.MainActivity;
import com.agatteclient.R;

/**
 * Created by Rémi Pannequin on 04/11/13.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2 {


    private Activity mActivity;
    private DayCardView mDayCardView;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        mActivity = getActivity();
        mDayCardView = (DayCardView) mActivity.findViewById(R.id.day_card_view);
    }


    public void testPreConditions() {
        assertNotNull(mDayCardView);
    }


}
