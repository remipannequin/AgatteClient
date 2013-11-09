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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rémi Pannequin on 06/11/13.
 */
public class AlarmArrayAdapter extends ArrayAdapter<PunchAlarmTime> {


    private final List<PunchAlarmTime> list;

    List<ToggleButton> day_button;
    private CompoundButton enabled;
    private TextView text;

    public AlarmArrayAdapter(Context context, List<PunchAlarmTime> objects) {
        super(context, R.layout.view_alarm, objects);
        this.list = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = null;
        PunchAlarmTime alarm = list.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.view_alarm, null);

        } else {
            itemView = convertView;
        }

        //Play with itemView
        day_button = new ArrayList<ToggleButton>(7);
        day_button.add((ToggleButton) itemView.findViewById(R.id.toggleButton_monday));
        day_button.add((ToggleButton) itemView.findViewById(R.id.toggleButton_tuesday));
        day_button.add((ToggleButton) itemView.findViewById(R.id.toggleButton_wednesday));
        day_button.add((ToggleButton) itemView.findViewById(R.id.toggleButton_thursday));
        day_button.add((ToggleButton) itemView.findViewById(R.id.toggleButton_friday));
        day_button.add((ToggleButton) itemView.findViewById(R.id.toggleButton_saturday));
        day_button.add((ToggleButton) itemView.findViewById(R.id.toggleButton_sunday));

        enabled = (CompoundButton)itemView.findViewById(R.id.alarmEnabledCheckBox);

        text = (TextView)itemView.findViewById(R.id.alarmTimeTextView);

        for (int i=0; i < 7; i++) {
            day_button.get(i).setChecked(alarm.fireAt(PunchAlarmTime.Day.values()[i]));
        }
        SimpleDateFormat df = new SimpleDateFormat("H:mm");
        text.setText(df.format(alarm.getTime()));

        //TODO: manage enabled



        return itemView;


    }
}
