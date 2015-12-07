package com.android.smartlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Constants {

    private static final String DEVICE_ID_KEY = "deviceId";
    private static final String IP_ADDRESS_KEY = "ipAddress";
    private static final String PORT_KEY = "port";
    private static final String TIMEOUT_KEY = "timeout";
    private static final String APPROVED_KEY = "approved";

    private static SharedPreferences mSharedPreferences;
    private static String IP_ADDRESS;
    private static String DEVICE_ID;
    private static int PORT;
    private static int TIMEOUT;
    private static boolean APPROVED;

    public static void init(Context context) {
        if (mSharedPreferences == null) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            DEVICE_ID = mSharedPreferences.getString(DEVICE_ID_KEY, "-1");
            IP_ADDRESS = mSharedPreferences.getString(IP_ADDRESS_KEY, "192.168.1.1");
            PORT = mSharedPreferences.getInt(PORT_KEY, 8000);
            TIMEOUT = mSharedPreferences.getInt(TIMEOUT_KEY, 5000);
            APPROVED = mSharedPreferences.getBoolean(APPROVED_KEY, false);
        }
    }


    public static String getDeviceId() {
        return DEVICE_ID;
    }

    public static void setDeviceId(String deviceId) {
        DEVICE_ID = deviceId;
        mSharedPreferences.edit().putString(DEVICE_ID_KEY, deviceId).apply();
    }

    public static String getIPAddress() {
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

    public static int getTimeout() {
        return TIMEOUT;
    }

    public static void setTimeout(int timeout) {
        Constants.TIMEOUT = timeout;
        mSharedPreferences.edit().putInt(TIMEOUT_KEY, timeout).apply();
    }

    public static boolean isApproved() {
        return APPROVED;
    }

    public static void setApproved(Boolean approved) {
        Constants.APPROVED = approved;
        mSharedPreferences.edit().putBoolean(APPROVED_KEY, approved).apply();
    }
}
