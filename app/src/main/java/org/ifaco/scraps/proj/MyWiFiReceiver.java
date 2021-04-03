package org.ifaco.scraps.proj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;

import androidx.appcompat.app.AppCompatActivity;

class MyWiFiReceiver extends BroadcastReceiver {
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    AppCompatActivity activity;

    public MyWiFiReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, AppCompatActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;
        switch (intent.getAction()) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:// 0
                MyWiFi.stateHandler.obtainMessage(0,
                        intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1) ==
                                WifiP2pManager.WIFI_P2P_STATE_ENABLED)
                        .sendToTarget();
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:// 1
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:// 2
                // Respond to new connection or disconnections
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:// 3
                // Respond to this device's wifi state changing
                break;
        }
    }
}
