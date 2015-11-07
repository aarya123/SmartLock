package com.android.smartlock.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.android.smartlock.Constants;
import com.android.smartlock.Internet.AsyncTaskListener;
import com.android.smartlock.Internet.LocatePi;
import com.android.smartlock.R;

public class SettingsActivity extends AppCompatActivity implements AsyncTaskListener {
    EditText ipEditText, portEditText, timeoutEditText;
    Button searchButton;
    ProgressBar ipSearchProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ipEditText = (EditText) findViewById(R.id.ipEditText);
        ipEditText.setText(Constants.getIPAdress());
        portEditText = (EditText) findViewById(R.id.portEditText);
        portEditText.setText(Constants.getPort() + "");
        timeoutEditText = (EditText) findViewById(R.id.timeoutEditText);
        timeoutEditText.setText(Constants.getTimeout() + "");
        searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchButton.setVisibility(View.GONE);
                ipSearchProgress.setProgress(0);
                ipSearchProgress.setVisibility(View.VISIBLE);
                new LocatePi(SettingsActivity.this, SettingsActivity.this).execute();
            }
        });
        ipSearchProgress = (ProgressBar) findViewById(R.id.ipSearchProgress);
    }

    @Override
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
            Constants.setTimeout(Integer.parseInt(timeoutEditText.getText().toString()));
            finish();
            return true;
        } else if (id == R.id.discardButton) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAsyncTaskCompleted() {
        searchButton.setVisibility(View.VISIBLE);
        ipSearchProgress.setVisibility(View.GONE);
        ipEditText.setText(Constants.getIPAdress());
    }

    @Override
    public void onAsyncTaskProgressUpdate(int value) {
        ipSearchProgress.setProgress(value);
    }
}
