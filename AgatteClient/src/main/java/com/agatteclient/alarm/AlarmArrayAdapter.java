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

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.agatteclient.BuildConfig;
import com.agatteclient.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Rémi Pannequin on 06/11/13.
 */
public class AlarmArrayAdapter extends ArrayAdapter<PunchAlarmTime> {

    private static final int[] days = new int[]{R.id.toggleButton_monday,
            R.id.toggleButton_tuesday,
            R.id.toggleButton_wednesday,
            R.id.toggleButton_thursday,
            R.id.toggleButton_friday,
            R.id.toggleButton_saturday,
            R.id.toggleButton_sunday};
    private final android.support.v4.app.FragmentManager fragment_manager;
    private final AlarmBinder alarms;
    //Used for testing
    private LayoutInflater inflater;

    public AlarmArrayAdapter(Context context, AlarmBinder objects) {
        super(context, R.layout.view_alarm, objects);
        this.alarms = objects;
        Context ctx = getContext();
        if (BuildConfig.DEBUG && !(ctx instanceof FragmentActivity))
            throw new RuntimeException("ctx is not a FragmentActivity");
        fragment_manager = ((FragmentActivity) ctx).getSupportFragmentManager();
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

        final PunchAlarmTime alarm = getItem(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = getInflater(parent).inflate(R.layout.view_alarm, parent, false);
            assert convertView != null;
            holder = new ViewHolder(convertView);
            //views.set(position, holder);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

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

        holder.getEnabled().setChecked(alarm.isEnabled());

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
                PunchAlarmTime a = alarms.get(p);
                newFragment.setAlarm(alarms, p);
                newFragment.setView((TextView) view);
                newFragment.show(fragment_manager, "timePicker");

            }
        });
        return convertView;
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private AlarmBinder binder;
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

        public void setAlarm(AlarmBinder binder, int position) {
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



    /**
     * Created by Rémi Pannequin on 16/11/13.
     */
    private class ViewHolder {
        final View row;

        private TextView text = null;
        private ToggleButton[] day_button = null;
        private CompoundButton enabled = null;


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


    }
}
