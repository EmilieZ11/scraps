package org.ifaco.scraps.proj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.switchmaterial.SwitchMaterial;

import org.ifaco.scraps.Fun;
import org.ifaco.scraps.R;

public class MyWiFi extends AppCompatActivity {
    ConstraintLayout body;
    Toolbar toolbar;
    SwitchMaterial w1IsOn;

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;
    static Handler stateHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_wi_fi);

        body = findViewById(R.id.body);
        toolbar = findViewById(R.id.toolbar);
        w1IsOn = findViewById(R.id.w1IsOn);

        Fun.Companion.init(this);


        // Handlers
        stateHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 0:// WIFI_P2P_STATE_CHANGED_ACTION
                        if (!(msg.obj instanceof Boolean)) return;
                        w1IsOn.setChecked((boolean) msg.obj);
                        break;
                    case 1:// WIFI_P2P_PEERS_CHANGED_ACTION
                        break;
                    case 2:// WIFI_P2P_CONNECTION_CHANGED_ACTION
                        break;
                    case 3:// WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
                        break;
                    default:
                        if (msg.obj instanceof String)
                            Toast.makeText(Fun.c, (String) msg.obj, Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Initializations
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        if (manager != null)
            channel = manager.initialize(this, getMainLooper(), null);
        receiver = new MyWiFiReceiver(manager, channel, this);
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reasonCode) {
            }
        });

        // Intent Filters
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        w1IsOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}