package com.example.jarvis.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkStatusFinder.getConnectivityStatusString(context);
        if(status.isEmpty()) {
            status="No Internet Access";
        }
//        Toast toast= Toast.makeText(context, status, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
//        toast.show();
    }
}