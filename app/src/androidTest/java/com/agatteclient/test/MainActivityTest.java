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

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.agatteclient.MainActivity;
import com.agatteclient.R;
import com.agatteclient.card.DayCardView;

public class MainActivityTest extends ActivityInstrumentationTestCase2 {


    private DayCardView mDayCardView;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        Activity mActivity = getActivity();
        mDayCardView = (DayCardView) mActivity.findViewById(R.id.day_card_view);
    }


    public void testPreConditions() {
        assertNotNull(mDayCardView);
    }


}
