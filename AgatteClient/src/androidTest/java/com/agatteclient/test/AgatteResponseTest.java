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

package com.agatteclient.test;

import android.os.Bundle;
import android.test.AndroidTestCase;

import com.agatteclient.agatte.AgatteResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by remi on 31/10/13.
 */
public class AgatteResponseTest extends AndroidTestCase {

    public void testBundle1() throws Exception {
        AgatteResponse instance = new AgatteResponse(AgatteResponse.Code.IOError);
        Bundle b = instance.toBundle();
        assertNotNull(b);
        AgatteResponse unbundled = AgatteResponse.fromBundle(b);
        assertEquals(instance.getCode(), unbundled.getCode());
    }

    public void testBundle2() throws Exception {
        Collection<String> punches = new ArrayList<String>();
        Collections.addAll(punches, new String[]{"08:00", "10:30", "12:00", "14:00"});
        Collection<String> virtual_punches = new ArrayList<String>(4);
        Collections.addAll(virtual_punches, new String[]{"06:00", "7:30"});
        AgatteResponse instance = new AgatteResponse(AgatteResponse.Code.PunchOK, punches, virtual_punches);
        Bundle b = instance.toBundle();
        assertNotNull(b);
        AgatteResponse unbundled = AgatteResponse.fromBundle(b);
        assertEquals(instance.getCode(), unbundled.getCode());
        assertEquals(instance.getPunches().length, unbundled.getPunches().length);
        assertEquals(instance.getVirtualPunches().length, unbundled.getVirtualPunches().length);
    }


}
