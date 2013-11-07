package com.agatteclient;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by remi on 22/10/13.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    Set<String> authorized_ssid;

    public NetworkChangeReceiver() {
        super();
        authorized_ssid = new HashSet<String>();
        //TODO: get the list of authorized network from preference
        authorized_ssid.add("eduroam");

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {

        }

        String ssid = getCurrentSsid(context);
        if (authorized_ssid.contains(ssid)) {

        }
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


}
