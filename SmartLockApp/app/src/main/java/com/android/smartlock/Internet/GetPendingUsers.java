package com.android.smartlock.Internet;

import android.os.AsyncTask;
import com.android.smartlock.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anubhaw Arya on 12/6/15.
 */
public class GetPendingUsers extends AsyncTask<String, String, String> {
    ArrayList<Integer> result = new ArrayList<Integer>();
    AsyncTaskListener listener;

    public GetPendingUsers(AsyncTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        String[] response = new Internet(Constants.getIPAddress(), "getunapproved", "true", "uid", Constants.getDeviceId()).getResult().split(",");

        for (String id : response) {
            if (!id.equals("")) {
                result.add(Integer.valueOf(id));
            }
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onAsyncTaskCompleted();
    }

    public List<Integer> getResult() {
        return result;
    }
}
