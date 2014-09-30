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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.agatteclient.MainActivity;

public class NetworkChangeReceiver extends BroadcastReceiver {


    public NetworkChangeReceiver() {
        super();

    }

    /**
     * Return the current SSID
     *
     * @param context calling application context
     * @return the currently connected SSID, or "" if no wifi network is connected
     */
    public static String getCurrentSsid(Context context) {
        String ssid = "";
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && connectionInfo.getSSID().length() != 0) {
                ssid = connectionInfo.getSSID();
                // Remove quotes
                ssid = ssid.replaceAll("\"", "");
            } else {
                Log.w(MainActivity.LOG_TAG, String.format("connection info is empty"));
            }
        }
        return ssid;
    }

    /**
     * Return true if a wifi network is currently available
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) { //NON-NLS
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            NetworkInfo.State state = networkInfo.getState();
            if (state == NetworkInfo.State.CONNECTED) {
                String ssid = getCurrentSsid(context);
                NetworkChangeRegistry.getInstance(context).setCurrentWifiState(true, ssid);
            }

            if (state == NetworkInfo.State.DISCONNECTED) {
                NetworkChangeRegistry.getInstance(context).setCurrentWifiState(false, "");
            }

        } else {
            Log.w(MainActivity.LOG_TAG, String.format("NetworkChangeReceiver got unexpected action %s", action));//NON-NLS
        }
    }


}
