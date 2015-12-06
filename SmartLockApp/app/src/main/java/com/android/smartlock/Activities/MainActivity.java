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
import com.android.smartlock.Internet.AsyncTaskListener;
import com.android.smartlock.Internet.LockDoor;
import com.android.smartlock.Internet.PiPinger;
import com.android.smartlock.Internet.UnlockDoor;
import com.android.smartlock.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements AsyncTaskListener {

    Lock lockButton;
    ScheduledExecutorService mScheduler;
    PiPinger mPiPinger = new PiPinger(this);

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

    }

    @Override
    protected void onResume() {
        scheduleLocator();
        super.onResume();
    }

    @Override
    protected void onPause() {
        pauseLocator();
        super.onPause();
    }

    private void scheduleLocator() {
        mScheduler = Executors.newScheduledThreadPool(1);
        mScheduler.scheduleAtFixedRate(mPiPinger, 0, 10, TimeUnit.SECONDS);
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
            lockButton.setLocked(mPiPinger.isLocked());
            lockButton.setEnabled(Constants.isApproved());
        } else {
            lockButton.setEnabled(false);
        }
    }

    @Override
    public void onAsyncTaskProgressUpdate(int value) {
        throw new UnsupportedOperationException("onAsyncTaskProgressUpdate in MainActivity not implemented");
    }

}
