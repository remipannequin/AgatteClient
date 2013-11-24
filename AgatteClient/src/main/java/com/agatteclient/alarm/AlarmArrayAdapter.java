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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.agatteclient.R;

import java.text.SimpleDateFormat;

/**
 * Created by Rémi Pannequin on 06/11/13.
 */
public class AlarmArrayAdapter extends ArrayAdapter<PunchAlarmTime> {

    private static int[] days = new int[]{R.id.toggleButton_monday,
            R.id.toggleButton_tuesday,
            R.id.toggleButton_wednesday,
            R.id.toggleButton_thursday,
            R.id.toggleButton_friday,
            R.id.toggleButton_saturday,
            R.id.toggleButton_sunday};
    private final AlarmBinder alarms;

    //Used for testing
    private LayoutInflater inflater;

    public AlarmArrayAdapter(Context context, AlarmBinder objects) {
        super(context, R.layout.view_alarm, objects);
        this.alarms = objects;

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

        for (int i=0; i < 7; i++) {
            ToggleButton button = holder.getToggleButton()[i];
            final PunchAlarmTime.Day cur_day = PunchAlarmTime.Day.values()[i];
            button.setChecked(alarm.isFireAt(cur_day));
        }

        holder.getEnabled().setChecked(alarm.isEnabled());

        TextView tv = holder.getText();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        tv.setText(df.format(alarm.getTime()));
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: display dialog to set alarm time
            }
        });
        return convertView;
    }


    /**
     * Created by Rémi Pannequin on 16/11/13.
     */
    private class ViewHolder {
        View row;

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
