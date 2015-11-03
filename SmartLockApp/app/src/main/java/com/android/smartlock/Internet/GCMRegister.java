package com.android.smartlock.Internet;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.android.smartlock.Constants;
import com.android.smartlock.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class GCMRegister extends AsyncTask<String, String, String> {

    private Context context;

    public GCMRegister(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String TAG = "registergcm";
        String output = "no response";
        try {
            InstanceID instanceID = InstanceID.getInstance(context);
            String token = instanceID.getToken(context.getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);
            Internet gcmsend = new Internet(Constants.getIPAdress(), "register", token);
            Log.d(TAG, "Assigned id is " + gcmsend.getResult());
            Constants.setDeviceId(gcmsend.getResult().split("=")[1]);
        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);
        }
        return output;
    }
}
