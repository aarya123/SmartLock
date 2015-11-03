package com.android.smartlock.Internet;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;

public class LocatePi extends AsyncTask<String, String, String> {
    Context context;

    public LocatePi(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d("LocatePi", "Starting search");
        WifiManager wifii = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo d = wifii.getDhcpInfo();
        String connections = "";
        InetAddress host;
        try {
            host = InetAddress.getByName(intToIp(d.gateway));
            byte[] ip = host.getAddress();
            for (int i = 1; i <= 254; i++) {
                ip[3] = (byte) i;
                InetAddress address = InetAddress.getByAddress(ip);
                if (address.isReachable(100)) {
                    connections += address + ",";
                }
            }
            connections = connections.replace("/", "");
            connections = connections.substring(0, connections.length() - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("LocatePi", connections);
        return connections;
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                ((i >> 24) & 0xFF);
    }
}
