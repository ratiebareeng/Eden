package com.example.eden;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import java.util.Objects;

public class ConnectionStateMonitor extends LiveData<Boolean> {
    private static final String TAG = "ConnectionStateMonitor";

    private Context context;
    private ConnectivityManager.NetworkCallback networkCallback = null;
    private NetworkReceiver networkReceiver;
    private ConnectivityManager connectivityManager;
    private Network[] networks;

    // create constructor to init variables
    public ConnectionStateMonitor(Context context) {
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkCallback = new NetworkCallback(this);
        } /*else {
            networkReceiver = new NetworkReceiver();
        }*/
    }

    // override livedata methods
    @Override
    protected void onActive() {
        super.onActive();
        updateConnection();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .build();

            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
        } else {
            context.registerReceiver(networkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }
    }

    void updateConnection() {
        if (connectivityManager != null)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                networks = connectivityManager.getAllNetworks();
                isUserConnected();
            }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void isUserConnected() {
        if (networks.length > 0) {
            for (Network network : networks) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                assert networkCapabilities != null;
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    postValue(true);
            }
        } else {
            postValue(false);
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        } /*else {
            context.unregisterReceiver(networkReceiver);
        }*/
    }

    @Override
    protected void postValue(Boolean value) {
        super.postValue(value);
    }

}
