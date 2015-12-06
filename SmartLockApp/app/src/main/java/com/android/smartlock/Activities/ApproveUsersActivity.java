package com.android.smartlock.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import com.android.smartlock.R;

public class ApproveUsersActivity extends AppCompatActivity {
    ListView approveUsersListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_users);
        approveUsersListView = (ListView) findViewById(R.id.approveUsersListView);
    }
}
