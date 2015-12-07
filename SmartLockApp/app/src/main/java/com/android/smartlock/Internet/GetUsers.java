package com.android.smartlock.Internet;

import android.os.AsyncTask;
import com.android.smartlock.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anubhaw Arya on 12/6/15.
 */
public class GetUsers extends AsyncTask<String, String, ArrayList<String>> {
    ArrayList<String> result = new ArrayList<String>();
    AsyncTaskListener listener;

    public GetUsers(AsyncTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        String[] response = new Internet(Constants.getIPAddress(), "getusers", "true", "uid", Constants.getDeviceId()).getResult().split(",");
        for (String id : response) {
            if (!id.equals("") && !id.contains(Constants.getDeviceId())) {
                result.add(id);
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<String> s) {
        listener.onAsyncTaskCompleted();
    }

    public List<String> getResult() {
        return result;
    }
}
