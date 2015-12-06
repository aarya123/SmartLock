package com.android.smartlock.Internet;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.android.smartlock.Constants;

public class LockDoor extends AsyncTask<String, String, String> {
    Context context;

    public LockDoor(Context context) {
        this.context = context;
    }

    protected String doInBackground(String... strings) {
        String result = new Internet(Constants.getIPAddress(), "lock_door", "true", "uid", Constants.getDeviceId()).getResult();
        Log.d("LockDoor", result);
        return result;
    }
}
