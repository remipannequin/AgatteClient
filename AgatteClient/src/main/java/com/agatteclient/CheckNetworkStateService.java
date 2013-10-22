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

/**
 * Created by remi on 22/10/13.
 */
public class CheckNetworkStateService extends Service {

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


            getCurrentSsid(context);


        }
    }

    private Receiver receiver;
    private boolean receiver_registered = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //intent is null
        if (!receiver_registered) {
            final IntentFilter filters = new IntentFilter();
            filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            filters.addAction("android.net.wifi.STATE_CHANGE");
            receiver = new Receiver();
            //ConnectivityAction ?
            super.registerReceiver(receiver, filters);
            receiver_registered = true;
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (receiver_registered) {
            super.unregisterReceiver(receiver);
            receiver_registered = false;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    /**
     * Return the current SSID
     *
     * @param context
     * @return
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
            }
        }
        return ssid;
    }


}
