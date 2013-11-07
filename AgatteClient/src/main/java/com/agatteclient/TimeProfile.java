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

package com.agatteclient;

/**
 * Created by remi on 29/10/13.
 */
public enum TimeProfile {

    week_38h10(7.66666f),
    week_37h30(7.5f),
    week_36h40(7.33333f);
    public float daily_time;

    TimeProfile(float daily_time) {
        this.daily_time = daily_time;
    }
}
