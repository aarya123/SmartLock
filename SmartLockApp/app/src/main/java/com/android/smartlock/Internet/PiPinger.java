package com.android.smartlock.Internet;

import com.android.smartlock.Constants;

public class PiPinger implements Runnable {
    AsyncTaskListener mAsyncTaskListener;
    private boolean mPiVisible = true;

    public PiPinger(AsyncTaskListener mAsyncTaskListener) {
        this.mAsyncTaskListener = mAsyncTaskListener;
    }

    @Override
    public void run() {
        boolean result;
        try {
            result = new Internet(Constants.getIPAdress(), "ping", "true").getResult().contains("pong");
        } catch (Exception e) {
            result = false;
        }
        setIsVisible(result);
    }

    private void setIsVisible(boolean visible) {
        boolean viewNeedsUpdating = mPiVisible != visible;
        mPiVisible = visible;
        if (viewNeedsUpdating) {
            mAsyncTaskListener.onAsyncTaskCompleted();
        }
    }

    public boolean isPiVisible() {
        return mPiVisible;
    }
}
