package com.android.smartlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Constants {
    public static final int TIMEOUT = 5000;

    private static final String DEVICE_ID_KEY = "deviceId";
    private static final String IP_ADDRESS_KEY = "ipAddress";
    private static final String PORT_KEY = "port";
    private static SharedPreferences mSharedPreferences;
    private static String IP_ADDRESS;
    private static String DEVICE_ID;
    private static int PORT;

    public static void init(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        DEVICE_ID = mSharedPreferences.getString(DEVICE_ID_KEY, "-1");
        IP_ADDRESS = mSharedPreferences.getString(IP_ADDRESS_KEY, "192.168.1.1");
        PORT = mSharedPreferences.getInt(PORT_KEY, 8000);
    }

    public static String getDeviceId() {
        return DEVICE_ID;
    }

    public static void setDeviceId(String deviceId) {
        DEVICE_ID = deviceId;
        mSharedPreferences.edit().putString(DEVICE_ID_KEY, deviceId).apply();
    }

    public static String getIPAdress() {
        return IP_ADDRESS;
    }

    public static void setIpAddress(String ip) {
        Constants.IP_ADDRESS = ip;
        mSharedPreferences.edit().putString(IP_ADDRESS_KEY, ip).apply();
    }

    public static int getPort() {
        return PORT;
    }

    public static void setPort(int port) {
        Constants.PORT = port;
        mSharedPreferences.edit().putInt(PORT_KEY, port).apply();
    }
}
