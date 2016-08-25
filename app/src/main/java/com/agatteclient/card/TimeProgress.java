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

package com.agatteclient.card;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.agatteclient.MainActivity;

/**
 * Custom Progress bar that use a TimeProgressDrawable.
 */
public class TimeProgress extends ProgressBar {

    public TimeProgress(Context context) {
        super(context);
        init_drawable(context);
    }

    public TimeProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init_drawable(context);
    }

    public TimeProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init_drawable(context);
    }

    private void init_drawable(Context context) {
        Resources r = context.getResources();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int profile_n;
        if (preferences != null) {
            String profile = preferences.getString(MainActivity.PROFILE_PREF, "1");
            profile_n = Integer.decode(profile) - 1;
        } else {
            profile_n = 0;
        }
        float day_goal = TimeProfile.values()[profile_n].daily_time;
        float scale = r.getDisplayMetrics().density;
        setProgressDrawable(new TimeProgressDrawable(1200, day_goal, 100, scale));
        setMax(1200);
    }
}