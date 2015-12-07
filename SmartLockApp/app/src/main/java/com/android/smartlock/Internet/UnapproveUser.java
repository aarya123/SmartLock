package com.android.smartlock.Internet;

import android.os.AsyncTask;
import com.android.smartlock.Adapters.ApproveUsersAdapter;
import com.android.smartlock.Constants;

/**
 * Created by Anubhaw Arya on 12/6/15.
 */
public class UnapproveUser extends AsyncTask<Integer, String, Integer> {
    ApproveUsersAdapter adapter;

    public UnapproveUser(ApproveUsersAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        if (new Internet(Constants.getIPAddress(), "unapprove", "" + adapter.getItem(params[0]).split(":")[0], "uid", Constants.getDeviceId()).getResult().contains("success")) {
            return params[0];
        }
        return -1;
    }

    @Override
    protected void onPostExecute(Integer s) {
        if (s != -1) {
            adapter.setIndex(s, adapter.getItem(s).split(":")[0] + ":0");
            adapter.notifyDataSetChanged();
        }
    }
}
