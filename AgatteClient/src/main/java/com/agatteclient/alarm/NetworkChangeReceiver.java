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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by remi on 22/10/13.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private final Set<String> authorized_ssid;

    public NetworkChangeReceiver() {
        super();
        authorized_ssid = new HashSet<String>();
        //TODO: get the list of authorized network from preference
        authorized_ssid.add("eduroam");

    }

    /**
     * Return the current SSID
     *
     * @param context
     * @return the currently connected SSID, or null if no wifi network is connected
     */
    private static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && connectionInfo.getSSID().length() != 0) {
                ssid = connectionInfo.getSSID();
                //TODO: remove quotes
                ssid = ssid.replaceAll("\"", "");
            }
        }
        return ssid;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {
            //TODO
        }

        String ssid = getCurrentSsid(context);
        if (authorized_ssid.contains(ssid)) {
            //TODO
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {
            //TODO
        }

        String ssid = getCurrentSsid(context);
        if (authorized_ssid.contains(ssid)) {
            //TODO
        }
    }


}
