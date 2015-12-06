package com.android.smartlock.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import com.android.smartlock.Adapters.ApproveUsersAdapter;
import com.android.smartlock.Internet.AsyncTaskListener;
import com.android.smartlock.Internet.GetPendingUsers;
import com.android.smartlock.R;

import java.util.ArrayList;

public class ApproveUsersActivity extends AppCompatActivity implements AsyncTaskListener {
    ListView approveUsersListView;
    ApproveUsersAdapter adapter;
    GetPendingUsers getPendingUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_users);
        approveUsersListView = (ListView) findViewById(R.id.approveUsersListView);
        adapter = new ApproveUsersAdapter(this, new ArrayList<Integer>());
        approveUsersListView.setAdapter(adapter);
        getPendingUsers = new GetPendingUsers(this);
        getPendingUsers.execute();
    }

    @Override
    public void onAsyncTaskCompleted() {
        adapter.clear();
        adapter.addAll(getPendingUsers.getResult());
    }

    @Override
    public void onAsyncTaskProgressUpdate(int value) {

    }
}
