package com.android.smartlock.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.android.smartlock.Constants;
import com.android.smartlock.CustomViews.Lock;
import com.android.smartlock.Internet.*;
import com.android.smartlock.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements AsyncTaskListener {

    static MainActivity instance;
    Lock lockButton;
    ScheduledExecutorService mScheduler;
    PiPinger mPiPinger = new PiPinger(this);
    View registerButton;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Constants.init(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lockButton = (Lock) findViewById(R.id.lockButton);
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lockButton.isLocked()) {
                    new UnlockDoor(MainActivity.this).execute();
                } else {
                    new LockDoor(MainActivity.this).execute();
                }
            }
        });
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new GCMRegister(MainActivity.this.getApplicationContext()).execute();
            }
        });
    }

    @Override
    protected void onResume() {
        instance = this;
        scheduleLocator();
        super.onResume();
    }

    @Override
    protected void onPause() {
        instance = null;
        pauseLocator();
        super.onPause();
    }

    public void setLock(boolean status) {
        lockButton.setLocked(status);
    }

    private void scheduleLocator() {
        mScheduler = Executors.newScheduledThreadPool(1);
        mScheduler.scheduleAtFixedRate(mPiPinger, 0, 5, TimeUnit.SECONDS);
    }

    private void pauseLocator() {
        mScheduler.shutdown();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settingsButton) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAsyncTaskCompleted() {
        Log.d("Main", "Triggered!");
        if (mPiPinger.isPiVisible()) {
            lockButton.setEnabled(Constants.isApproved());
            if (Constants.isApproved()) {
                lockButton.setLocked(mPiPinger.isLocked());
                registerButton.setVisibility(View.GONE);
            } else {
                lockButton.setLocked(true);
                lockButton.setDisabledColor(0xff1565C0);
                registerButton.setVisibility(View.VISIBLE);
            }
        } else {
            lockButton.setLocked(true);
            lockButton.setDisabledColor(0xff9e9e9e);
            lockButton.setEnabled(false);
            registerButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAsyncTaskProgressUpdate(int value) {
        throw new UnsupportedOperationException("onAsyncTaskProgressUpdate in MainActivity not implemented");
    }

}
