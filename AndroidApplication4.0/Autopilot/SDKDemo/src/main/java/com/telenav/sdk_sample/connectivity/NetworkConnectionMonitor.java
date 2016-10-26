package com.telenav.sdk_sample.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.telenav.sdk.network.NetworkConnectivityManager;

/**
 * Defines a connection monitor that receives information when network connection changes
 */
public class NetworkConnectionMonitor extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkConnectivityManager.getInstance().updateNetworkConnectivityStatus(NetworkUtils.isInternetAvailable(context));
    }
}