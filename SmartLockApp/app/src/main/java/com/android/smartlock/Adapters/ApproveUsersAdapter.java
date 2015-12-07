package com.android.smartlock.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.android.smartlock.Internet.ApproveUser;
import com.android.smartlock.Internet.UnapproveUser;
import com.android.smartlock.R;

import java.util.List;

/**
 * Created by Anubhaw Arya on 12/6/15.
 */
public class ApproveUsersAdapter extends ArrayAdapter<String> {
    List<String> uidList;
    ViewHolder holder;

    public ApproveUsersAdapter(Context context, List<String> items) {
        super(context, R.layout.approve_user_cell, items);
        uidList = items;
    }

    public void setIndex(int index, String s) {
        uidList.set(index, s);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.approve_user_cell, parent, false);
            holder = new ViewHolder();
            holder.uidTextView = (TextView) convertView.findViewById(R.id.uidTextView);
            holder.approveUserButton = (Button) convertView.findViewById(R.id.approveUserButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String[] data = getItem(position).split(":");
        holder.uidTextView.setText(data[0]);
        if (data[1].equals("0")) {
            holder.approveUserButton.setText("Approve");
            holder.approveUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ApproveUser(ApproveUsersAdapter.this).execute(position);
                }
            });
        } else {
            holder.approveUserButton.setText("Unapprove");
            holder.approveUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UnapproveUser(ApproveUsersAdapter.this).execute(position);
                }
            });
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView uidTextView;
        Button approveUserButton;
    }
}
