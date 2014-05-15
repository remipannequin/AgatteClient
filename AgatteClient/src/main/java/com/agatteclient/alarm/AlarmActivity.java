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

package com.agatteclient.alarm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.agatteclient.R;

import java.text.SimpleDateFormat;

public class AlarmActivity extends FragmentActivity {


    private AlarmArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ListView lv = (ListView) findViewById(R.id.alarmListView);
        mAdapter = new AlarmArrayAdapter(this, AlarmBinder.getInstance(this));
        lv.setAdapter(mAdapter);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                //do your stuff here
                final PunchAlarmTime a = AlarmBinder.getInstance(AlarmActivity.this).get(position);
                AlertDialog.Builder adb = new AlertDialog.Builder(AlarmActivity.this);
                adb.setTitle(getString(R.string.alarm_delete_confirm_question));
                adb.setMessage(String.format(getString(R.string.alarm_delete_confirm), new SimpleDateFormat("H:mm").format(a.getTime())));
                adb.setNegativeButton(getString(R.string.alarm_delete_confirm_cancel), null);
                adb.setPositiveButton(getString(R.string.alarm_delete_confirm_ok), new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.remove(a);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                adb.show();
                return true;
            }
        });

    }

    public AlarmArrayAdapter getAdapter() {
        return mAdapter;
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
            case R.id.action_alarm_add:
                //add a new alarm
                PunchAlarmTime new_alarm = new PunchAlarmTime(12, 0);
                mAdapter.add(new_alarm);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
