package com.agatteclient.test;

import android.os.Bundle;
import android.test.AndroidTestCase;

import com.agatteclient.AgatteResponse;

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
        Collections.addAll(virtual_punches, new String[] {"06:00", "7:30"});
        AgatteResponse instance = new AgatteResponse(AgatteResponse.Code.PunchOK, punches, virtual_punches);
        Bundle b = instance.toBundle();
        assertNotNull(b);
        AgatteResponse unbundled = AgatteResponse.fromBundle(b);
        assertEquals(instance.getCode(), unbundled.getCode());
        assertEquals(instance.getPunches().length, unbundled.getPunches().length);
        assertEquals(instance.getVirtualPunches().length, unbundled.getVirtualPunches().length);
    }


}
