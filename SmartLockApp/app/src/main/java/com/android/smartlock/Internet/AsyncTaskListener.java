package com.android.smartlock.Internet;

public interface AsyncTaskListener {
    void onAsyncTaskCompleted();

    void onAsyncTaskProgressUpdate(int value);
}
