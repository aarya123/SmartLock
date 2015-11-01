package com.android.smartlock;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    Button pingButton;
    EditText serverAddress;
    TextView output;
    Lock lockButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pingButton = (Button) findViewById(R.id.button_ping);
        serverAddress = (EditText) findViewById(R.id.editText_address);
        serverAddress.setText(Constants.IP);
        output = (TextView) findViewById(R.id.textView_output);
        lockButton = (Lock) findViewById(R.id.lockButton);

        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.IP = serverAddress.getText().toString();
                if (lockButton.isLocked()) {
                    new LockDoor(MainActivity.this).execute();
                } else {
                    new LockDoor(MainActivity.this).execute();
                }
            }
        });

        pingButton.setOnClickListener(new View.OnClickListener() {
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
}
