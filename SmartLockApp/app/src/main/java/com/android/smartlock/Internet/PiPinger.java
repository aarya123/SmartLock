package com.android.smartlock.Internet;

import com.android.smartlock.Constants;

public class PiPinger implements Runnable {
    AsyncTaskListener mAsyncTaskListener;
    private boolean mPiVisible;
    private boolean lockStatus;

    public PiPinger(AsyncTaskListener mAsyncTaskListener) {
        this.mAsyncTaskListener = mAsyncTaskListener;
    }

    @Override
    public void run() {
        try {
            String[] response = new Internet(Constants.getIPAddress(), "ping", "true").getResult().split("\n");
            mPiVisible = response[0].contains("pong");
            lockStatus = !response[1].split("=")[1].equals("0");
        } catch (Exception e) {
            mPiVisible = false;
        }
        mAsyncTaskListener.onAsyncTaskCompleted();
    }

    public boolean isPiVisible() {
        return mPiVisible;
    }

    public boolean isLocked() {
        return lockStatus;
    }
}
