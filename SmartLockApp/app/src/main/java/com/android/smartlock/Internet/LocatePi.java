package com.android.smartlock.Internet;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import com.android.smartlock.Constants;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class LocatePi extends AsyncTask<String, String, String> {
    Context context;
    AsyncTaskListener mAsyncTaskListenerListener;

    public LocatePi(AsyncTaskListener asyncTaskListenerListener, Context context) {
        this.context = context;
        this.mAsyncTaskListenerListener = asyncTaskListenerListener;
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d("LocatePi", "Starting search");
        WifiManager wifii = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo d = wifii.getDhcpInfo();
        ArrayList<String> ips = new ArrayList<String>();
        InetAddress host;
        try {
            host = InetAddress.getByName(intToIp(d.gateway));
            byte[] ip = host.getAddress();
            for (int i = 1; i <= 254; i++) {
                ip[3] = (byte) i;
                InetAddress address = InetAddress.getByAddress(ip);
                if (address.isReachable(100)) {
                    String ipString = address.toString().replace("/", "");
                    String response = new Internet(ipString, "ping", "true").getResult();
                    if (response.contains("pong")) {
                        publishProgress("1.0");
                        Log.d("LocatePi", "Found server at " + ipString);
                        return ipString;
                    }
                }
                publishProgress(((i + 1.0) / 255.0) + "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("LocatePi", "No results :(");
        return Constants.getIPAddress();
    }

    @Override
    protected void onPostExecute(String s) {
        Constants.setIpAddress(s);
        mAsyncTaskListenerListener.onAsyncTaskCompleted();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        mAsyncTaskListenerListener.onAsyncTaskProgressUpdate((int) (Double.parseDouble(values[0]) * 100.0));
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                ((i >> 24) & 0xFF);
    }
}
