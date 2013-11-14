/*This file is part of AgatteClient.

    AgatteClient is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AgatteClient is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AgatteClient.  If not, see <http://www.gnu.org/licenses/>.*/

package com.agatteclient.alarm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.agatteclient.R;

import java.util.ArrayList;
import java.util.List;

public class AlarmActivity extends Activity {

    private AlarmArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ListView lv = (ListView) findViewById(R.id.alarmListView);
        lv.setAdapter(mAdapter);
    }

    public AlarmArrayAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(AlarmArrayAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public void setAlarms(final List<PunchAlarmTime> alarms) {
        final Context ctx = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter = new AlarmArrayAdapter(ctx, alarms);
                ListView lv = (ListView) findViewById(R.id.alarmListView);
                lv.setAdapter(mAdapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
