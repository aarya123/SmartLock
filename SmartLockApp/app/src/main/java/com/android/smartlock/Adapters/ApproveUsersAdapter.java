package com.android.smartlock.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.android.smartlock.Internet.ApproveUser;
import com.android.smartlock.R;

import java.util.List;

/**
 * Created by Anubhaw Arya on 12/6/15.
 */
public class ApproveUsersAdapter extends ArrayAdapter<Integer> {
    List<Integer> uidList;
    ViewHolder holder;

    public ApproveUsersAdapter(Context context, List<Integer> items) {
        super(context, R.layout.approve_user_cell, items);
        uidList = items;
    }

    public void removeIndex(int index) {
        uidList.remove(index);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.approve_user_cell, parent);
            holder = new ViewHolder();
            holder.uidTextView = (TextView) convertView.findViewById(R.id.uidTextView);
            holder.approveUserButton = (Button) convertView.findViewById(R.id.approveUserButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.uidTextView.setText(getItem(position) + "");
        holder.approveUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ApproveUser(ApproveUsersAdapter.this).execute(position);
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView uidTextView;
        Button approveUserButton;
    }
}
