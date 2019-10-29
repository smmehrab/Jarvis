package com.example.jarvis.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
class NetworkStatusFinder {
    public static String getConnectivityStatusString(Context context) {
        String status = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                status = "Connected to Wifi";
                return status;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                status = "Connected to Mobile Data";
                return status;
            }
        } else {
            status = "No Internet Access";
            return status;
        }
        return status;
    }
}