package com.android.smartlock.Internet;

import com.android.smartlock.Constants;

public class PiPinger implements Runnable {
    AsyncTaskListener mAsyncTaskListener;
    private boolean mPiVisible = true;
    private boolean lockStatus = false;

    public PiPinger(AsyncTaskListener mAsyncTaskListener) {
        this.mAsyncTaskListener = mAsyncTaskListener;
    }

    @Override
    public void run() {
        boolean result;
        boolean viewNeedsUpdating = false;
        try {
            String[] response = new Internet(Constants.getIPAdress(), "ping", "true").getResult().split("\n");
            result = response[0].contains("pong");
            boolean lock = Boolean.parseBoolean(response[1].split("=")[1]);
            if (lock != lockStatus) {
                viewNeedsUpdating = true;
            }
            lockStatus = lock;
        } catch (Exception e) {
            result = false;
        }
        if (result != mPiVisible) {
            viewNeedsUpdating = true;
        }
        mPiVisible = result;
        if (viewNeedsUpdating) {
            mAsyncTaskListener.onAsyncTaskCompleted();
        }
    }

    public boolean isPiVisible() {
        return mPiVisible;
    }

    public boolean isLocked() {
        return lockStatus;
    }
}
