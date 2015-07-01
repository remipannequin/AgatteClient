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
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.agatteclient.BuildConfig;
import com.agatteclient.MainActivity;
import com.agatteclient.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlarmArrayAdapter extends ArrayAdapter<PunchAlarmTime> {

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


    private final android.support.v4.app.FragmentManager fragment_manager;
    private final AlarmList alarms;
    private final Context context;
    //private final ListView list;
    private Set<Integer> expanded = new HashSet<Integer>();
    private LayoutInflater inflater;


    public AlarmArrayAdapter(Context context, AlarmList objects) {
        super(context, R.layout.view_alarm, objects);
        this.alarms = objects;
        this.context = context;
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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = newView(parent, position);
        } else {
            v = convertView;
        }
        bindView(v, position);
        return v;
    }


    /**
     * Create a new view for a list item.
     *
     * @param parent the view group parent
     * @return the view created
     */
    private View newView(ViewGroup parent, int position) {
        View view = getInflater(parent).inflate(R.layout.view_alarm, parent, false);
        if (BuildConfig.DEBUG && view == null)
            throw new RuntimeException("View should not be null");
        ViewHolder holder = new ViewHolder(view, position);
        //views.set(position, holder);
        view.setTag(holder);
        return view;
    }


    /**
     * Bind the view of this tiem to the actual data
     *
     * @param v the view of the item to bind
     * @param position the position of th item in the list
     */
    private void bindView(View v, int position) {
        final ViewHolder holder = (ViewHolder) v.getTag();
        final PunchAlarmTime alarm = getItem(position);
        /* get and binf enabled button */
        CompoundButton cb = holder.getEnabled();
        cb.setTag(position);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int p = (Integer) compoundButton.getTag();
                PunchAlarmTime a = alarms.get(p);
                if (a.isEnabled() != b) {
                    alarms.setEnabled(p, b);
                }
            }
        });
        cb.setChecked(alarm.isEnabled());

        /* get and bind day button */
        for (int i = 0; i < 7; i++) {
            ToggleButton button = holder.getToggleButton()[i];
            button.setTag(position);
            final PunchAlarmTime.Day cur_day = PunchAlarmTime.Day.values()[i];
            button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int p = (Integer) compoundButton.getTag();
                    PunchAlarmTime a = alarms.get(p);
                    if (a.isFireAt(cur_day) != b) {
                        alarms.setFireAt(p, cur_day, b);
                    }
                }
            });
        }
        for (int i = 0; i < 7; i++) {
            ToggleButton button = holder.getToggleButton()[i];
            final PunchAlarmTime.Day cur_day = PunchAlarmTime.Day.values()[i];
            button.setChecked(alarm.isFireAt(cur_day));
        }

        /* get and bind delete button */
        ImageButton del_button = holder.getDeleteButton();
        del_button.setTag(position);
        del_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int p = (Integer) v.getTag();
                final PunchAlarmTime a = alarms.get(p);
                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                adb.setTitle(context.getString(R.string.alarm_delete_confirm_question));
                adb.setMessage(String.format(context.getString(R.string.alarm_delete_confirm), new SimpleDateFormat("H:mm").format(a.getTime())));
                adb.setNegativeButton(context.getString(R.string.alarm_delete_confirm_cancel), null);
                adb.setPositiveButton(context.getString(R.string.alarm_delete_confirm_ok), new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alarms.remove(a);
                        /* also remove index from expanded list */
                        expanded.remove(p);
                        notifyDataSetChanged();
                    }
                });
                adb.show();
            }
        });

        /* Time textview */
        TextView tv = holder.getText();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        tv.setTag(position);
        tv.setText(df.format(alarm.getTime()));
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //display dialog to set alarm time
                if (BuildConfig.DEBUG && !(view instanceof TextView)) throw new RuntimeException();
                TimePickerFragment newFragment = new TimePickerFragment();
                int p = (Integer) view.getTag();
                newFragment.setAlarm(alarms, p);
                newFragment.setView((TextView) view);
                newFragment.show(fragment_manager, "timePicker");//NON-NLS

            }
        });
        /* set summary form selected days */
        TextView summary = holder.getSummary();

        List<Integer> active_days = new ArrayList<Integer>(7);
        for (int i = 0; i < 7; i++) {
            final PunchAlarmTime.Day cur_day = PunchAlarmTime.Day.values()[i];
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
        info.setTag(position);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = (Integer) v.getTag();
                expand(p);
            }
        });
        /* expand or collapse item */
        final ImageButton collapse_button = holder.getCollapse();
        collapse_button.setTag(position);
        collapse_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = (Integer) v.getTag();
                collapse(p);
            }
        });

        if (expanded.contains(position)) {
            holder.getInfoArea().setVisibility(View.GONE);
            holder.getExpandArea().setVisibility(View.VISIBLE);
        } else {
            holder.getInfoArea().setVisibility(View.VISIBLE);
            holder.getExpandArea().setVisibility(View.GONE);
        }

        //populate the spinner according to the alarm type...
        final Spinner type_spinner = holder.getAlarmTypeSpinner();
        type_spinner.setTag(position);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.alarm_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(adapter);
        //Select the right one from alarm type
        PunchAlarmTime.Type t = alarm.getType();
        type_spinner.setSelection(t.ordinal());
        //bind item selection
        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int p = (Integer) parent.getTag();
                PunchAlarmTime.Type new_type = PunchAlarmTime.Type.values()[position];
                alarms.setType(p, new_type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.w(MainActivity.LOG_TAG, "Nothing selected in type_spinner.");
            }
        });
    }


    private void expand(int p) {
        expanded.add(p);
        notifyDataSetChanged();
    }


    private void collapse(int p) {
        expanded.remove(p);
        notifyDataSetChanged();
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private AlarmList binder;
        private int position;
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

        public void setAlarm(AlarmList binder, int position) {
            this.binder = binder;
            this.position = position;
        }

        public void setView(TextView tv) {
            this.tv = tv;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            binder.setTime(position, hourOfDay, minute);
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            tv.setText(df.format(binder.get(position).getTime()));
        }
    }


    private class ViewHolder {
        final View row;
        final int position;

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

        public ViewHolder(View row, int position) {
            this.row = row;
            this.position = position;
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
}