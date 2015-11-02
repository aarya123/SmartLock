package com.android.smartlock.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import com.android.smartlock.Constants;
import com.android.smartlock.R;

public class SettingsActivity extends Activity {
    EditText ipEditText;
    EditText portEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ipEditText = (EditText) findViewById(R.id.ipEditText);
        ipEditText.setText(Constants.getIPAdress());
        portEditText = (EditText) findViewById(R.id.portEditText);
        portEditText.setText(Constants.getPort() + "");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.saveButton) {
            Constants.setIpAddress(ipEditText.getText().toString());
            Constants.setPort(Integer.parseInt(portEditText.getText().toString()));
            finish();
            return true;
        } else if (id == R.id.discardButton) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
