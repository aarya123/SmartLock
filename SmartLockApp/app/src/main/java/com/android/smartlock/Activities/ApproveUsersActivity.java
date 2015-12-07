package com.android.smartlock.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import com.android.smartlock.Adapters.ApproveUsersAdapter;
import com.android.smartlock.Internet.AsyncTaskListener;
import com.android.smartlock.Internet.GetUsers;
import com.android.smartlock.R;

import java.util.ArrayList;

public class ApproveUsersActivity extends AppCompatActivity implements AsyncTaskListener {
    ListView approveUsersListView;
    ApproveUsersAdapter adapter;
    GetUsers getUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_users);
        approveUsersListView = (ListView) findViewById(R.id.approveUsersListView);
        adapter = new ApproveUsersAdapter(this, new ArrayList<String>());
        approveUsersListView.setAdapter(adapter);
        getUsers = new GetUsers(this);
        getUsers.execute();
    }

    @Override
    public void onAsyncTaskCompleted() {
        adapter.clear();
        adapter.addAll(getUsers.getResult());
    }

    @Override
    public void onAsyncTaskProgressUpdate(int value) {

    }
}
