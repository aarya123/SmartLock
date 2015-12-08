package com.android.smartlock.Internet;

import com.android.smartlock.Constants;

public class PiPinger implements Runnable {
    AsyncTaskListener mAsyncTaskListener;
    private boolean mPiVisible = false;
    private boolean lockStatus = true;

    public PiPinger(AsyncTaskListener mAsyncTaskListener) {
        this.mAsyncTaskListener = mAsyncTaskListener;
    }

    @Override
    public void run() {
        try {
            String[] response = new Internet(Constants.getIPAddress(), "ping", "true", "uid", Constants.getDeviceId()).getResult().split("\n");
            mPiVisible = response[0].contains("pong");
            lockStatus = !response[1].split("=")[1].equals("0");
            boolean approved = response[2].split("=")[1].equals("1");
            if (approved != Constants.isApproved()) {
                Constants.setApproved(approved);
            }
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
