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

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.agatteclient.MainActivity;
import com.agatteclient.R;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * This singleton record networf (SSID) changes to trigger punching at given times of the day
 * <p/>
 * <p/>
 * Created by remi on 25/04/14.
 */
public class NetworkChangeRegistry {
    private static NetworkChangeRegistry ourInstance;
    private final Set<String> authorized_ssid;
    private boolean wifi_connected = false;
    private Dictionary<Long, String> ssid_history;
    private String ssid_current;
    private OnChangeListener listener;

    private NetworkChangeRegistry(Context context) {
        authorized_ssid = new HashSet<String>();
        ssid_history = new Hashtable<Long, String>();
        Resources res = context.getResources();
        for (String ssid : res.getStringArray(R.array.default_auth_ssid)) {
            authorized_ssid.add(ssid);
        }
    }

    public static NetworkChangeRegistry getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new NetworkChangeRegistry(context.getApplicationContext());
        }
        return ourInstance;
    }

    /**
     * Force update (with the current network SSID)
     */
    public void update(Context ctx) {
        boolean c = NetworkChangeReceiver.isConnected(ctx);
        String ssid = NetworkChangeReceiver.getCurrentSsid(ctx);
        setCurrentWifiState(c, ssid);
    }

    /**
     * Set the current Network SSID. Record the ssid and date
     *
     * @param ssid The current SSID of the wifi network, or "" (empty string) if no wifi connection
     */
    public void setCurrentWifiState(boolean connected, String ssid) {
        wifi_connected = connected;
        if (wifi_connected) {
            Log.d(MainActivity.LOG_TAG, String.format("Wifi state change: connected to %s", ssid));//NON-NLS
        } else {
            Log.d(MainActivity.LOG_TAG, String.format("Wifi state change: disconnected"));//NON-NLS
        }
        long now = System.currentTimeMillis();
        //Check that value changed
        if (!ssid.equals(ssid_current)) {
            ssid_history.put(now, ssid);
            ssid_current = ssid;
            if (listener != null) {
                listener.onChange();
            }
            Log.d(MainActivity.LOG_TAG, String.format("Current SSID is %s", ssid));//NON-NLS
        }
    }

    public boolean addAuthorizedSsid(String object) {
        return authorized_ssid.add(object);
    }

    public boolean removeAuthorizedSsid(String object) {
        return authorized_ssid.remove(object);
    }

    public boolean containsAuthorizedSsid(String object) {
        return authorized_ssid.contains(object);
    }

    public void clearAuthorizedSsid() {
        authorized_ssid.clear();
    }

    public int sizeAuthorizedSsid() {
        return authorized_ssid.size();
    }

    public boolean isAuthorizedSsidEmpty() {
        return authorized_ssid.isEmpty();
    }

    public Iterator<String> iterator() {
        return authorized_ssid.iterator();
    }

    public boolean isOnAuthorizeNetwork() {
        return wifi_connected && authorized_ssid.contains(ssid_current);
    }

    public void setOnChangeListener(OnChangeListener listener) {
        this.listener = listener;
    }

    public interface OnChangeListener {
        void onChange();
    }

}
