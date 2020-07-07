package com.example.eden;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

public class NetworkReceiver extends BroadcastReceiver {
    private ConnectionStateMonitor connectionStateMonitor;

    public NetworkReceiver(ConnectionStateMonitor connectionStateMonitor) {
        this.connectionStateMonitor = connectionStateMonitor;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.net.conn.CONNECTIVITY_CHANGE")) {
            connectionStateMonitor.updateConnection();
        }
    }
}