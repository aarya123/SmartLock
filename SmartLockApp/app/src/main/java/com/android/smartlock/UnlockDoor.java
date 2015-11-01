package com.android.smartlock;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class UnlockDoor extends AsyncTask<String, String, String> {
    Context context;

    public UnlockDoor(Context context) {
        this.context = context;
    }

    protected String doInBackground(String... strings) {
        String uid = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.DEVICE_ID_KEY, "-1");
        String result = new Internet("unlock_door", "true", "uid", uid).getResult();
        Log.d("UnlockDoor", result);
        return result;
    }
}
