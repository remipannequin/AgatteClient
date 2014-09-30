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
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.agatteclient.BuildConfig;
import com.agatteclient.MainActivity;
import com.agatteclient.R;
import com.agatteclient.alarm.db.AlarmContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlarmActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public final static int LOADER_ID = 0;
    private AlarmCursorAdapter mAdapter;


    private static final int[] days = new int[]{
            R.id.toggleButton_monday,
            R.id.toggleButton_tuesday,
            R.id.toggleButton_wednesday,
            R.id.toggleButton_thursday,
            R.id.toggleButton_friday,
            R.id.toggleButton_saturday,
            R.id.toggleButton_sunday};

    private static final int[] days_names = new int[]{
            R.string.short_monday,
            R.string.short_tuesday,
            R.string.short_wednesday,
            R.string.short_thursday,
            R.string.short_friday,
            R.string.short_saturday,
            R.string.short_sunday
    };

    private static final int[] days_names_long = new int[]{
            R.string.monday,
            R.string.tuesday,
            R.string.wednesday,
            R.string.thursday,
            R.string.friday,
            R.string.saturday,
            R.string.sunday
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ListView lv = (ListView) findViewById(R.id.alarmListView);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        mAdapter = new AlarmCursorAdapter(this, null);
        lv.setAdapter(mAdapter);
        /*
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                //do your stuff here
                final PunchAlarmTime a = Alarms.getInstance(AlarmActivity.this).get(position);
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
        */
    }

    public AlarmCursorAdapter getAdapter() {
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
                AlarmRegistry.getInstance().addAlarm(getApplicationContext(), 12, 0);
                reload_cursor();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        final Context ctx = this;


        return new AsyncTaskLoader<Cursor>(this) {
            private Cursor mCursor;

            @Override
            public Cursor loadInBackground() {
                mCursor = AlarmRegistry.getInstance().getAlarmsCursor(ctx);
                return mCursor;
            }

            @Override
            public void deliverResult(Cursor cursor) {
                if (isReset()) {
                    // An async query came in while the loader is stopped
                    if (cursor != null) {
                        cursor.close();
                    }
                    return;
                }
                Cursor oldCursor = mCursor;
                mCursor = cursor;

                if (isStarted()) {
                    super.deliverResult(cursor);
                }

                if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
                    oldCursor.close();
                }
            }

            @Override
            protected void onStartLoading() {
                if (mCursor != null) {
                    deliverResult(mCursor);
                }
                if (takeContentChanged() || mCursor == null) {
                    forceLoad();
                }
            }

            /**
             * Must be called from the UI thread
             */
            @Override
            protected void onStopLoading() {
                // Attempt to cancel the current load task if possible.
                cancelLoad();
            }

            @Override
            public void onCanceled(Cursor cursor) {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

            @Override
            protected void onReset() {
                super.onReset();

                // Ensure the loader is stopped
                onStopLoading();

                if (mCursor != null && !mCursor.isClosed()) {
                    mCursor.close();
                }
                mCursor = null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    private void reload_cursor() {
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }


    private static void reload_cursor(AlarmActivity parent) {
        parent.getSupportLoaderManager().restartLoader(LOADER_ID, null, parent);
    }


    public class AlarmCursorAdapter extends CursorAdapter {

        //private final ListView list;
        private Set<Long> expanded = new HashSet<Long>();
        private LayoutInflater inflater;


        public AlarmCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);//NO FLAGS
        }


        private LayoutInflater getInflater(ViewGroup parent) {
            if (inflater == null) {
                assert parent != null;
                assert parent.getContext() != null;
                inflater = (LayoutInflater) parent.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            return inflater;
        }

        /**
         * Create a new view for a list item.
         *
         * @param parent  parent view
         * @param cursor  the cursor to display
         * @param context context
         * @return the bound view
         */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = getInflater(parent).inflate(R.layout.view_alarm, parent, false);
            int id = cursor.getInt(0);
            ViewHolder holder = new ViewHolder(view);
            view.setTag(holder);

            return view;
        }


        /**
         * Bind the view of this item to the actual data
         *
         * @param v       the view to bind
         * @param cursor  data to display
         * @param context context
         */
        @Override
        public void bindView(View v, final Context context, final Cursor cursor) {
            //create a new PunchAlarmTime from cursor
            final PunchAlarmTime alarm = new PunchAlarmTime(cursor);
            final long id = alarm.getId();
            final ViewHolder holder = (ViewHolder) v.getTag();

            /* get and bind enabled button */
            CompoundButton cb = holder.getEnabled();
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (alarm.isEnabled() != b) {
                        AlarmRegistry.getInstance().setEnabled(context, id, b);
                        reload_cursor();
                    }
                }
            });
            cb.setChecked(alarm.isEnabled());

            /* get and bind day button */
            for (int i = 0; i < 7; i++) {
                ToggleButton button = holder.getToggleButton()[i];
                final AlarmContract.Day cur_day = AlarmContract.Day.values()[i];
                button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (alarm.isFireAt(cur_day) != b) {
                            AlarmRegistry.getInstance().setDayOfWeek(context, id, cur_day, b);
                            reload_cursor();
                        }
                    }
                });
                button.setChecked(alarm.isFireAt(cur_day));
            }

            /* get and bind delete button */
            ImageButton del_button = holder.getDeleteButton();
            del_button.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(context);
                    adb.setTitle(context.getString(R.string.alarm_delete_confirm_question));
                    adb.setMessage(String.format(context.getString(R.string.alarm_delete_confirm), new SimpleDateFormat("H:mm").format(alarm.getTime())));
                    adb.setNegativeButton(context.getString(R.string.alarm_delete_confirm_cancel), null);
                    adb.setPositiveButton(context.getString(R.string.alarm_delete_confirm_ok), new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        /* also remove index from expanded list */
                            expanded.remove(alarm.getId());
                            AlarmRegistry.getInstance().remove(context, alarm.getId());
                            reload_cursor();
                        }
                    });
                    adb.show();
                }
            });

            /* Time textview */
            TextView tv = holder.getText();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            tv.setText(df.format(alarm.getTime()));
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //display dialog to set alarm time
                    if (BuildConfig.DEBUG && !(view instanceof TextView))
                        throw new RuntimeException();
                    TimePickerFragment time_picker = new TimePickerFragment();
                    time_picker.setAlarm(alarm);
                    time_picker.setView((TextView) view);
                    time_picker.setContext(context);
                    time_picker.show(getSupportFragmentManager(), "timePicker");//NON-NLS
                }
            });

            /* set summary form selected days */
            TextView summary = (TextView) v.findViewById(R.id.alarmSummary);
            List<Integer> active_days = new ArrayList<Integer>(7);
            for (int i = 0; i < 7; i++) {
                final AlarmContract.Day cur_day = AlarmContract.Day.values()[i];
                if (alarm.isFireAt(cur_day)) {
                    active_days.add(i);
                }
            }
            if (active_days.size() > 1) {
                StringBuilder days_summary = new StringBuilder();
                boolean first = true;
                for (int i : active_days) {
                    if (!first) {
                        days_summary.append(", ");
                    } else {
                        first = false;
                    }
                    days_summary.append(context.getText(days_names[i]));
                    summary.setText(days_summary.toString());
                }
            } else if (active_days.size() == 1) {
                summary.setText(context.getText(days_names_long[active_days.get(0)]));
            }

            /* bind view to collapse/expand */
            final View info = holder.getInfoArea();
            final View expand = holder.getExpandArea();
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expanded.add(id);
                    info.setVisibility(View.GONE);
                    expand.setVisibility(View.VISIBLE);
                }
            });

            /* expand or collapse item */
            final ImageButton collapse_button = holder.getCollapse();
            collapse_button.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expanded.contains(id)) {
                        expanded.remove(id);
                        info.setVisibility(View.VISIBLE);
                        expand.setVisibility(View.GONE);
                    } else {
                        expanded.add(id);
                        info.setVisibility(View.GONE);
                        expand.setVisibility(View.VISIBLE);
                    }
                }
            });

            if (expanded.contains(id)) {
                info.setVisibility(View.GONE);
                expand.setVisibility(View.VISIBLE);
            } else {
                info.setVisibility(View.VISIBLE);
                expand.setVisibility(View.GONE);
            }

            //populate the spinner according to the alarm type...
            final Spinner type_spinner = holder.getAlarmTypeSpinner();
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                    R.array.alarm_type_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            type_spinner.setAdapter(adapter);
            //Select the right one from alarm type
            AlarmContract.Constraint t = alarm.getConstraint();
            type_spinner.setSelection(t.ordinal());
            //bind item selection
            type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long some_id) {

                    if (alarm.getConstraint().ordinal() != position) {
                        AlarmContract.Constraint new_contraint = AlarmContract.Constraint.values()[position];
                        AlarmRegistry.getInstance().setConstraint(context, id, new_contraint);
                        reload_cursor();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.wtf(MainActivity.LOG_TAG, "OnNothingSelected called");//NON-NLS
                }
            });
        }
    }


    private class ViewHolder {

        final View row;
        TextView text = null;
        ToggleButton[] day_button = null;
        CompoundButton enabled = null;
        View expandArea = null;
        View infoArea = null;
        ImageButton deleteButton = null;
        View alarmItem = null;
        private ImageButton collapse = null;
        private TextView summary;
        private Spinner alarmTypeSpinner;

        public ViewHolder(View row) {
            this.row = row;
        }

        public TextView getText() {
            if (this.text == null) {
                this.text = (TextView) row.findViewById(R.id.alarmTimeTextView);
            }
            return this.text;
        }

        public ToggleButton[] getToggleButton() {
            if (day_button == null) {
                day_button = new ToggleButton[7];
                for (int i = 0; i < 7; i++) {
                    ToggleButton item = (ToggleButton) row.findViewById(days[i]);
                    assert item != null;
                    day_button[i] = item;
                }
            }
            return day_button;
        }

        public CompoundButton getEnabled() {
            if (enabled == null) {
                enabled = (CompoundButton) row.findViewById(R.id.alarmEnabledCompoundButton);
            }
            return enabled;
        }

        public View getExpandArea() {
            if (this.expandArea == null) {
                this.expandArea = row.findViewById(R.id.expandArea);
            }
            return this.expandArea;
        }

        public View getInfoArea() {
            if (this.infoArea == null) {
                this.infoArea = row.findViewById(R.id.infoArea);
            }
            return this.infoArea;
        }

        public ImageButton getDeleteButton() {
            if (deleteButton == null) {
                deleteButton = (ImageButton) row.findViewById(R.id.deleteButton);
            }
            return deleteButton;
        }

        public View getAlarmItem() {
            if (alarmItem == null) {
                alarmItem = row.findViewById(R.id.alarmItem);
            }
            return alarmItem;
        }

        public ImageButton getCollapse() {
            if (collapse == null) {
                collapse = (ImageButton) row.findViewById(R.id.collapse);
            }
            return collapse;
        }

        public TextView getSummary() {
            if (summary == null) {
                summary = (TextView) row.findViewById(R.id.alarmSummary);
            }
            return summary;
        }

        public Spinner getAlarmTypeSpinner() {
            if (alarmTypeSpinner == null) {
                alarmTypeSpinner = (Spinner) row.findViewById(R.id.alarmTypeSpinner);
            }
            return alarmTypeSpinner;
        }
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private PunchAlarmTime alarm;
        private TextView tv;
        private Context context;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void setAlarm(PunchAlarmTime a) {
            this.alarm = a;
        }

        public void setView(TextView tv) {
            this.tv = tv;
        }

        public void setContext(Context ctx) {
            this.context = ctx;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            AlarmRegistry.getInstance().setTime(context, alarm.getId(), hourOfDay, minute);
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            tv.setText(df.format(alarm.getTime()));
            reload_cursor((AlarmActivity) context);
        }
    }


}
