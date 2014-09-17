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
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.agatteclient.BuildConfig;
import com.agatteclient.R;
import com.agatteclient.alarm.db.AlarmContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlarmCursorAdapter extends CursorAdapter {

    private static final int[] days = new int[]{
            R.id.toggleButton_monday,
            R.id.toggleButton_tuesday,
            R.id.toggleButton_wednesday,
            R.id.toggleButton_thursday,
            R.id.toggleButton_friday,
            R.id.toggleButton_saturday,
            R.id.toggleButton_sunday};

    private static final int[] days_names = new int[] {
            R.string.short_monday,
            R.string.short_tuesday,
            R.string.short_wednesday,
            R.string.short_thursday,
            R.string.short_friday,
            R.string.short_saturday,
            R.string.short_sunday
    };

    private static final int[] days_names_long = new int[] {
            R.string.monday,
            R.string.tuesday,
            R.string.wednesday,
            R.string.thursday,
            R.string.friday,
            R.string.saturday,
            R.string.sunday
    };


    private final android.support.v4.app.FragmentManager fragment_manager;

    //private final ListView list;
    private Set<PunchAlarmTime> expanded = new HashSet<PunchAlarmTime>();
    private LayoutInflater inflater;


    public AlarmCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, true);
        if (BuildConfig.DEBUG && !(context instanceof FragmentActivity))
            throw new RuntimeException("ctx is not a FragmentActivity");
        fragment_manager = ((FragmentActivity) context).getSupportFragmentManager();
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
     * @param parent
     * @param cursor
     * @param context
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = getInflater(parent).inflate(R.layout.view_alarm, parent, false);

        //init ?


        return view;
    }


    /**
     * Bind the view of this tiem to the actual data
     *
     * @param v
     * @param cursor
     * @param context
     */
    @Override
    public void bindView(View v, final Context context, Cursor cursor) {
        //create a new PunchAlarmTime from cursor
        final PunchAlarmTime alarm = new PunchAlarmTime(cursor);





        /* get and bind enabled button */
        CompoundButton cb = (CompoundButton) v.findViewById(R.id.alarmEnabledCompoundButton);
        cb.setChecked(alarm.isEnabled());
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int p = (Integer) compoundButton.getTag();

                if (alarm.isEnabled() != b) {
                    alarm.setEnabled(b);
                }
            }
        });


        /* get and bind day button */
        for (int i = 0; i < 7; i++) {
            ToggleButton button = (ToggleButton) v.findViewById(days[i]);
            final AlarmContract.Day cur_day = AlarmContract.Day.values()[i];
            button.setChecked(alarm.isFireAt(cur_day));
            button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (alarm.isFireAt(cur_day) != b) {
                        alarm.setFireAt(cur_day, b);
                    }
                }
            });
        }


        /* get and bind delete button */
        ImageButton del_button = (ImageButton) v.findViewById(R.id.deleteButton);

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
                        expanded.remove(alarm);
                        AlarmRegistry.getInstance().remove(context, alarm.getId());
                        notifyDataSetChanged();
                    }
                });
                adb.show();
            }
        });

        /* Time textview */
        TextView tv = (TextView) v.findViewById(R.id.alarmTimeTextView);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        tv.setText(df.format(alarm.getTime()));
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //display dialog to set alarm time
                if (BuildConfig.DEBUG && !(view instanceof TextView)) throw new RuntimeException();
                TimePickerFragment time_picker = new TimePickerFragment();
                time_picker.setAlarm(alarm);
                time_picker.setView((TextView) view);
                time_picker.show(fragment_manager, "timePicker");

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
        final View info = v.findViewById(R.id.infoArea);
        final View expand = v.findViewById(R.id.expandArea);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expanded.add(alarm);
            }
        });
        /* expand or collapse item */
        final ImageButton collapse_button = (ImageButton) v.findViewById(R.id.collapse);
        collapse_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expanded.contains(alarm)) {
                    expanded.remove(alarm);
                } else {
                    expanded.add(alarm);
                }

            }
        });

        if (expanded.contains(alarm)) {
            info.setVisibility(View.GONE);
            expand.setVisibility(View.VISIBLE);
        } else {
            info.setVisibility(View.VISIBLE);
            expand.setVisibility(View.GONE);
        }

        //populate the spinner according to the alarm type...
        final Spinner type_spinner = (Spinner) v.findViewById(R.id.alarmTypeSpinner);

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
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AlarmContract.Constraint new_type = AlarmContract.Constraint.values()[position];
                alarm.setConstraint(new_type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //TODO ? is this possible ?? can't happen ?!
            }
        });
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private PunchAlarmTime alarm;
        private TextView tv;

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

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            alarm.setTime(hourOfDay, minute);
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            tv.setText(df.format(alarm.getTime()));
        }
    }
}
