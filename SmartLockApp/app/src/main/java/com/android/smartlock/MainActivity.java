package com.android.smartlock;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    Button pingButton;
    EditText serverAddress;
    TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pingButton = (Button) findViewById(R.id.button_ping);
        serverAddress = (EditText) findViewById(R.id.editText_address);
        serverAddress.setText(Constants.IP);
        output = (TextView) findViewById(R.id.textView_output);

        pingButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.IP = serverAddress.getText().toString();

                String msg = "TCP Send: " + Constants.IP;

                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                GCMRegister reg = new GCMRegister(MainActivity.this);
                reg.execute();
            }
        });
    }

    public void onMessageReceived(String from, Bundle data) {
        String TAG = "onMessageReceived";
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        output.setText(output.getText() + "\n" + message);
    }
}
