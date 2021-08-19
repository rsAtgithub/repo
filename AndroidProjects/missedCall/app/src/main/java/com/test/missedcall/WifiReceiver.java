package com.test.missedcall;

import static com.test.missedcall.MainActivity.wifiApnString;
import static com.test.missedcall.MainActivity.wifiMgr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiReceiver extends ConnectivityManager.NetworkCallback {

    final NetworkRequest networkRequest;
    private static ConnectivityManager.NetworkCallback ncallb;

    public WifiReceiver() {
        networkRequest = new NetworkRequest.Builder()
                /*.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)*/
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
    }

    public void enable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerNetworkCallback(networkRequest, this);
        ncallb = this;
        Log.i(MainActivity.debugTag, "WiFiEvent enabled");
    }

    public void disable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.unregisterNetworkCallback(ncallb);
        Log.i(MainActivity.debugTag, "WiFiEvent disabled");
    }

    // Likewise, you can have a disable method that simply calls ConnectivityManager.unregisterNetworkCallback(NetworkCallback) too.

    @Override
    public void onAvailable(Network network) {
        // Do what you need to do here
        WifiManager wifiManager = (WifiManager) MainActivity.appContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();

        Log.i(MainActivity.debugTag, " -- Wifi connected --- " + " SSID " + ssid );
        ssid = ssid.replaceAll("^\"|\"$", "");
        if (ssid.equals(wifiApnString)) {
            Log.i(MainActivity.debugTag, "Ringer mode NORMAL");
            MainActivity.am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }
}

