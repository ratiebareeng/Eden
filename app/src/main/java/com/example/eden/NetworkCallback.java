package com.example.eden;

import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetworkCallback extends ConnectivityManager.NetworkCallback {
    private ConnectionStateMonitor connectionStateMonitor;

    public NetworkCallback(ConnectionStateMonitor connectionStateMonitor) {
        this.connectionStateMonitor = connectionStateMonitor;
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        //super.onAvailable(network);
        connectionStateMonitor.postValue(true);
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        connectionStateMonitor.postValue(false);
    }
}
