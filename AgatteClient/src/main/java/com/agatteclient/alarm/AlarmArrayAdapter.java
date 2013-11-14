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
import android.support.v7.internal.view.menu.MenuView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.agatteclient.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rémi Pannequin on 06/11/13.
 */
public class AlarmArrayAdapter extends ArrayAdapter<PunchAlarmTime> {


    private final List<PunchAlarmTime> list;

    Map<Integer, List<ToggleButton>> day_button;
    private CompoundButton enabled;
    private TextView text;

    public AlarmArrayAdapter(Context context, List<PunchAlarmTime> objects) {
        super(context, R.layout.view_alarm, objects);
        this.list = objects;
        this.day_button = new HashMap<Integer, List<ToggleButton>>(list.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        assert parent != null;
        View itemView;
        final PunchAlarmTime alarm = list.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.view_alarm, null);

        } else {
            itemView = convertView;
        }

        assert itemView != null;

        //Play with itemView
        ArrayList<ToggleButton> button = new ArrayList<ToggleButton>(7);
        for (int b: new int[] {R.id.toggleButton_monday,
                R.id.toggleButton_tuesday,
                R.id.toggleButton_wednesday,
                R.id.toggleButton_thursday,
                R.id.toggleButton_friday,
                R.id.toggleButton_saturday,
                R.id.toggleButton_sunday}) {
            ToggleButton item = (ToggleButton) itemView.findViewById(b);
            assert item != null;
            button.add(item);
        }
        day_button.put(position, button);

        enabled = (CompoundButton)itemView.findViewById(R.id.alarmEnabledCheckBox);

        text = (TextView)itemView.findViewById(R.id.alarmTimeTextView);

        for (int i=0; i < 7; i++) {
            final PunchAlarmTime.Day cur_day = PunchAlarmTime.Day.values()[i];
            button.get(i).setChecked(alarm.isFireAt(cur_day));
            button.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    alarm.setFireAt(cur_day, b);
                }
            });
        }
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        text.setText(df.format(alarm.getTime()));
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //TODO: manage enabled



        return itemView;


    }

    public List<ToggleButton> getDay_button(int position) {
        return day_button.get(position);
    }
}
