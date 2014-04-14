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

package com.agatteclient.agatte;

import android.os.Bundle;

import java.util.Collection;

/**
 * Created by Rémi Pannequin on 06/10/13.
 */
public class AgatteResponse {

    private String[] punches;
    private String[] virtual_punches;

    private AgatteResponse() {
    }

    public AgatteResponse(Collection<String> punches) {
        this.punches = punches.toArray(new String[punches.size()]);
    }

    public AgatteResponse(Collection<String> punches, Collection<String> virtual_punches) {
        this.virtual_punches = virtual_punches.toArray(new String[punches.size()]);
    }

    public String[] getPunches() {
        return punches;
    }

    public String[] getVirtualPunches() {
        return virtual_punches;
    }

    public boolean hasVirtualPunches() {
        return (virtual_punches != null && virtual_punches.length != 0);
    }

    public Bundle toBundle() {
        Bundle result = new Bundle();
        result.putStringArray("punches", this.punches);
        result.putStringArray("virtual_punches", this.virtual_punches);
        return result;
    }

    public static AgatteResponse fromBundle(Bundle bundle){
        AgatteResponse instance = new AgatteResponse();
        instance.punches = bundle.getStringArray("punches");
        instance.virtual_punches = bundle.getStringArray("virtual_punches");
        return instance;
    }

    public boolean hasPunches() {
        return (punches != null && punches.length != 0);
    }
}
