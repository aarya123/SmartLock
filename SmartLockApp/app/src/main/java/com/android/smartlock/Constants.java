package com.android.smartlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class Constants {

    private static final String DEVICE_ID_KEY = "deviceId";
    private static final String IP_ADDRESS_KEY = "ipAddress";
    private static final String PORT_KEY = "port";
    private static final String TIMEOUT_KEY = "timeout";
    private static final String APPROVED_KEY = "approved";
    private static final String PUBLIC_KEY_MODULUS_KEY = "pbkm";
    private static final String PRIVATE_KEY_MODULUS_KEY = "prkm";
    private static final String PUBLIC_KEY_EXPONENT_KEY = "pbke";
    private static final String PRIVATE_KEY_EXPONENT_KEY = "prke";

    private static SharedPreferences mSharedPreferences;
    private static String IP_ADDRESS;
    private static String DEVICE_ID;
    private static int PORT;
    private static int TIMEOUT;
    private static boolean APPROVED;
    private static PublicKey PUBLIC_KEY;
    private static PrivateKey PRIVATE_KEY;

    private static void initRSA() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp = kpg.genKeyPair();
            KeyFactory fact = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(), RSAPublicKeySpec.class);
            RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(), RSAPrivateKeySpec.class);
            BigInteger public_key_modulus = pub.getModulus();
            BigInteger private_key_modulus = priv.getModulus();
            BigInteger public_key_exponent = pub.getPublicExponent();
            BigInteger private_key_exponent = priv.getPrivateExponent();
            PUBLIC_KEY = kp.getPublic();
            PRIVATE_KEY = kp.getPrivate();
            mSharedPreferences.edit()
                    .putString(PUBLIC_KEY_MODULUS_KEY, public_key_modulus.toString())
                    .putString(PRIVATE_KEY_MODULUS_KEY, private_key_modulus.toString())
                    .putString(PUBLIC_KEY_EXPONENT_KEY, public_key_exponent.toString())
                    .putString(PRIVATE_KEY_EXPONENT_KEY, private_key_exponent.toString()).apply();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public static void init(Context context) {
        if (mSharedPreferences == null) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            DEVICE_ID = mSharedPreferences.getString(DEVICE_ID_KEY, "-1");
            IP_ADDRESS = mSharedPreferences.getString(IP_ADDRESS_KEY, "192.168.1.1");
            PORT = mSharedPreferences.getInt(PORT_KEY, 8000);
            TIMEOUT = mSharedPreferences.getInt(TIMEOUT_KEY, 5000);
            APPROVED = mSharedPreferences.getBoolean(APPROVED_KEY, false);
            BigInteger public_key_modulus = new BigInteger(mSharedPreferences.getString(PUBLIC_KEY_MODULUS_KEY, "-1"));
            BigInteger private_key_modulus = new BigInteger(mSharedPreferences.getString(PRIVATE_KEY_MODULUS_KEY, "-1"));
            BigInteger public_key_exponent = new BigInteger(mSharedPreferences.getString(PUBLIC_KEY_EXPONENT_KEY, "-1"));
            BigInteger private_key_exponent = new BigInteger(mSharedPreferences.getString(PRIVATE_KEY_EXPONENT_KEY, "-1"));
            if (public_key_modulus.equals(new BigInteger("-1"))) {
                initRSA();
            } else {
                try {
                    KeyFactory fact = KeyFactory.getInstance("RSA");
                    PUBLIC_KEY = fact.generatePublic(new RSAPublicKeySpec(public_key_modulus, public_key_exponent));
                    PRIVATE_KEY = fact.generatePrivate(new RSAPrivateKeySpec(private_key_modulus, private_key_exponent));
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static PublicKey getPublicKey() {
        return PUBLIC_KEY;
    }

    public static String getPublicKeyString() {
        return "-----BEGIN PUBLIC KEY-----&&&"
                + Base64.encodeToString(PUBLIC_KEY.getEncoded(), Base64.DEFAULT).replace("\n", "&&&")
                + "&&&-----END PUBLIC KEY-----";
    }

    public static PrivateKey getPrivateKey() {
        return PRIVATE_KEY;
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
