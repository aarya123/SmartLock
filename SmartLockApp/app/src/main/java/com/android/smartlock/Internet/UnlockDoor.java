package com.android.smartlock.Internet;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.android.smartlock.Constants;

public class UnlockDoor extends AsyncTask<String, String, String> {
    Context context;

    public UnlockDoor(Context context) {
        this.context = context;
    }

    protected String doInBackground(String... strings) {
        String result = new Internet(Constants.getIPAdress(), "unlock_door", "true", "uid", Constants.getDeviceId()).getResult();
        Log.d("UnlockDoor", result);
        return result;
    }
}
